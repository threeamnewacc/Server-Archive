package me.devkevin.landcore.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 10:27
 * RedisHandler / land.pvp.core.redis.annotation / LandCore
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisHandler {
    String value();
}
