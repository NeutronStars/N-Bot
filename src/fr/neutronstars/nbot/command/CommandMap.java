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
import fr.neutronstars.nbot.entity.Channel;
import fr.neutronstars.nbot.entity.ChannelEntity;
import fr.neutronstars.nbot.entity.CommandSender;
import fr.neutronstars.nbot.entity.ConsoleEntity;
import fr.neutronstars.nbot.entity.Message;
import fr.neutronstars.nbot.entity.User;
import fr.neutronstars.nbot.entity.UserEntity;
import fr.neutronstars.nbot.logger.Level;
import fr.neutronstars.nbot.logger.NBotLogger;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author NeutronStars
 * @since 1.0
 */
public final class CommandMap {

	private final List<SimpleCommand> simpleCommands = new ArrayList<>();
	private final Map<String, SimpleCommand> commands = new HashMap<>();
	private final NBotLogger logger = NBotLogger.getLogger();
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
				SimpleCommand simpleCommand = new SimpleCommand(command.name(), command.type(), command.description(), command.permission(), commandManager, command.executePrivate(),command.alias(), method);
				simpleCommands.add(simpleCommand);
				commands.put(command.name(), simpleCommand);
				if(command.alias().length > 0){
					for(String alias : command.alias()) commands.put(alias, simpleCommand);
				}
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, String command, Message message){
		Object[] object = getCommand(command);
		if(object[0] == null || (sender.isConsoleEntity() && ((SimpleCommand)object[0]).getExecutor() == Executor.USER) || (sender.isUserEntity() && (((SimpleCommand)object[0]).getExecutor() == Executor.CONSOLE || (message.getChannel().isPrivateChannel() && !((SimpleCommand)object[0]).canExecutePrivate())))){
			if(sender.isConsoleEntity()) sender.getConsoleEntity().sendMessage("Command unknow.");
			return false;
		}
		try{
			if((sender.isUserEntity()) && (message.getChannel().isTextChannel()) && !NBot.getNBot().getServer(message.getGuild()).hasPermission(((SimpleCommand)object[0]).getPermission(), (User)sender)){
				if(message.getGuild().getMember(message.getJDA().getSelfUser()).hasPermission(Permission.MESSAGE_WRITE)) message.getChannel().sendMessage(sender.getUserEntity().getAsMention()+"\n```diff\n-You don't have permission to perform this command.```");
				return true;
			}
			if(sender.isConsoleEntity()) sender.getConsoleEntity().sendMessage(command);
			else{
				if(message.getChannel().isTextChannel()) logger.log(sender.getName()+" has perform command to guild "+message.getGuild().getName() +" -> "+command);
				else if(message.getChannel().isPrivateChannel()) logger.log(sender.getName()+" has perform command in private channel : "+command);
			}
			execute(((SimpleCommand)object[0]), command,(String[])object[1], message, sender);
		}catch(Exception exception){
			logger.log(Level.FATAL, "The method "+((SimpleCommand)object[0]).getMethod().getName()+" is not initialize correctly.");
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
	
	private void execute(SimpleCommand simpleCommand, String command, String[] args, Message message, CommandSender sender) throws Exception{
		Parameter[] parameters = simpleCommand.getMethod().getParameters();
		Object[] objects = new Object[parameters.length];
		for(int i = 0; i < parameters.length; i++){
			
			if(parameters[i].getType() == JDA.class) objects[i] = NBot.getNBot().getJDA();
			
			else if(parameters[i].getType() == String[].class) objects[i] = args;
			
			else if(parameters[i].getType() == String.class) objects[i] = command;
			
			else if(parameters[i].getType() == Message.class) objects[i] = message;
			
			else if(parameters[i].getType() == Guild.class) objects[i] = message == null ? null : message.getGuild();
			else if(parameters[i].getType() == Channel.class) objects[i] = message == null ? null : message.getChannel();
			else if(parameters[i].getType() == ChannelEntity.class) objects[i] = message == null ? null : message.getChannel();
			
			else if(parameters[i].getType() == User.class) objects[i] = sender.isUserEntity() ? sender : null;
			else if(parameters[i].getType() == UserEntity.class) objects[i] = sender.isUserEntity() ? sender : null;
			else if(parameters[i].getType() == CommandSender.class) objects[i] = sender;
			else if(parameters[i].getType() == ConsoleEntity.class) objects[i] = NBot.getNBot().getConsoleEntity();
		}
		simpleCommand.getMethod().invoke(simpleCommand.getCommandManager(), objects);
	}
}
