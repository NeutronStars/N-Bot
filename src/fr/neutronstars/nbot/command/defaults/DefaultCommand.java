package fr.neutronstars.nbot.command.defaults;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.Command;
import fr.neutronstars.nbot.command.Command.Executor;
import fr.neutronstars.nbot.command.Command.Permission;
import fr.neutronstars.nbot.command.CommandManager;
import fr.neutronstars.nbot.command.CommandMap;
import fr.neutronstars.nbot.command.SimpleCommand;
import fr.neutronstars.nbot.logger.NBotLogger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * Default Command of NBot
 * @author NeutronStars
 * @since 1.0
 */
public final class DefaultCommand implements CommandManager {

	private final CommandMap commandMap;
	
	public DefaultCommand(CommandMap commandMap){
		this.commandMap = commandMap;
	}
	
	/**
	 * Help Command.
	 * @param user
	 * @param channel
	 * @since 1.0
	 */
	@SuppressWarnings("deprecation")
	@Command(name="help",description="Shows the list of commands.")
	private void help(User user, TextChannel channel){
		StringBuilder builder = new StringBuilder(user == null ? "\n========================================" : "```").append("\nCommands list :\n\n");
		for(SimpleCommand command : commandMap.getCommands()){
			if((user == null && command.getExecutor() == Executor.USER) || (user != null && (command.getExecutor() == Executor.CONSOLE || (channel != null && !NBot.getNBot().getServer(channel.getGuild()).hasPermission(command.getPermission(), user))))) continue;	
			builder.append("\n").append(command.getName()).append(" -> ").append(command.getDescription());
		}
		builder.append(user == null ? "\n========================================" : "\n```");
		if(user == null) NBotLogger.LOGGER.log(builder.toString());
		else{
			if(!channel.getGuild().getSelfMember().hasPermission(net.dv8tion.jda.core.Permission.MESSAGE_WRITE)) return;
			if(!user.hasPrivateChannel()) user.openPrivateChannel().complete();
			user.getPrivateChannel().sendMessage(builder.toString()).queue();
		}
	}
	
	/**
	 * Stop Command.
	 * @since 1.0
	 */
	@Command(name="stop",type=Executor.CONSOLE,description="Stop the bot.")
	private void stop(){
		NBot.getNBot().stop();
	}
	
	/**
	 * Operator Command.
	 * @param user
	 * @param channel
	 * @param args
	 * @since 1.0
	 */
	@Command(name="operator",permission=Permission.ADMINISTRATOR,description="Add/Remove a operator of the guild.")
	private void operator(User user, TextChannel channel, String[] args){
		if(user != null && !channel.getGuild().getSelfMember().hasPermission(net.dv8tion.jda.core.Permission.MESSAGE_WRITE)) return;
		Guild guild = null;
		User target = null;
		if((user == null && args.length < 3) || (user != null && args.length < 2)){
			if(user == null) NBotLogger.LOGGER.log("-> operator <add/remove> <ID Guild> <Id User>");
			else{
				if(channel == null) return;
				channel.sendMessage(user.getAsMention()+" ->\n```diff\n-"+commandMap.getTag()+"operator <add/remove> <Id User>```").queue();
			}
		}
		String param = args[0];
		guild = user == null ? NBot.getNBot().getJDA().getGuildById(args[1]) : channel.getGuild();
		target = NBot.getNBot().getJDA().getUserById(user == null ? args[2] : args[1]);
		if(operator(param, guild, target)){
			if(user == null) NBotLogger.LOGGER.log(target.getName()+" "+param+" operator to the guild "+guild.getName()+".");
			else channel.sendMessage(target.getAsMention()+" "+param+" operator.").queue();
		}else{
			if(user == null) NBotLogger.LOGGER.log("-> operator <add/remove> <ID Guild> <Id User>");
			else channel.sendMessage(user.getAsMention()+" ->\n```diff\n-"+commandMap.getTag()+"operator <add/remove> <Id User>```").queue();
		}
	}
	
	private boolean operator(String param, Guild guild, User user){
		if(param == null || guild == null || user == null) return false;
		if(param.equalsIgnoreCase("add")) NBot.getNBot().getServer(guild).addOperator(user.getId());
		else if(param.equalsIgnoreCase("remove")) NBot.getNBot().getServer(guild).removeOperator(user.getId());
		else return false;
		return true;
	}
	
	
}
