package fr.neutronstars.nbot.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author NeutronStars
 */

final class JDAClassLoader extends URLClassLoader{
	
	private JDAPlugin plugin;
	
	protected JDAClassLoader(String main, ClassLoader parent,File file) throws Exception{
		super(new URL[]{file.toURI().toURL()}, parent);
		Class<?> clazz = Class.forName(main, true, this);
		plugin = clazz.asSubclass(JDAPlugin.class).newInstance();
		plugin.setPluginClassLoader(this);
	}
	
	public JDAPlugin getPlugin() {
		return plugin;
	}
}
