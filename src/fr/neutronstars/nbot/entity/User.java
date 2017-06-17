package fr.neutronstars.nbot.entity;

import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.Command.Permission;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.impl.UserImpl;

public class User implements UserEntity {

	private final net.dv8tion.jda.core.entities.User user;
	private Channel privateChannel;
	
	public User(net.dv8tion.jda.core.entities.User user){
		this.user = user;
	}
	
	public String getId() {
		return user.getId();
	}

	public net.dv8tion.jda.core.entities.User getUser() {
		return user;
	}
	
	public long getIdLong() {
		return user.getIdLong();
	}

	public OffsetDateTime getCreationTime() {
		return user.getCreationTime();
	}

	private void openPrivateChannel(){
		if(!user.hasPrivateChannel()) user.openPrivateChannel().complete();
		if(privateChannel == null || !privateChannel.getChannel().equals(((UserImpl)user).getPrivateChannel()))
			privateChannel = new Channel(((UserImpl)user).getPrivateChannel());
	}
	
	public void sendMessage(MessageEmbed embed) {
		openPrivateChannel();
		privateChannel.sendMessage(embed);
	}

	public void sendMessage(Message msg) {
		openPrivateChannel();
		privateChannel.sendMessage(msg);
	}

	public void sendFile(File file, Message message) {
		openPrivateChannel();
		privateChannel.sendFile(file, message);
	}

	public void sendFile(File file, String fileName, Message message) {
		openPrivateChannel();
		privateChannel.sendFile(file, fileName, message);
	}

	public void sendFile(InputStream data, String fileName, Message message) {
		openPrivateChannel();
		privateChannel.sendFile(data, fileName, message);
	}

	public void sendFile(byte[] data, String fileName, Message message) {
		openPrivateChannel();
		privateChannel.sendFile(data, fileName, message);
	}

	public String getName() {
		return user.getName();
	}

	public JDA getJDA() {
		return NBot.getNBot().getJDA();
	}

	public void sendMessage(String text) {
		openPrivateChannel();
		privateChannel.sendMessage(text);
	}

	public void sendMessage(String format, Object... args) {
		openPrivateChannel();
		privateChannel.sendMessage(format, args);
	}

	public String getDiscriminator() {
		return user.getDiscriminator();
	}

	public String getAvatarId() {
		return user.getAvatarId();
	}

	public String getAvatarUrl() {
		return user.getAvatarUrl();
	}

	public String getDefaultAvatarId() {
		return user.getDefaultAvatarId();
	}

	public String getDefaultAvatarUrl() {
		return user.getDefaultAvatarUrl();
	}

	public String getEffectiveAvatarUrl() {
		return user.getEffectiveAvatarUrl();
	}

	public List<Guild> getMutualGuilds() {
		return user.getMutualGuilds();
	}

	public ChannelEntity getPrivateChannel() {
		openPrivateChannel();
		return privateChannel;
	}

	public String getAsMention() {
		return user.getAsMention();
	}
	
	@Override
	public boolean isOperator(Guild guild) {
		return NBot.getNBot().getServer(guild).hasPermission(Permission.OPERATOR, this);
	}
}
