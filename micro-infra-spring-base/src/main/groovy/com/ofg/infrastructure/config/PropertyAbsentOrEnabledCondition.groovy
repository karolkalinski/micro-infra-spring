package com.ofg.infrastructure.config

import groovy.transform.CompileStatic
import org.apache.commons.lang.BooleanUtils
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

@CompileStatic
abstract class PropertyAbsentOrEnabledCondition implements Condition {

    private final String filteringProperty

    PropertyAbsentOrEnabledCondition(String filteringProperty) {
        this.filteringProperty = filteringProperty
    }

    @Override
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return switchNotDefined(context) || switchIsEnabled(context)
    }

    private boolean switchNotDefined(ConditionContext context) {
        return !context.environment.containsProperty(filteringProperty)
    }

    private boolean switchIsEnabled(ConditionContext context) {
        String propertyEnabled = context.environment.getProperty(filteringProperty).toLowerCase()
        return BooleanUtils.toBoolean(propertyEnabled)
    }
}
