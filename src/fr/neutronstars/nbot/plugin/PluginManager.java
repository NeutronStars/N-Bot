package fr.neutronstars.nbot.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import fr.neutronstars.nbot.NBot;
import fr.neutronstars.nbot.command.CommandManager;
import fr.neutronstars.nbot.command.CommandMap;
import fr.neutronstars.nbot.logger.NBotLogger;

/**
 * @author NeutronStars
 * @version 1.1.2
 * @since 1.0.0
 */

public final class PluginManager {

	private final Map<String, NBotPlugin> plugins = new HashMap<>();
	private final NBotLogger logger = NBotLogger.getLogger();
	private final File folder = new File("plugins");
	private final CommandMap commandMap;
	
	public PluginManager(CommandMap commandMap){
		this.commandMap = commandMap;
		if(!folder.exists()) folder.mkdirs();
	}
	
	public void loadPlugins(){
		if(plugins.size() > 0) disablePlugins();
		for(File file : folder.listFiles()) loadPlugin(file);
	}
	
	public void loadPlugin(File file){
		NBotPlugin plugin = loadNBotPlugin(file);
		if(plugin != null){
			plugin.onLoad();
			logger.log(String.format("%1$s %2$s is loaded.", new Object[]{plugin.getName(), plugin.getVersion()}));
		}		
	}
	
	@SuppressWarnings("resource")
	private NBotPlugin loadNBotPlugin(File file){
		try(JarFile jar = new JarFile(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(jar.getJarEntry("plugin.txt"))))){
			String[] params = new String[3];
			while (reader.ready()){
				String line = reader.readLine().replace(" ", "");
				if(line.startsWith("main=")) params[0] = line.replaceFirst("main=", "");
				if(line.startsWith("name=")) params[1] = line.replaceFirst("name=", "");
				if(line.startsWith("version=")) params[2] = line.replaceFirst("version=", "");
			}
			NBotClassLoader pluginClassLoader = new NBotClassLoader(params[0], this.getClass().getClassLoader(),file);
			NBotPlugin plugin = pluginClassLoader.getPlugin();
			plugin.setName(params[1]);
			plugin.setVersion(params[2]);
			plugins.put(plugin.getName(), plugin);
			return plugin;
		}catch (Exception e) {
			logger.logThrowable(e);
		}
		return null;
	}

	public void disablePlugins() {
		for(NBotPlugin plugin : plugins.values()){
			plugin.onDisable();
			plugin.close();
			logger.log(String.format("%1$s %2$s is disabled.", new Object[]{plugin.getName(), plugin.getVersion()}));
		}
		plugins.clear();
	}
	
	public NBotPlugin getPlugin(String name){
		return plugins.get(name);
	}

	@SuppressWarnings("unchecked")
	public <T extends NBotPlugin> T getPlugin(Class<T> clazz){
		for(NBotPlugin plugin : getPlugins()){
			if(plugin.getClass() == clazz) return (T) plugin;
		}
		return null;
	}

	public Collection<NBotPlugin> getPlugins(){
		return new ArrayList<>(plugins.values());
	}

	/**
	 * Register a command in the application.
	 * @param commandManager
	 * @since 1.0.0
	 */
	public final void registerCommand(CommandManager... commandManager){
		NBot.getNBot().getPluginManager().registerCommands(commandManager);
	}
	public void registerCommands(CommandManager...commandManagers){
		commandMap.registerCommands(commandManagers);
	}
}
