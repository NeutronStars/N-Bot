package fr.neutronstars.nbot.listener;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.CommandMap;
import fr.neutronstars.nbot.entity.Message;
import fr.neutronstars.nbot.entity.User;
import fr.neutronstars.nbot.logger.NBotLogger;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

/**
 * @author NeutronStars
 * @version 1.1.0
 * @since 1.0.0
 */
public class BotListener implements EventListener{

	private final NBotLogger logger = NBotLogger.getLogger();
	private final CommandMap commandMap;
	
	public BotListener(CommandMap commandMap){
		this.commandMap = commandMap;
	}
	
	public void onEvent(Event event) {
		if(event instanceof ReadyEvent) readyEvent((ReadyEvent)event);
		if(event instanceof MessageReceivedEvent) messageReceivedEvent((MessageReceivedEvent)event);
	}

	public void messageReceivedEvent(MessageReceivedEvent event){
		if(event.getAuthor().equals(event.getJDA().getSelfUser())) return;
		String msg = event.getMessage().getContent();
		if(msg.startsWith(commandMap.getTag())){
			Message message = new Message(event.getMessage());
			User userEntity = new User(event.getAuthor());
			msg = msg.replaceFirst(commandMap.getTag(), "");
			String[] commands = msg.replace("\\|", "&SPLITNBOT&").split("&SPLITNBOT&");
			boolean execute = false;
			for(int i = 0; i < commands.length; i++){
				String command = commands[i];
				while (command.startsWith(" ")) command = command.replaceFirst(" ", "");
				if(commandMap.onCommand(userEntity, command, message) && !execute) execute = true;
			}
			if(execute && message.getChannel().isTextChannel() && message.getChannel().getTextChannel().getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE) && !message.isDelete()) message.delete();
			return;
		}
	}
	
	private void readyEvent(ReadyEvent event) {
		NBot.getNBot().start();
		JDA jda = event.getJDA();
		StringBuilder builder = new StringBuilder("\n========================================\n")
		.append(jda.getSelfUser().getName()).append(" is ready.\n========================================\nGuilds :");
		jda.getGuilds().forEach(g->{
			NBot.getNBot().loadGuild(g);
			builder.append("\n     -> ").append(g.getName());
		});
		builder.append("\n========================================");
		logger.log(builder.toString());
	}

}
