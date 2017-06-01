package fr.neutronstars.nbot.command.defaults;

import java.awt.Color;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.Command;
import fr.neutronstars.nbot.command.Command.Executor;
import fr.neutronstars.nbot.command.Command.Permission;
import fr.neutronstars.nbot.command.CommandManager;
import fr.neutronstars.nbot.command.CommandMap;
import fr.neutronstars.nbot.command.SimpleCommand;
import fr.neutronstars.nbot.logger.NBotLogger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.UserImpl;

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
	@Command(name="help",description="Shows the list of commands.",alias={"?"})
	private void help(User user, TextChannel textChannel){
		if(user == null) helpConsole();
		else if(textChannel != null) helpUser(user, textChannel);
	}
	
	private void helpConsole(){
		StringBuilder builder = new StringBuilder("\n========================================").append("\nCommands list :\n\n");
		for(SimpleCommand command : commandMap.getCommands()){
			if(command.getExecutor() == Executor.USER) continue;	
			builder.append("\n").append(command.getName()).append(" -> ").append(command.getDescription());
		}
		builder.append("\n========================================");
		NBotLogger.LOGGER.log(builder.toString());
	}
	
	private void helpUser(User user, TextChannel channel){
		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(user.getName(), null, user.getAvatarUrl()+"?size=256");
		builder.setTitle("Command lists");
		builder.setDescription("Shows the list of commands of the guild "+channel.getGuild().getName());
		builder.setColor(Color.CYAN);
		for(SimpleCommand command : commandMap.getCommands()){
			if(command.getExecutor() == Executor.CONSOLE || !NBot.getNBot().getServer(channel.getGuild()).hasPermission(command.getPermission(), user)) continue;
			builder.addField(new Field(command.getName(), (command.hasAlias() ? "[>](1) Alias : "+command.getAliasString()+"\n" : "")+"[>](2) Description : "+command.getDescription(), false));
		}
		if(!user.hasPrivateChannel()) user.openPrivateChannel().complete();
		((UserImpl)user).getPrivateChannel().sendMessage(builder.build()).queue();
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
	@Command(name="operator",permission=Permission.ADMINISTRATOR,description="Add/Remove a operator of the guild.",alias={"op"})
	private void operator(User user, TextChannel channel, String[] args){
		if(user != null && !channel.getGuild().getSelfMember().hasPermission(net.dv8tion.jda.core.Permission.MESSAGE_WRITE)) return;
		Guild guild = null;
		User target = null;
		if((user == null && args.length < 3) || (user != null && args.length < 2)){
			if(user == null) NBotLogger.LOGGER.log("-> operator <add/remove> <ID Guild> <Id User>");
			else if(channel != null) channel.sendMessage(user.getAsMention()+" ->\n```diff\n-"+commandMap.getTag()+"operator <add/remove> <Id User>```").queue();
		}
		String param = args[0];
		guild = user == null ? NBot.getNBot().getJDA().getGuildById(args[1]) : channel != null ? channel.getGuild() : null;
		target = NBot.getNBot().getJDA().getUserById(user == null ? args[2] : args[1]);
		if(operator(param, guild, target)){
			if(user == null) NBotLogger.LOGGER.log(target.getName()+" as been "+(param.equalsIgnoreCase("add") ? "added" : "removed")+" as operator to the guild "+guild.getName()+".");
			else channel.sendMessage(target.getAsMention()+" as been "+(param.equalsIgnoreCase("add") ? "added" : "removed")+" as operator.").queue();
		}else{
			if(user == null) NBotLogger.LOGGER.log("-> operator <add/remove> <ID Guild> <Id User>");
			else if(channel != null) channel.sendMessage(user.getAsMention()+" ->\n```diff\n-"+commandMap.getTag()+"operator <add/remove> <Id User>```").queue();
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
