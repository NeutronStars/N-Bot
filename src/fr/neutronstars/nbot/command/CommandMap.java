package fr.neutronstars.nbot.command;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.Command.Executor;
import fr.neutronstars.nbot.command.defaults.DefaultCommand;
import fr.neutronstars.nbot.logger.Level;
import fr.neutronstars.nbot.logger.NBotLogger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public final class CommandMap {

	private final Map<String, SimpleCommand> commands = new HashMap<>();
	private final String tag;
	
	public CommandMap(String tag){
		this.tag = tag;
		registerCommand(new DefaultCommand(this));
	}
	
	public String getTag() {
		return tag;
	}
	
	public Collection<SimpleCommand> getCommands(){
		return commands.values();
	}
	
	public void registerCommands(CommandManager...commandManagers){
		for(CommandManager commandManager : commandManagers) registerCommand(commandManager);
	}
	
	public void registerCommand(CommandManager commandManager){
		for(Method method : commandManager.getClass().getDeclaredMethods()){
			if(method.isAnnotationPresent(Command.class)){
				Command command = method.getAnnotation(Command.class);
				method.setAccessible(true);
				commands.put(command.name(), new SimpleCommand(command.name(), command.type(), command.description(), command.permission(),commandManager,method));
			}
		}
	}
	
	public void commandConsole(String command){
		Object[] object = getCommand(command);
		if(object[0] == null || ((SimpleCommand)object[0]).getExecutor() == Executor.USER){
			System.out.println("Command unknow.");
			return;
		}
		try{
			execute(((SimpleCommand)object[0]), command, (String[])object[1], null, null, null, null);
		}catch(Exception exception){
			NBotLogger.LOGGER.log(Level.FATAL, "The method "+((SimpleCommand)object[0]).getMethod().getName()+" is not initialize correctly.");
		}
	}
	
	public boolean commandUser(User user, String command, Message message){
		Object[] object = getCommand(command);
		if(object[0] == null || ((SimpleCommand)object[0]).getExecutor() == Executor.CONSOLE) return false;
		try{
			if(message.getTextChannel() != null && !NBot.getNBot().getServer(message.getGuild()).hasPermission(((SimpleCommand)object[0]).getPermission(), user)){
				if(message.getGuild().getMember(message.getJDA().getSelfUser()).hasPermission(Permission.MESSAGE_WRITE)) message.getTextChannel().sendMessage(user.getAsMention()+"\n```diff\n-You don't have permission to perform this command.```").queue();
				return true;
			}
			execute(((SimpleCommand)object[0]), command,(String[])object[1], user, message.getTextChannel(), message.getPrivateChannel(), message.getGuild());
		}catch(Exception exception){
			NBotLogger.LOGGER.log(Level.FATAL, "The method "+((SimpleCommand)object[0]).getMethod().getName()+" is not initialize correctly.");
		}
		return true;
	}
	
	private Object[] getCommand(String command){
		String[] commandSplit = command.split(" ");
		String[] args = new String[commandSplit.length-1];
		for(int i = 1; i < commandSplit.length; i++) args[i-1] = commandSplit[i];
		SimpleCommand simpleCommand = commands.get(commandSplit[0]);
		return new Object[]{simpleCommand, args};
	}
	
	private void execute(SimpleCommand command, String message, String[] args, User user, TextChannel textChannel, PrivateChannel privateChannel, Guild guild) throws Exception{
		Parameter[] parameters = command.getMethod().getParameters();
		Object[] objects = new Object[parameters.length];
		for(int i = 0; i < parameters.length; i++){
			if(parameters[i].getType() == String[].class) objects[i] = args;
			else if(parameters[i].getType() == User.class) objects[i] = user;
			else if(parameters[i].getType() == TextChannel.class) objects[i] = textChannel;
			else if(parameters[i].getType() == PrivateChannel.class) objects[i] = privateChannel;
			else if(parameters[i].getType() == Guild.class) objects[i] = guild;
			else if (parameters[i].getType() == String.class) objects[i] = message;
		}
		command.getMethod().invoke(command.getCommandManager(), objects);
	}
}
