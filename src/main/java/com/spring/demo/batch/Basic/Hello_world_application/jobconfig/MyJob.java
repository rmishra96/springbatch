package com.spring.demo.batch.Basic.Hello_world_application.jobconfig;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Qualifier("myjob")
public @interface MyJob {
}
