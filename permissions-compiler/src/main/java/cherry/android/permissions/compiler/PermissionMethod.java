package cherry.android.permissions.compiler;

import com.squareup.javapoet.CodeBlock;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

/**
 * Created by Administrator on 2017/5/15.
 */

public class PermissionMethod {

    private ExecutableElement mElement;
    private int[] mPermissionRequestCode;

    public PermissionMethod(Element element, Class<? extends Annotation> annotationClass) {
        if (element.getKind() != ElementKind.METHOD)
            throw new IllegalArgumentException(String.format("%s should apply on method", annotationClass.getSimpleName()));
        this.mElement = (ExecutableElement) element;
        Annotation annotation = mElement.getAnnotation(annotationClass);

        try {
            Method method = annotationClass.getDeclaredMethod("value");
            mPermissionRequestCode = (int[]) method.invoke(annotation);
            if (distinct()) {
                throw new RuntimeException(String.format("Method %s with @%s contains same requestCode",
                        getMethodName(),
                        annotationClass.getSimpleName()));
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String getMethodName() {
        return mElement.getSimpleName().toString();
    }

    public int[] getPermissionRequestCodes() {
        return mPermissionRequestCode;
    }

    private boolean distinct() {
        if (mPermissionRequestCode.length <= 1)
            return false;
        BitSet bitSet = new BitSet(1);
        for (int requestCode : mPermissionRequestCode) {
            if (bitSet.get(requestCode)) {
                return true;
            }
            bitSet.set(requestCode);
        }
        return false;
    }

    public CodeBlock generateCode() {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        for (int requestCode : mPermissionRequestCode) {
            codeBuilder.add("case $L:\n", requestCode);
        }
        List<? extends VariableElement> parameters = mElement.getParameters();
        if (parameters.size() > 1) {
            throw new IllegalArgumentException(String.format("parameter outOfBounds %s", getMethodName()));
        }
        codeBuilder.add("target.$N(", getMethodName());
        if (parameters.size() != 0) {
            VariableElement param = parameters.get(0);
            if (param.asType().getKind() != TypeKind.INT) {
                throw new IllegalArgumentException("parameter must be int");
            }
            codeBuilder.add("requestCode");
        }
        codeBuilder.add(");\n")
                .add("break;\n");
        return codeBuilder.build();
    }
}
