package cherry.android.permissions.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import cherry.android.permissions.annotations.PermissionDenied;
import cherry.android.permissions.annotations.PermissionGranted;
import cherry.android.permissions.annotations.PermissionNeverAskAgain;

/**
 * Created by Administrator on 2017/5/15.
 */

public class PermissionClass {

    private Elements mElementUtils;
    private TypeElement mClassElement;

    private List<PermissionMethod> mGrantedMethodList;
    private List<PermissionMethod> mDeniedMethodList;
    private List<PermissionMethod> mRationalMethodList;

    public PermissionClass(Elements elementUtils, TypeElement element) {
        this.mElementUtils = elementUtils;
        this.mClassElement = element;
        mGrantedMethodList = new ArrayList<>();
        mDeniedMethodList = new ArrayList<>();
        mRationalMethodList = new ArrayList<>();
    }

    public void addPermissionGrantedMethod(PermissionMethod method) {
        if (distinct(method, mGrantedMethodList)) {
            throw new IllegalArgumentException(logMessage(method.getMethodName(), PermissionGranted.class.getSimpleName()));
        }
        mGrantedMethodList.add(method);
    }

    public void addPermissionDeniedMethod(PermissionMethod method) {
        if (distinct(method, mDeniedMethodList)) {
            throw new IllegalArgumentException(logMessage(method.getMethodName(), PermissionDenied.class.getSimpleName()));
        }
        mDeniedMethodList.add(method);
    }

    public void addPermissionRationalMethod(PermissionMethod method) {
        if (distinct(method, mRationalMethodList)) {
            throw new IllegalArgumentException(logMessage(method.getMethodName(), PermissionNeverAskAgain.class.getSimpleName()));
        }
        mRationalMethodList.add(method);
    }

    private String logMessage(String methodName, String annotationName) {
        StringBuilder builder = new StringBuilder();
        builder.append("RequestCode already used.")
                .append("The method ")
                .append(methodName)
                .append("() in class: ")
                .append(getClassName())
                .append(" Annotation with @")
                .append(annotationName);
        return builder.toString();
    }

    private static boolean distinct(final PermissionMethod method,
                                    List<PermissionMethod> methodList) {
        return methodList.stream()
                .flatMapToInt(method1 -> IntStream.of(method1.getPermissionRequestCodes()))
                .anyMatch(value -> IntStream.of(method.getPermissionRequestCodes())
                        .anyMatch(value2 -> value == value2));
    }

    private String getPackageName() {
        return mElementUtils.getPackageOf(mClassElement).getQualifiedName().toString();
    }

    private String getClassName() {
        String packageName = getPackageName();
        String fullClassName = getFullClassName();
        int packageLen = packageName.length() + 1;
        return fullClassName.substring(packageLen).replace('.', '$');
    }

    private String getFullClassName() {
        return mClassElement.getQualifiedName().toString();
    }

    private TypeName getTypeName() {
        return TypeName.get(mClassElement.asType());
    }

    public JavaFile generateFile() {
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(getClassName() + "_Permissions")
                .addSuperinterface(ACTION_CLASS)
                .addModifiers(Modifier.PUBLIC)
                .addField(getTypeName(), "target", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(buildConstructorMethod())
                .addMethod(buildPermissionGrantedMethod())
                .addMethod(buildPermissionDeniedMethod())
                .addMethod(buildShouldPermissionRationalMethod())
                .addMethod(buildShowPermissionRationalMethod());

        if (!mRationalMethodList.isEmpty()) {
            typeBuilder.addField(BitSet.class, "bitSet", Modifier.PRIVATE);
        }
        return JavaFile.builder(getPackageName(), typeBuilder.build()).build();
    }

    private MethodSpec buildConstructorMethod() {
        MethodSpec.Builder method = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(getTypeName(), "target", Modifier.FINAL)
                .addStatement("this.target = target");
        if (!mRationalMethodList.isEmpty()) {
            method.addStatement("this.bitSet = new $T(1)", BitSet.class);
            BitSet bitSet = new BitSet(1);
            for (PermissionMethod m : mRationalMethodList) {
                for (int requestCode : m.getPermissionRequestCodes()) {
                    if (bitSet.get(requestCode))
                        continue;
                    bitSet.set(requestCode);
                    method.addStatement("this.bitSet.set($L)", requestCode);
                }
            }
        }
        return method.build();
    }

    private MethodSpec buildPermissionGrantedMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("permissionGranted")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "requestCode")
                .addAnnotation(Override.class);
        if (!mGrantedMethodList.isEmpty()) {
            method.beginControlFlow("switch(requestCode)");
            for (PermissionMethod permissionMethod : mGrantedMethodList) {
                method.addCode(permissionMethod.generateCode());
            }
            method.endControlFlow();
        }
        return method.build();
    }

    private MethodSpec buildPermissionDeniedMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("permissionDenied")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "requestCode")
                .addAnnotation(Override.class);
        method.beginControlFlow("switch(requestCode)");
        if (!mDeniedMethodList.isEmpty()) {
            for (PermissionMethod permissionMethod : mDeniedMethodList) {
                method.addCode(permissionMethod.generateCode());
            }
            method.endControlFlow();
        }
        return method.build();
    }

    private MethodSpec buildShowPermissionRationalMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("showPermissionRationale")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "requestCode")
                .addAnnotation(Override.class);
        if (mRationalMethodList.size() > 0) {
            method.beginControlFlow("switch(requestCode)");
            for (PermissionMethod permissionMethod : mRationalMethodList) {
                method.addCode(permissionMethod.generateCode());
            }
            method.endControlFlow();
        }
        return method.build();
    }

    private MethodSpec buildShouldPermissionRationalMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("shouldPermissionRationale")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "requestCode")
                .addAnnotation(Override.class)
                .returns(TypeName.BOOLEAN);
        if (mRationalMethodList.size() > 0) {
            method.addStatement("return this.bitSet.get(requestCode)");
        } else {
            method.addStatement("return false");
        }
        return method.build();
    }

    private MethodSpec buildUpdateTargetMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("updateTarget")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.OBJECT, "target")
                .addAnnotation(Override.class)
                .addStatement("this.target = $T.castTarget(target, $T.class)", PERMISSIONS_CLASS, getTypeName());
        return method.build();
    }

    private static final ClassName ACTION_CLASS = ClassName.get("cherry.android.permissions.api.internal", "Action");
    private static final ClassName PERMISSIONS_CLASS = ClassName.get("cherry.android.permissions.api.internal", "PermissionUtils");
}
