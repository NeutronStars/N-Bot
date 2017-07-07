package fr.neutronstars.nbot.command.defaults;

import java.awt.*;

import java.util.List;
import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.Command;
import fr.neutronstars.nbot.command.Command.Executor;
import fr.neutronstars.nbot.command.Command.Permission;
import fr.neutronstars.nbot.command.CommandManager;
import fr.neutronstars.nbot.command.CommandMap;
import fr.neutronstars.nbot.command.SimpleCommand;
import fr.neutronstars.nbot.entity.*;
import fr.neutronstars.nbot.entity.Channel;
import fr.neutronstars.nbot.entity.Message;
import fr.neutronstars.nbot.entity.User;
import fr.neutronstars.nbot.plugin.NBotPlugin;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.impl.GameImpl;

/**
 * Default Command of NBot
 * @author NeutronStars
 * @version 1.1.2
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
			builder.addField(new Field(command.getName(), (command.hasAlias() ? "[>](1) Alias : "+command.getAliasString()+"\n" : "")+"[>](2) Description : "+command.getDescription()+(command.onlyGuild() ? "\n[>](3) Guilds : "+command.getGuildList() : "")+(command.onlyTextChannel() ? "\n[>](3) Text Channels : "+command.getTextChannelList() : ""), false));
		}
		user.sendMessage(builder.build());
		if(channel.isTextChannel()) channel.sendMessage(user.getAsMention()+" check your private message.");
	}

	/**
	 * Show the command list
	 * @param sender
	 * @param channel
	 * @since 1.1.2
	 */
	@Command(name="plugins",description="Show the plugins list.",alias={"plgs"},executePrivate = true)
	private void plugins(CommandSender sender, Channel channel){
		if(sender.isConsoleEntity()){
			StringBuilder builder = new StringBuilder("Plugins list :\n|--------------|");
			for(NBotPlugin plugin : NBot.getNBot().getPluginManager().getPlugins()){
				builder.append("\n").append(plugin.getName()).append("\n   version : ").append(plugin.getVersion()).append("\n   author(s) : ").append(plugin.getAuthorsToString());
			}
			if(builder.length() < 15) builder.append("\nDon't use a plugin.");
			sender.getConsoleEntity().sendMessage(builder.toString());
			return;
		}
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Plugins list");
		builder.setDescription("|--------------|");
		for(NBotPlugin plugin : NBot.getNBot().getPluginManager().getPlugins()){
			builder.addField(new Field(plugin.getName(), "[>](1) Version : "+plugin.getVersion()+"\n[>](2) Author(s) : "+plugin.getAuthorsToString(), true));
		}
		if(builder.getFields().size() == 0) builder.setDescription("Don't use a plugin.");
		builder.setFooter("Using API N-Bot v"+NBot.getNBot().getVersion()+" created by NeutronStars", null);
		builder.setColor(Color.MAGENTA);
		try{
			channel.sendMessage(builder.build());
		}catch (Exception e){
			channel.sendMessage(e.getMessage());
		}
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

	@Command(name="info",description="Info NBot.",executePrivate = true)
	private void info(CommandSender sender, Channel channel){
		if(sender.isConsoleEntity())
			sender.getConsoleEntity().sendMessage("\n========================================"
					+"\nNBot: \n  -Created by NeutronStars"
					+"\nVersion: "+ NBot.getNBot().getVersion()
					+"\nGuilds: "+NBot.getNBot().getJDA().getGuilds().size()
					+"\nLink:\n  GitHub: https://github.com/NeutronStars/N-Bot"
					+"\n========================================");
		else{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("N-Bot");
			builder.addField("Version", NBot.getNBot().getVersion(), true);
			builder.addField("Guilds", String.valueOf(channel.getJDA().getGuilds().size()), true);
			builder.addField("GitHub", "[>](1) https://github.com/NeutronStars/N-Bot", true);
			builder.setFooter("Created by NeutronStars", null);
			builder.setColor(Color.BLUE);

			try {
				channel.sendMessage(builder.build());
			}catch (Exception e){
				channel.sendMessage(e.getMessage());
			}
		}
	}

	@Command(name = "game",type = Executor.CONSOLE)
	private void game(CommandSender sender, JDA jda, String command){
		jda.getPresence().setGame(new GameImpl(command.replaceFirst("game ", ""), null, Game.GameType.DEFAULT));
		sender.getConsoleEntity().sendMessage("Game modified.");
	}

	@Command(name="invite",type =Executor.USER,executePrivate = true,description = "Invite this bot in your guild.")
	private void invite(User user, Channel channel){
		String url = "https://discordapp.com/oauth2/authorize?client_id=312375900559638528&scope=bot&permissions=2146958583";
		if(channel.isTextChannel()) channel.sendMessage(user.getAsMention()+" -> "+url);
		else channel.sendMessage(url);
	}

	@Command(name = "ban",permission = Permission.ADMINISTRATOR,description = "Ban a user in your guild.")
	private void ban(Channel channel, Message message, String[] args){
		if(args.length < 2 || message.getMentionedUsers().size() == 0){
			channel.sendMessage("&ban <@User> <Raison>");
			return;
		}
		StringBuilder builder = new StringBuilder();
		for(int i = 1; i < args.length; i++){
			if(builder.length() > 0) builder.append(" ");
			builder.append(args[i]);
		}
		channel.getTextChannel().getGuild().getController().ban(message.getMentionedUsers().get(0),0, builder.toString()).complete();
		channel.sendMessage("Thor's hammer to hit !");
	}
}
