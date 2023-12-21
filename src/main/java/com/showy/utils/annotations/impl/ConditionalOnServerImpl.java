package com.showy.utils.annotations.impl;

import com.showy.utils.annotations.ConditionalOnServer;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Optional;

import static com.showy.utils.constant.GlobalConstant.PROPERTY_FIELD_FOR_SERVER_LOCATION;

public class ConditionalOnServerImpl implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String property = conditionContext.getEnvironment().getProperty(PROPERTY_FIELD_FOR_SERVER_LOCATION);

        Optional<Object> valueObj = annotatedTypeMetadata.getAnnotations()
                .get(ConditionalOnServer.class).getValue("value");
        return valueObj.orElse("").toString().equals(property);
    }
}
