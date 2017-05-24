package fr.neutronstars.nbot.plugin;

import java.io.IOException;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.CommandManager;
import fr.neutronstars.nbot.logger.NBotLogger;
import net.dv8tion.jda.core.JDA;

/**
 * @author NeutronStars
 */

public abstract class JDAPlugin{

	private JDAClassLoader jdaClassLoader;
	private final String[] authors;
	private String name, version;
	
	public JDAPlugin(String... authors){
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
	
	public final void registerCommand(CommandManager... commandManager){
		NBot.getNBot().getPluginManager().registerCommands(commandManager);
	}
	
	public final JDA getJDA(){
		return NBot.getNBot().getJDA();
	}
	
	public final NBotLogger getLogger(){
		return NBotLogger.LOGGER;
	}
	
	public void onLoad(){}
		
	public void onDisable(){}
	
	protected void setPluginClassLoader(JDAClassLoader jdaClassLoader){
		this.jdaClassLoader = jdaClassLoader;
	}
	
	protected void close(){
		try {
			jdaClassLoader.close();
		} catch (IOException e) {
			getLogger().logThrowable(e);
		}
	}
}
