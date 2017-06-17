package fr.neutronstars.nbot.entity;

import java.util.Formatter;
import java.util.List;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.restaction.pagination.MessagePaginationAction;

public interface ChannelEntity extends DiscordEntity{
	
	public String getLatestMessageId();
	
	public long getLatestMessageIdLong();

	public boolean hasLatestMessage();
	
	public ChannelType getType();
	
	public Message getMessageById(String messageId);

	public Message getMessageById(long messageId);

	public void deleteMessageById(String messageId);

	public void deleteMessageById(long messageId);

	public MessageHistory getHistory();

	public MessagePaginationAction getIterableHistory();

	public MessageHistory getHistoryAround(Message message, int limit);

	public MessageHistory getHistoryAround(String messageId, int limit);
	
	public MessageHistory getHistoryAround(long messageId, int limit);

	public void sendTyping();

	public void addReactionById(String messageId, String unicode);

	public void addReactionById(long messageId, String unicode);

	public void addReactionById(String messageId, Emote emote);
	
	public void addReactionById(long messageId, Emote emote);

	public void pinMessageById(String messageId);

	public void pinMessageById(long messageId);

	public void unpinMessageById(String messageId);

	public void unpinMessageById(long messageId);

	public List<Message> getPinnedMessages();

	public Message editMessageById(String messageId, String newContent);

	public Message editMessageById(String messageId, Message newContent);

	public Message editMessageById(String messageId, String format, Object... args);

	public Message editMessageById(long messageId, String format, Object... args);

	public Message editMessageById(long messageId, Message newContent);

	public Message editMessageById(String messageId, MessageEmbed newEmbed);

	public Message editMessageById(long messageId, MessageEmbed newEmbed);

	public void formatTo(Formatter formatter, int flags, int width, int precision);
	
	public boolean isTextChannel();
	
	public boolean isPrivateChannel();
	
	public TextChannel getTextChannel();
	
	public PrivateChannel getPrivateChannel();

}
