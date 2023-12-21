package com.showy.utils.annotations;

import com.showy.utils.annotations.impl.ConditionalOnInstanceImpl;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionalOnInstanceImpl.class)
public @interface ConditionalOnInstance {
    String value();
}
