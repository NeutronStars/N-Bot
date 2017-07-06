package fr.neutronstars.nbot.command;

import java.lang.reflect.Method;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.Command.Executor;
import fr.neutronstars.nbot.command.Command.Permission;

/**
 * @author NeutronStars
 * @version 1.1.3
 * @since 1.0.0
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
	private final long[] guildId, textChannelId;
	
	public SimpleCommand(String name, Executor executor, String description, Permission permission,CommandManager commandManager, boolean executePrivate,String[] alias, Method method, long[] guildId, long[] textChannelId){
		this.name = name;
		this.executor = executor;
		this.description = description;
		this.permission = permission;
		this.commandManager = commandManager;
		this.executePrivate = executePrivate;
		this.alias = alias;
		this.method = method;
		this.guildId = guildId;
		this.textChannelId = textChannelId;
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

	public long[] getGuildId() {
		return guildId;
	}

	public long[] getTextChannelId() {
		return textChannelId;
	}

	public boolean onlyGuild(){
		return guildId.length != 0;
	}

	public boolean onlyTextChannel(){
		return textChannelId.length != 0;
	}

	public boolean hasGuild(String id){
		return hasGuild(Long.parseLong(id));
	}

	public boolean hasGuild(long id){
		for(long l : guildId) if(l == id) return true;
		return false;
	}

	public boolean hasTextChannel(String id){
		return hasTextChannel(Long.parseLong(id));
	}

	public boolean hasTextChannel(long id){
		for(long l : textChannelId) if(l == id) return true;
		return false;
	}

	public String getGuildList(){
		StringBuilder builder = new StringBuilder();
		for(long id : getGuildId()){
			if(builder.length() != 0) builder.append(", ");
			builder.append(NBot.getNBot().getJDA().getGuildById(id).getName());
		}
		return builder.toString();
	}

	public String getTextChannelList(){
		StringBuilder builder = new StringBuilder();
		for(long id : getTextChannelId()){
			if(builder.length() != 0) builder.append(", ");
			builder.append(NBot.getNBot().getJDA().getTextChannelById(id).getName());
		}
		return builder.toString();
	}
}
