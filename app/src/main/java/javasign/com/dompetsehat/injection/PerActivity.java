package javasign.com.dompetsehat.injection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by pratama on 3/11/16.
 */
@Scope @Retention(RetentionPolicy.RUNTIME) public @interface PerActivity {
}
