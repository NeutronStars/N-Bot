package fr.neutronstars.nbot.entity;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.logger.NBotLogger;
import net.dv8tion.jda.core.JDA;

/**
 * The console entity.
 * @author NeutronStars
 * @version 1.0.0
 * @since 1.1.0
 */
public final class ConsoleEntity implements Entity, CommandSender{
	
	private final NBotLogger logger = NBotLogger.getLogger();
	
	public ConsoleEntity(){}

	public String getName() {
		return "Server";
	}

	public JDA getJDA() {
		return NBot.getNBot().getJDA();
	}
	
	public void sendMessage(String message){
		logger.log(message);
	}
	
	public void sendMessage(String format, Object... args) {
		sendMessage(String.format(format, args));
	}

	public String getAsMention() {
		return getName();
	}
	
	public boolean isConsoleEntity() {
		return true;
	}
}
