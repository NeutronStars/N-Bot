package fr.neutronstars.nbot.entity;

import java.util.List;

import net.dv8tion.jda.core.entities.Guild;

public interface UserEntity extends DiscordEntity, CommandSender{
	
	public String getDiscriminator();

	public String getAvatarId();

	public String getAvatarUrl();

	public String getDefaultAvatarId();

	public String getDefaultAvatarUrl();

	public String getEffectiveAvatarUrl();

	List<Guild> getMutualGuilds();

	ChannelEntity getPrivateChannel();
	
	public boolean isOperator(Guild guild);
	
	public default boolean isUserEntity() {
		return true;
	}

}
