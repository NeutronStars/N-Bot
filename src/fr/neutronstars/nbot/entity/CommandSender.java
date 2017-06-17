package fr.neutronstars.nbot.entity;

/**
 * Entities who can execute commands.
 * @author NeutronStars
 * @version 1.0.0
 * @since 1.1.0
 */
public interface CommandSender {
	
	public String getName();
	
	public default boolean isConsoleEntity(){
		return false;
	}
	
	public default boolean isUserEntity(){
		return false;
	}
	
	public default ConsoleEntity getConsoleEntity(){
		return isConsoleEntity() ? (ConsoleEntity) this : null;
	}
	
	public default UserEntity getUserEntity(){
		return isUserEntity() ? (UserEntity) this : null;
	}
}