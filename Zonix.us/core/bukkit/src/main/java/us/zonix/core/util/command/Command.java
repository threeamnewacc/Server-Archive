package us.zonix.core.util.command;

import us.zonix.core.rank.Rank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String name();

    String[] aliases() default {};

    String description() default "";

    String usage() default "";

    Rank rank() default Rank.DEFAULT;

    boolean requiresPlayer() default false;

}
