package com.diboot.core.data.annotation;

import com.diboot.core.data.protect.SensitiveInfoSerialize;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import tools.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveInfoSerialize.class)
public @interface DataMask {
}
