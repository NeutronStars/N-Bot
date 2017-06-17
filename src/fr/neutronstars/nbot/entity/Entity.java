package fr.neutronstars.nbot.entity;

import net.dv8tion.jda.core.JDA;

public interface Entity {

	public String getName();
	
	public JDA getJDA();
	
	public void sendMessage(String text);

	public void sendMessage(String format, Object... args);
	
	public String getAsMention();
}
