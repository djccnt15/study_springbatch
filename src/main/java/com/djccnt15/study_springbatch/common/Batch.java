package com.djccnt15.study_springbatch.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Configuration
public @interface Batch {
    
    @AliasFor(annotation = Configuration.class)
    String value() default "";
}
