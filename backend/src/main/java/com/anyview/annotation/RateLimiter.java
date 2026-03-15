package com.anyview.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    String key() default "";

    int limit() default 100;

    long timeout() default 1;

    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
