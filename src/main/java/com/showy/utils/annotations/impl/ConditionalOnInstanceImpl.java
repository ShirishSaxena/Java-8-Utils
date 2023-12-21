package com.showy.utils.annotations.impl;

import com.showy.utils.annotations.ConditionalOnInstance;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Optional;

import static com.showy.utils.constant.GlobalConstant.*;

public class ConditionalOnInstanceImpl implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String property = conditionContext.getEnvironment().getProperty(PROPERTY_FIELD_FOR_SCHEDULER);

        Optional<Object> valueObj = annotatedTypeMetadata.getAnnotations()
                .get(ConditionalOnInstance.class).getValue("value");
        String value = valueObj.orElse("").toString();

        if (property == null || "all".equals(property)) {
            return true;
        }

        switch (value) {
            case SCHEDULER:
                return "true".equals(property);
            case NO_SCHEDULER:
                return !"true".equals(property);
        }
        return false;
    }
}
