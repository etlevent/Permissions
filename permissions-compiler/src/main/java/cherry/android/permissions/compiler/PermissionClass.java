package cherry.android.permissions.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by Administrator on 2017/5/15.
 */

public class PermissionClass {

    private Elements mElementUtils;
    private TypeElement mClassElement;

    private List<PermissionMethod> mGrantedMethodList;
    private List<PermissionMethod> mDeniedMethodList;

    public PermissionClass(Elements elementUtils, TypeElement element) {
        this.mElementUtils = elementUtils;
        this.mClassElement = element;
        mGrantedMethodList = new ArrayList<>();
        mDeniedMethodList = new ArrayList<>();
    }

    public void addPermissionGrantedMethod(PermissionMethod method) {
        mGrantedMethodList.add(method);
    }

    public void addPermissionDeniedMethod(PermissionMethod method) {
        mDeniedMethodList.add(method);
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
                .addField(getTypeName(), "target", Modifier.PRIVATE)
                .addMethod(buildConstructorMethod())
                .addMethod(buildPermissionGrantedMethod())
                .addMethod(buildPermissionDeniedMethod());


        return JavaFile.builder(getPackageName(), typeBuilder.build()).build();
    }

    private MethodSpec buildConstructorMethod() {
        MethodSpec.Builder method = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(getTypeName(), "target", Modifier.FINAL)
                .addStatement("this.target = target");
        return method.build();
    }

    private MethodSpec buildPermissionGrantedMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("permissionGranted")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "requestCode")
                .addAnnotation(Override.class);
        if (mGrantedMethodList.size() > 0) {
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
        if (mDeniedMethodList.size() > 0) {
            for (PermissionMethod permissionMethod : mDeniedMethodList) {
                method.addCode(permissionMethod.generateCode());
            }
            method.endControlFlow();
        }
        return method.build();
    }

    private static final ClassName ACTION_CLASS = ClassName.get("cherry.android.permissions.api", "Action");
    private static final ClassName PERMISSIONS_CLASS = ClassName.get("cherry.android.permissions.api", "Permissions");
}
