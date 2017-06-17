package fr.neutronstars.nbot.entity;

import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;

import net.dv8tion.jda.core.entities.MessageEmbed;

/**
 * The entities discord (User or Channel)
 * @author NeutronStars
 * @version 1.0.0
 * @since 1.1.0
 */
public interface DiscordEntity extends Entity {

	public String getId();

	public long getIdLong();

	public OffsetDateTime getCreationTime();
	
	public void sendMessage(MessageEmbed embed);

	public void sendMessage(Message msg);

	public void sendFile(File file, Message message);

	public void sendFile(File file, String fileName, Message message);

	public void sendFile(InputStream data, String fileName, Message message);

	public void sendFile(byte[] data, String fileName, Message message);
}
