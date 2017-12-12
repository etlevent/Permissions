package cherry.android.permissions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/5/17.
 * <p>
 * warning: don't request permission in component lifecycle callback(onResume onPause).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface RequestPermission {
    String[] value();

    int requestCode();
}
