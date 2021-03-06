package fr.neutronstars.nbot.plugin;

import java.io.IOException;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.CommandManager;
import fr.neutronstars.nbot.logger.NBotLogger;
import net.dv8tion.jda.core.JDA;

/**
 * Main class for the plugins.
 * @author NeutronStars
 * @version 1.1.2
 * @since 1.0.0
 */

public abstract class NBotPlugin{

	private NBotClassLoader jdaClassLoader;
	private final String[] authors;
	private String name, version;
	
	public NBotPlugin(String... authors){
		this.authors = authors;
	}
	
	public final String getName() {
		return name;
	}
	
	public final String getVersion() {
		return version;
	}
	
	public final String[] getAuthors() {
		return authors;
	}
	
	public void setName(String name) {
		this.name = name != null ? name : "My_Plugin";
	}
	
	public void setVersion(String version) {
		this.version = version != null ? version : "1.0";
	}
	
	/**
	 * Register a command in the application.
	 * @param commandManager
	 * @since 1.0.0
	 */
	public final void registerCommand(CommandManager... commandManager){
		NBot.getNBot().getPluginManager().registerCommands(commandManager);
	}
	
	/**
	 * Retrieves the instance of the class JDA.
	 * @return JDA
	 * @since 1.0.0
	 */
	public final JDA getJDA(){
		return NBot.getNBot().getJDA();
	}
	
	/**
	 * Retrieves the instance of the class NBotLogger.
	 * @return NBotLogger
	 * @since 1.0.0
	 */
	public final NBotLogger getLogger(){
		return NBotLogger.getLogger();
	}
	
	/**
	 * Call when loading the plugin.
	 * @since 1.0.0
	 */
	public void onLoad(){}
	
	/**
	 * Call when disabling the plugin.
	 * @since 1.0.0
	 */
	public void onDisable(){}
	
	protected void setPluginClassLoader(NBotClassLoader jdaClassLoader){
		this.jdaClassLoader = jdaClassLoader;
	}
	
	protected void close(){
		try {
			jdaClassLoader.close();
		} catch (IOException e) {
			getLogger().logThrowable(e);
		}
	}

	public String getAuthorsToString(){
		StringBuilder builder = new StringBuilder();
		for(String name : getAuthors()){
			if(builder.length() != 0) builder.append(", ");
			builder.append(name);
		}
		return builder.length() != 0 ? builder.toString() : builder.append("Not author").toString();
	}
}
