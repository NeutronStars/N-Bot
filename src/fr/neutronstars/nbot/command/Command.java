package fr.neutronstars.nbot.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author NeutronStars
 * @version 1.1.3
 * @since 1.0.0
 */

@Documented
@Target(value=ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	/**
	 * Name of the Command
	 * @return String
	 * @since 1.0.0
	 */
	public String name();
	
	/**
	 * Description of the Command
	 * @return String
	 * @since 1.0.0
	 */
	public String description() default "hasn't description.";
	
	/**
	 * Executor type of the Command
	 * @return Executor
	 * @since 1.0.0
	 */
	public Executor type() default Executor.ALL;
	
	/**
	 * Permission of the Command
	 * @return Permission
	 * @since 1.0.0
	 */
	public Permission permission() default Permission.NONE;
	
	/**
	 * Alias of the Command
	 * @return String[]
	 * @since 1.0.0
	 */
	public String[] alias() default {};
	
	/**
	 * Can send the command in the Private channel
	 * @since 1.1.0
	 */
	public boolean executePrivate() default false;

	/**
	 * Can send the command in a guild only
	 * @since 1.1.3
	 */
	public long[] guildId() default {};

	/**
	 * Can send the command in a text channel only
	 * @since 1.1.3
	 */
	public long[] textChannelId() default {};

	/**
	 * Who can execute command
	 * @author NeutronStars
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public enum Executor{
		USER, CONSOLE, ALL;
	}
	
	/**
	 * Permission of the Command 
	 * @author NeutronStars
	 * @since 1.0.0
	 */
	public enum Permission {
		NONE, OPERATOR, ADMINISTRATOR;
	}
}
