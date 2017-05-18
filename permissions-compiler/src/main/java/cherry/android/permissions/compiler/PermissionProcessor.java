package cherry.android.permissions.compiler;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import cherry.android.permissions.annotations.PermissionDenied;
import cherry.android.permissions.annotations.PermissionGranted;
import cherry.android.permissions.annotations.PermissionNeverAskAgain;

@AutoService(Processor.class)
public class PermissionProcessor extends AbstractProcessor {

    private Elements mElementUtils;
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            annotationTypes.add(annotation.getCanonicalName());
        }
        return annotationTypes;
    }

    public static Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new HashSet<>();
        annotations.add(PermissionGranted.class);
        annotations.add(PermissionDenied.class);
        annotations.add(PermissionNeverAskAgain.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            findAndParseTargets(roundEnv);
        }
        return false;
    }

    private void findAndParseTargets(RoundEnvironment roundEnv) {
        Map<String, PermissionClass> permissionClassMap = new HashMap<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(PermissionGranted.class)) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            PermissionClass permission = getPermissionClass(permissionClassMap, enclosingElement);
            PermissionMethod method = new PermissionMethod(element, PermissionGranted.class);
            permission.addPermissionGrantedMethod(method);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(PermissionDenied.class)) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            PermissionClass permission = getPermissionClass(permissionClassMap, enclosingElement);
            PermissionMethod method = new PermissionMethod(element, PermissionDenied.class);
            permission.addPermissionDeniedMethod(method);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(PermissionNeverAskAgain.class)) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            PermissionClass permission = getPermissionClass(permissionClassMap, enclosingElement);
            PermissionMethod method = new PermissionMethod(element, PermissionNeverAskAgain.class);
            permission.addPermissionRationalMethod(method);
        }

        try {
            for (PermissionClass permission : permissionClassMap.values()) {
                permission.generateFile().writeTo(mFiler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PermissionClass getPermissionClass(Map<String, PermissionClass> map, TypeElement enclosingElement) {
        String className = enclosingElement.getQualifiedName().toString();
        PermissionClass permission = map.get(className);
        if (permission == null) {
            permission = new PermissionClass(mElementUtils, enclosingElement);
            map.put(className, permission);
        }
        return permission;
    }
}
