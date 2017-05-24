package fr.neutronstars.nbot.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import fr.neutronstars.nbot.command.CommandManager;
import fr.neutronstars.nbot.command.CommandMap;
import fr.neutronstars.nbot.logger.NBotLogger;

/**
 * @author NeutronStars
 */

public final class PluginManager {

	private final Map<String, JDAPlugin> plugins = new HashMap<>();
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
		JDAPlugin plugin = loadJDAPlugin(file);
		if(plugin != null){
			plugin.onLoad();
			NBotLogger.LOGGER.log(String.format("%1$s %2$s is loaded.", new Object[]{plugin.getName(), plugin.getVersion()}));
		}		
	}
	
	@SuppressWarnings("resource")
	private JDAPlugin loadJDAPlugin(File file){
		try(JarFile jar = new JarFile(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(jar.getJarEntry("plugin.txt"))))){
			String[] params = new String[3];
			while (reader.ready()){
				String line = reader.readLine().replace(" ", "");
				if(line.startsWith("main=")) params[0] = line.replaceFirst("main=", "");
				if(line.startsWith("name=")) params[1] = line.replaceFirst("name=", "");
				if(line.startsWith("version=")) params[2] = line.replaceFirst("version=", "");
			}
			JDAClassLoader pluginClassLoader = new JDAClassLoader(params[0], this.getClass().getClassLoader(),file);
			JDAPlugin plugin = pluginClassLoader.getPlugin();
			plugin.setName(params[1]);
			plugin.setVersion(params[2]);
			plugins.put(plugin.getName(), plugin);
			return plugin;
		}catch (Exception e) {
			NBotLogger.LOGGER.logThrowable(e);
		}
		return null;
	}

	public void disablePlugins() {
		for(JDAPlugin plugin : plugins.values()){
			plugin.onDisable();
			plugin.close();
			NBotLogger.LOGGER.log(String.format("%1$s %2$s is disabled.", new Object[]{plugin.getName(), plugin.getVersion()}));
		}
		plugins.clear();
	}
	
	public JDAPlugin getPlugin(String name){
		return plugins.get(name);
	}
	
	public void registerCommands(CommandManager...commandManagers){
		commandMap.registerCommands(commandManagers);
	}
}
