package fr.neutronstars.nbot.command.defaults;

import java.awt.Color;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.Command;
import fr.neutronstars.nbot.command.Command.Executor;
import fr.neutronstars.nbot.command.Command.Permission;
import fr.neutronstars.nbot.command.CommandManager;
import fr.neutronstars.nbot.command.CommandMap;
import fr.neutronstars.nbot.command.SimpleCommand;
import fr.neutronstars.nbot.entity.Channel;
import fr.neutronstars.nbot.entity.CommandSender;
import fr.neutronstars.nbot.entity.ConsoleEntity;
import fr.neutronstars.nbot.entity.User;
import fr.neutronstars.nbot.entity.UserEntity;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

/**
 * Default Command of NBot
 * @author NeutronStars
 * @version 1.1.0
 * @since 1.0.0
 */
public final class DefaultCommand implements CommandManager {

	private final CommandMap commandMap;
	
	public DefaultCommand(CommandMap commandMap){
		this.commandMap = commandMap;
	}
	
	/**
	 * Help Command.
	 * @param sender
	 * @param channel
	 * @since 1.0.0
	 */
	@Command(name="help",description="Shows the command list.",alias={"?"},executePrivate=true)
	private void help(CommandSender sender, Channel channel){
		if(sender instanceof ConsoleEntity) helpConsole(sender.getConsoleEntity());
		else helpUser(sender.getUserEntity(), channel);
	}
	
	private void helpConsole(ConsoleEntity sender){
		StringBuilder builder = new StringBuilder("\n========================================").append("\nCommands list :\n\n");
		for(SimpleCommand command : commandMap.getCommands()){
			if(command.getExecutor() == Executor.USER) continue;	
			builder.append("\n-> ").append(command.getName());
			if(command.hasAlias()) builder.append("\n     aliases -> ").append(command.getAliasString());
			builder.append("\n     description -> ").append(command.getDescription());
		}
		builder.append("\n========================================");
		sender.sendMessage(builder.toString());
	}
	
	private void helpUser(UserEntity user, Channel channel){
		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(user.getName(), null, user.getAvatarUrl()+"?size=256");
		if(channel.isTextChannel()) builder.setTitle(channel.getTextChannel().getGuild().getName()+" server command list");
		else builder.setTitle("Private channel command list");
		builder.setDescription("Prefix command : "+commandMap.getTag());
		builder.setColor(Color.CYAN);
		for(SimpleCommand command : commandMap.getCommands()){
			if(command.getExecutor() == Executor.CONSOLE
			   || (channel.isTextChannel() && !NBot.getNBot().getServer(channel.getTextChannel().getGuild()).hasPermission(command.getPermission(), (User)user))
			   || (channel.isPrivateChannel() && !command.canExecutePrivate())) continue;
			builder.addField(new Field(command.getName(), (command.hasAlias() ? "[>](1) Alias : "+command.getAliasString()+"\n" : "")+"[>](2) Description : "+command.getDescription(), false));
		}
		user.sendMessage(builder.build());
		if(channel.isTextChannel()) channel.sendMessage(user.getAsMention()+" check your private message.");
	}
	
	
	
	/**
	 * Stop Command.
	 * @since 1.0.0
	 */
	@Command(name="stop",type=Executor.CONSOLE,description="Stop the bot.")
	private void stop(){
		NBot.getNBot().stop();
	}
	
	/**
	 * Operator Command.
	 * @param sender
	 * @param channel
	 * @param args
	 * @since 1.0.0
	 */
	@Command(name="operator",permission=Permission.ADMINISTRATOR,description="Add/Remove a operator of the guild.",alias={"op"})
	private void operator(CommandSender sender, Channel channel, String[] args){
		if(sender.isUserEntity() && !channel.getTextChannel().getGuild().getSelfMember().hasPermission(net.dv8tion.jda.core.Permission.MESSAGE_WRITE)) return;
		Guild guild = null;
		User target = null;
		if((sender.isConsoleEntity() && args.length < 3) || (sender.isUserEntity() && args.length < 2)){
			if(sender.isConsoleEntity()) sender.getConsoleEntity().sendMessage("-> operator <add/remove> <ID Guild> <Id User>");
			else channel.sendMessage(sender.getUserEntity().getAsMention()+" ->\n```diff\n-"+commandMap.getTag()+"operator <add/remove> <Id User>```");
		}
		String param = args[0];
		guild = sender.isConsoleEntity() ? NBot.getNBot().getJDA().getGuildById(args[1]) : channel.getTextChannel().getGuild();
		target = new User(NBot.getNBot().getJDA().getUserById(sender.isConsoleEntity() ? args[2] : args[1]));
		
		if(operator(param, guild, target)){
			if(sender.isConsoleEntity()) sender.getConsoleEntity().sendMessage(target.getName()+" as been "+(param.equalsIgnoreCase("add") ? "added" : "removed")+" as operator to the guild "+guild.getName()+".");
			else channel.sendMessage(target.getAsMention()+" as been "+(param.equalsIgnoreCase("add") ? "added" : "removed")+" as operator.");
		}else{
			if(sender.isConsoleEntity()) sender.getConsoleEntity().sendMessage("-> operator <add/remove> <ID Guild> <Id User>");
			else channel.sendMessage(sender.getUserEntity().getAsMention()+" ->\n```diff\n-"+commandMap.getTag()+"operator <add/remove> <Id User>```");
		}
	}
	
	private boolean operator(String param, Guild guild, User user){
		if(param == null || guild == null || user == null) return false;
		if(param.equalsIgnoreCase("add")) NBot.getNBot().getServer(guild).addOperator(user.getId());
		else if(param.equalsIgnoreCase("remove")) NBot.getNBot().getServer(guild).removeOperator(user.getId());
		else return false;
		return true;
	}
	
	@Command(name="info",description="Info NBot.",executePrivate=true)
	private void info(CommandSender sender, Channel channel){
		String info = "\nInfo NBot: \n  -Created by NeutronStars"
				 	 +"\nGuild count: "+NBot.getNBot().getJDA().getGuilds().size()
				 	 +"\nVersion: "+NBot.getNBot().getVersion();
		if(sender.isConsoleEntity())
			sender.getConsoleEntity().sendMessage("\n========================================"
												 + info
												 +"\n========================================");
		else channel.sendMessage("```yml\n"+info+"```");
		
			
	}
}
