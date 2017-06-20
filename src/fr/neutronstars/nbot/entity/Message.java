package fr.neutronstars.nbot.entity;

import java.time.OffsetDateTime;
import java.util.Formatter;
import java.util.List;

import fr.neutronstars.nbot.NBot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageType;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * Message Discord
 * @author NeutronStars
 * @version 1.0.0
 * @since 1.1.0
 */
public final class Message {

	private net.dv8tion.jda.core.entities.Message message;
	private Channel channel;
	private boolean delete;
	
	public Message(net.dv8tion.jda.core.entities.Message message){
		this.message = message;
		channel = new Channel(message.getChannel());
	}
	
	public String getId() {
		return message.getId();
	}

	public long getIdLong(){
		return message.getIdLong();
	}

	public OffsetDateTime getCreationTime() {
		return message.getCreationTime();
	}
	
	public boolean isDelete() {
		return delete;
	}
	
	public void delete(){
		if(!isDelete()){
			delete = true;
			message.delete().queue();
		}
	}
	
	public net.dv8tion.jda.core.entities.Message getMessage() {
		return message;
	}
	
	public List<User> getMentionedUsers(){
		return message.getMentionedUsers();
	}

	public boolean isMentioned(User user){
		return message.isMentioned(user);
	}

	public List<TextChannel> getMentionedChannels(){
		return message.getMentionedChannels();
	}

	public List<Role> getMentionedRoles(){
		return message.getMentionedRoles();
	}

	public boolean mentionsEveryone(){
		return message.mentionsEveryone();
	}

	public boolean isEdited(){
		return message.isEdited();
	}

	public OffsetDateTime getEditedTime(){
		return message.getEditedTime();
	}

	public User getAuthor(){
		return message.getAuthor();
	}

	public Member getMember(){
		return message.getMember();
	}

	public String getContent(){
		return message.getContent();
	}

	public String getRawContent(){
		return message.getRawContent();
	}

	public String getStrippedContent(){
		return message.getStrippedContent();
	}

	public boolean isFromType(ChannelType type){
		return message.isFromType(type);
	}

	public ChannelType getChannelType(){
		return message.getChannelType();
	}

	public boolean isWebhookMessage(){
		return message.isWebhookMessage();
	}

	public ChannelEntity getChannel(){
		return channel;
	}

	public Guild getGuild(){
		return message.getGuild();
	}

	public List<net.dv8tion.jda.core.entities.Message.Attachment> getAttachments(){
		return message.getAttachments();
	}

	public List<MessageEmbed> getEmbeds(){
		return message.getEmbeds();
	}

	public List<Emote> getEmotes(){
		return message.getEmotes();
	}

	public List<MessageReaction> getReactions(){
		return message.getReactions();
	}

	public boolean isTTS(){
		return message.isTTS();
	}

	public void editMessage(String arg0){
		message = message.editMessage(arg0).complete();
	}

	public void editMessage(MessageEmbed arg0){
		message = message.editMessage(arg0).complete();
	}

	public void editMessage(String arg0, Object... arg1){
		message = message.editMessageFormat(arg0, arg1).complete();
	}

	public void editMessage(net.dv8tion.jda.core.entities.Message arg0){
		message = message.editMessage(arg0).complete();
	}

	public JDA getJDA(){
		return NBot.getNBot().getJDA();
	}

	public boolean isPinned(){
		return message.isPinned();
	}

	public void pin(){
		message.pin().complete();
	}

	public void unpin(){
		message.unpin().complete();
	}

	public void addReaction(Emote arg0){
		message.addReaction(arg0).queue();
	}

	public void addReaction(String arg0){
		message.addReaction(arg0).queue();
	}

	public void clearReactions(){
		message.clearReactions().queue();
	}

	public MessageType getType(){
		return message.getType();
	}
	
	public void formatTo(Formatter formatter, int flags, int width, int precision){
		message.formatTo(formatter, flags, width, precision);
	}
}
