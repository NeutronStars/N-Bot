package fr.neutronstars.nbot.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value=ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	public String name();
	public String description() default "hasn't description.";
	public Executor type() default Executor.ALL;
	public Permission permission() default Permission.NONE;
	
	public enum Executor{
		USER, CONSOLE, ALL;
	}
	
	public enum Permission {
		NONE, OPERATOR, ADMINISTRATOR;
	}
}
