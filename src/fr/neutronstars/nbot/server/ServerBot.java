package fr.neutronstars.nbot.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import fr.neutronstars.nbot.command.Command.Permission;
import fr.neutronstars.nbot.logger.NBotLogger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

/**
 * 
 * @author NeutronStars
 * @since 1.0
 */
public final class ServerBot {

	/**
	 * List of operator of the guild.
	 * @since 1.0
	 */
	private final List<String> operators = new ArrayList<>();
	
	/**
	 * Creator ID of the guild.
	 * @since 1.0
	 */
	private final String administratorId;
	
	/**
	 * Folder of the guild.
	 * @since 1.0
	 */
	private final File folder;
	
	/**
	 * ID of the guild.
	 * @since 1.0
	 */
	private final String id;
	
	public ServerBot(Guild guild){
		administratorId = guild.getOwner().getUser().getId();
		id = guild.getId();
		folder = new File("servers/"+id);
		if(!folder.exists()) folder.mkdirs();
		load();
	}
	
	/**
	 * Add Operator in the Server.
	 * @param userID
	 * @since 1.0
	 */
	public void addOperator(String userID){
		if(!operators.contains(userID)) operators.add(userID);
	}
	
	/**
	 * Remove Operator in the Server.
	 * @param userID
	 * @since 1.0
	 */
	public void removeOperator(String userID){
		if(operators.contains(userID)) operators.remove(userID);
	}
	
	/**
	 * Check if user has permission.
	 * @param permission
	 * @param user
	 * @return Boolean
	 * @since 1.0
	 */
	public boolean hasPermission(Permission permission, User user){
		switch(permission){
			case NONE : return true;
			case OPERATOR : return administratorId.equalsIgnoreCase(user.getId()) || user.getJDA().getGuildById(id).getMember(user).hasPermission(net.dv8tion.jda.core.Permission.ADMINISTRATOR) || operators.contains(user.getId());
			case ADMINISTRATOR : return administratorId.equalsIgnoreCase(user.getId()) || user.getJDA().getGuildById(id).getMember(user).hasPermission(net.dv8tion.jda.core.Permission.ADMINISTRATOR);
		}
		return false;
	}
	
	private void load(){
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(folder, "permission.txt")))){
			while(bufferedReader.ready()){
				String id = bufferedReader.readLine();
				if(id == null || id.length() < 1) continue;
				addOperator(id);
			}
		}catch (Exception e){
			NBotLogger.LOGGER.logThrowable(e);
		}
	}
	
	public void save(){
		try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "permission.txt")))){
			for(String id : operators){
				bufferedWriter.write(id);
				bufferedWriter.flush();
				bufferedWriter.newLine();
			}
		}catch (Exception e){
			NBotLogger.LOGGER.logThrowable(e);
		}
	}
}
