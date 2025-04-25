package us.zonix.hcfactions.util.command;

import us.zonix.core.rank.Rank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command Framework - Command <br>
 * The command annotation used to designate methods as commands. All methods
 * should have a single CommandArgs argument
 * 
 * @author minnymin3
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	public String name();
	public Rank permission() default Rank.DEFAULT;
	public String[] aliases() default {};
	public String description() default "";
	public String usage() default "";
	public boolean inGameOnly() default true;
	public boolean inFactionOnly() default false;
	public boolean isLeaderOnly() default false;
	public boolean isOfficerOnly() default false;
}
