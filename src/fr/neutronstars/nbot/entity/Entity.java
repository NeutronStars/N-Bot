package fr.neutronstars.nbot.entity;

import net.dv8tion.jda.core.JDA;

/**
 * @author NeutronStars
 * @version 1.0.0
 * @since 1.1.0
 */
public interface Entity {

	public String getName();
	
	public JDA getJDA();
	
	public void sendMessage(String text);

	public void sendMessage(String format, Object... args);
	
	public String getAsMention();
}
