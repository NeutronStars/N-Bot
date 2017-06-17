package fr.neutronstars.nbot.entity;

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