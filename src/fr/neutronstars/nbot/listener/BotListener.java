package fr.neutronstars.nbot.listener;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.CommandMap;
import fr.neutronstars.nbot.logger.NBotLogger;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

/**
 * @author NeutronStars
 * @since 1.0
 */
public class BotListener implements EventListener{

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
		String message = event.getMessage().getContent();
		if(message.startsWith(commandMap.getTag())){
			message = message.replaceFirst(commandMap.getTag(), "");
			if(commandMap.commandUser(event.getAuthor(), message, event.getMessage()))
				if(event.getGuild() != null && event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) event.getMessage().delete().queue();
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
		NBotLogger.LOGGER.log(builder.toString());
	}

}
