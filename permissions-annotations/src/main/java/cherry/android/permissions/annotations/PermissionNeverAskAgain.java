package cherry.android.permissions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/5/18.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface PermissionNeverAskAgain {
    int[] value();
}
