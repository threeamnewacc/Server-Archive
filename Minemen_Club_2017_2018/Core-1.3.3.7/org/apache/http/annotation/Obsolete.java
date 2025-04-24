// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

@Documented
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface Obsolete {
}
