package fr.neutronstars.nbot.entity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import fr.neutronstars.nbot.NBot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.restaction.pagination.MessagePaginationAction;

/**
 * Discord Channel. (Text or Private.)
 * @author NeutronStars
 * @version 1.0.0
 * @since 1.1.0
 */
public class Channel implements ChannelEntity {

	private final MessageChannel channel;
	
	public Channel(MessageChannel channel){
		this.channel = channel;
	}
	
	public String getId() {
		return channel.getId();
	}
	
	public MessageChannel getChannel() {
		return channel;
	}
	
	public long getIdLong() {
		return channel.getIdLong();
	}

	public OffsetDateTime getCreationTime() {
		return channel.getCreationTime();
	}
	
	public void sendMessage(MessageEmbed embed) {
		channel.sendMessage(embed).queue();
	}

	public void sendMessage(Message msg) {
		channel.sendMessage(msg.getMessage()).queue();
	}

	public void sendFile(File file, Message message) {
		try {
			channel.sendFile(file, message.getMessage()).queue();
		} catch (IOException e){
			sendMessage("```diff\nError : "+e.getMessage()+"```");
		}
	}

	public void sendFile(File file, String fileName, Message message) {
		try{
			channel.sendFile(file, fileName, message.getMessage());
		}catch(IOException e){
			sendMessage("```diff\nError : "+e.getMessage()+"```");
		}
	}

	public void sendFile(InputStream data, String fileName, Message message) {
		channel.sendFile(data, fileName, message.getMessage());
	}

	public void sendFile(byte[] data, String fileName, Message message) {
		channel.sendFile(data, fileName, message.getMessage());
	}

	public String getName(){
		return channel.getName();
	}

	public JDA getJDA() {
		return NBot.getNBot().getJDA();
	}

	public void sendMessage(String text) {
		channel.sendMessage(text).queue();
	}

	public void sendMessage(String format, Object... args) {
		channel.sendMessageFormat(format, args);
	}

	public String getLatestMessageId() {
		return channel.getLatestMessageId();
	}

	public long getLatestMessageIdLong() {
		return channel.getLatestMessageIdLong();
	}

	public boolean hasLatestMessage() {
		return channel.hasLatestMessage();
	}

	public ChannelType getType() {
		return channel.getType();
	}

	public Message getMessageById(String messageId) {
		return new Message(channel.getMessageById(messageId).complete());
	}

	public Message getMessageById(long messageId) {
		return new Message(channel.getMessageById(messageId).complete());
	}

	public void deleteMessageById(String messageId) {
		channel.deleteMessageById(messageId).queue();
	}

	public void deleteMessageById(long messageId) {
		channel.deleteMessageById(messageId).queue();
	}

	public MessageHistory getHistory() {
		return channel.getHistory();
	}

	public MessagePaginationAction getIterableHistory() {
		return channel.getIterableHistory();
	}

	public MessageHistory getHistoryAround(Message message, int limit) {
		return channel.getHistoryAround(message.getMessage(), limit).complete();
	}

	public MessageHistory getHistoryAround(String messageId, int limit) {
		return channel.getHistoryAround(messageId, limit).complete();
	}

	public MessageHistory getHistoryAround(long messageId, int limit) {
		return channel.getHistoryAround(messageId, limit).complete();
	}

	public void sendTyping(){
		channel.sendTyping().complete();
	}

	public void addReactionById(String messageId, String unicode) {
		channel.addReactionById(messageId, unicode).queue();
	}

	public void addReactionById(long messageId, String unicode) {
		channel.addReactionById(messageId, unicode).queue();
	}

	public void addReactionById(String messageId, Emote emote) {
		channel.addReactionById(messageId, emote).queue();
	}

	public void addReactionById(long messageId, Emote emote) {
		channel.addReactionById(messageId, emote).queue();
	}

	public void pinMessageById(String messageId) {
		channel.pinMessageById(messageId).queue();
	}

	public void pinMessageById(long messageId) {
		channel.pinMessageById(messageId).queue();
	}

	public void unpinMessageById(String messageId) {
		channel.unpinMessageById(messageId).queue();
	}

	public void unpinMessageById(long messageId) {
		channel.unpinMessageById(messageId).queue();
	}

	public List<Message> getPinnedMessages() {
		List<Message> list = new ArrayList<>();
		List<net.dv8tion.jda.core.entities.Message> list2 = channel.getPinnedMessages().complete();
		for(net.dv8tion.jda.core.entities.Message message : list2) list.add(new Message(message));
		return list;
	}

	public Message editMessageById(String messageId, String newContent) {
		return new Message(channel.editMessageById(messageId, newContent).complete());
	}

	public Message editMessageById(String messageId, Message newContent) {
		return new Message(channel.editMessageById(messageId, newContent.getMessage()).complete());
	}

	public Message editMessageById(String messageId, String format, Object... args) {
		return new Message(channel.editMessageFormatById(messageId, format, args).complete());
	}

	public Message editMessageById(long messageId, String format, Object... args) {
		return new Message(channel.editMessageFormatById(messageId, format, args).complete());
	}

	public Message editMessageById(long messageId, Message newContent) {
		return new Message(channel.editMessageById(messageId, newContent.getMessage()).complete());
	}

	public Message editMessageById(String messageId, MessageEmbed newEmbed) {
		return new Message(channel.editMessageById(messageId, newEmbed).complete());
	}

	public Message editMessageById(long messageId, MessageEmbed newEmbed) {
		return new Message(channel.editMessageById(messageId, newEmbed).complete());
	}

	public void formatTo(Formatter formatter, int flags, int width, int precision) {
		channel.formatTo(formatter, flags, width, precision);
	}

	public String getAsMention() {
		return channel instanceof TextChannel ? ((TextChannel)channel).getAsMention() : getName();
	}
	
	public boolean isTextChannel() {
		return channel instanceof TextChannel;
	}

	public boolean isPrivateChannel() {
		return channel instanceof PrivateChannel;
	}
	
	public TextChannel getTextChannel() {
		return isTextChannel() ? (TextChannel) channel : null;
	}
	
	public PrivateChannel getPrivateChannel() {
		return isPrivateChannel() ? (PrivateChannel) channel : null;
	}
}
