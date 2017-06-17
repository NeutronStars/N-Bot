package fr.neutronstars.nbot.command;

import java.lang.reflect.Method;

import fr.neutronstars.nbot.command.Command.Executor;
import fr.neutronstars.nbot.command.Command.Permission;

/**
 * @author NeutronStars
 * @since 1.0
 */
public final class SimpleCommand {

	private final String name;
	private final Executor executor;
	private final String description;
	private final Permission permission;
	private final CommandManager commandManager;
	private final boolean executePrivate;
	private final String[] alias;
	private final Method method;
	
	public SimpleCommand(String name, Executor executor, String description, Permission permission,CommandManager commandManager, boolean executePrivate,String[] alias, Method method){
		this.name = name;
		this.executor = executor;
		this.description = description;
		this.permission = permission;
		this.commandManager = commandManager;
		this.executePrivate = executePrivate;
		this.alias = alias;
		this.method = method;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Executor getExecutor() {
		return executor;
	}
	
	public Permission getPermission() {
		return permission;
	}
	
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
	public boolean canExecutePrivate(){
		return executePrivate;
	}
	
	public boolean hasAlias(){
		return alias.length > 0;
	}
	
	public String[] getAlias() {
		return alias;
	}
	
	public String getAliasString(){
		if(!hasAlias()) return "";
		StringBuilder builder = new StringBuilder();
		for(String alias : this.alias){
			if(builder.length() > 0) builder.append(", ");
			builder.append(alias);
		}
		return builder.toString();
	}
	
	public Method getMethod() {
		return method;
	}
}
