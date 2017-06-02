package fr.neutronstars.nbot.command;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.Command.Executor;
import fr.neutronstars.nbot.command.defaults.DefaultCommand;
import fr.neutronstars.nbot.logger.Level;
import fr.neutronstars.nbot.logger.NBotLogger;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * @author NeutronStars
 * @since 1.0
 */
public final class CommandMap {

	private final List<SimpleCommand> simpleCommands = new ArrayList<>();
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
		return Collections.unmodifiableCollection(simpleCommands);
	}
	
	public void registerCommands(CommandManager...commandManagers){
		for(CommandManager commandManager : commandManagers) registerCommand(commandManager);
	}
	
	public void registerCommand(CommandManager commandManager){
		for(Method method : commandManager.getClass().getDeclaredMethods()){
			if(method.isAnnotationPresent(Command.class)){
				Command command = method.getAnnotation(Command.class);
				method.setAccessible(true);
				SimpleCommand simpleCommand = new SimpleCommand(command.name(), command.type(), command.description(), command.permission(), commandManager, command.alias(), method);
				simpleCommands.add(simpleCommand);
				commands.put(command.name(), simpleCommand);
				if(command.alias().length > 0){
					for(String alias : command.alias()) commands.put(alias, simpleCommand);
				}
			}
		}
	}
	
	public boolean onCommand(User user, String command, Message message){
		Object[] object = getCommand(command);
		if(user == null) NBotLogger.LOGGER.log(command);
		else{
			if(message.getTextChannel() != null) NBotLogger.LOGGER.log(user.getName()+" has perform command to guild "+message.getGuild().getName() +" -> "+command);
			else if(message.getPrivateChannel() != null) NBotLogger.LOGGER.log(user.getName()+" has perform command in private channel : "+command);
		}
		if(object[0] == null || (user == null && ((SimpleCommand)object[0]).getExecutor() == Executor.USER) || (user != null && ((SimpleCommand)object[0]).getExecutor() == Executor.CONSOLE)){
			if(user == null) NBotLogger.LOGGER.log("Command unknow.");
			return false;
		}
		try{
			if(user != null && message.getTextChannel() != null && !NBot.getNBot().getServer(message.getGuild()).hasPermission(((SimpleCommand)object[0]).getPermission(), user)){
				if(message.getGuild().getMember(message.getJDA().getSelfUser()).hasPermission(Permission.MESSAGE_WRITE)) message.getTextChannel().sendMessage(user.getAsMention()+"\n```diff\n-You don't have permission to perform this command.```").queue();
				return true;
			}
			execute(((SimpleCommand)object[0]), command,(String[])object[1], message);
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
	
	private void execute(SimpleCommand simpleCommand, String command, String[] args, Message message) throws Exception{
		Parameter[] parameters = simpleCommand.getMethod().getParameters();
		Object[] objects = new Object[parameters.length];
		for(int i = 0; i < parameters.length; i++){
			if(parameters[i].getType() == String[].class) objects[i] = args;
			else if(parameters[i].getType() == User.class) objects[i] = message == null ? null : message.getAuthor();
			else if(parameters[i].getType() == TextChannel.class) objects[i] = message == null ? null : message.getTextChannel();
			else if(parameters[i].getType() == PrivateChannel.class) objects[i] = message == null ? null : message.getPrivateChannel();
			else if(parameters[i].getType() == Guild.class) objects[i] = message == null ? null : message.getGuild();
			else if(parameters[i].getType() == String.class) objects[i] = command;
			else if(parameters[i].getType() == Message.class) objects[i] = message;
			else if(parameters[i].getType() == JDA.class) objects[i] = NBot.getNBot().getJDA();
			else if(parameters[i].getType() == MessageChannel.class) objects[i] = message.getChannel();
		}
		simpleCommand.getMethod().invoke(simpleCommand.getCommandManager(), objects);
	}
}
