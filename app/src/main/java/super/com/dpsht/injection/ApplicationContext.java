package javasign.com.dompetsehat.injection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by pratama on 3/11/16.
 */
@Qualifier @Retention(RetentionPolicy.RUNTIME) public @interface ApplicationContext {
}
