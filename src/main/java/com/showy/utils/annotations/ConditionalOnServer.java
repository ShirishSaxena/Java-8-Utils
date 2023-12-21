package com.showy.utils.annotations;

import com.showy.utils.annotations.impl.ConditionalOnServerImpl;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionalOnServerImpl.class)
public @interface ConditionalOnServer {
    String value();
}
