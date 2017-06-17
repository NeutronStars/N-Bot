package fr.neutronstars.nbot.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author NeutronStars
 * @since 1.0
 */

final class NBotClassLoader extends URLClassLoader{
	
	private NBotPlugin plugin;
	
	protected NBotClassLoader(String main, ClassLoader parent,File file) throws Exception{
		super(new URL[]{file.toURI().toURL()}, parent);
		Class<?> clazz = Class.forName(main, true, this);
		plugin = clazz.asSubclass(NBotPlugin.class).newInstance();
		plugin.setPluginClassLoader(this);
	}
	
	public NBotPlugin getPlugin() {
		return plugin;
	}
}
