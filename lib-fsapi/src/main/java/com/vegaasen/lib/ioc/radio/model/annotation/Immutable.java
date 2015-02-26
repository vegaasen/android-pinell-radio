package com.vegaasen.lib.ioc.radio.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ..what..
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Immutable {
}
