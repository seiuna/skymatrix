package cn.seiua.skymatrix.client.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SModule {
    String name();

    String category();

    boolean disable() default false;


    String[] require() default "";
}
