package fr.neutronstars.nbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import fr.neutronstars.nbot.command.CommandMap;
import fr.neutronstars.nbot.entity.ConsoleEntity;
import fr.neutronstars.nbot.listener.BotListener;
import fr.neutronstars.nbot.logger.NBotLogger;
import fr.neutronstars.nbot.plugin.PluginManager;
import fr.neutronstars.nbot.server.ServerBot;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author NeutronStars
 * @since 1.0 
 */

public final class NBot implements Runnable{

	private static NBot nBot;
	
	/**
	 * Retrieves the instance of the class NBot.
	 * @return NBot
	 * @since 1.0
	 */
	public static NBot getNBot() {
		return nBot;
	}
	
	private final ConsoleEntity consoleEntity = new ConsoleEntity();
	private final Map<String, ServerBot> servers = new HashMap<>();
	private final Thread thread = new Thread(this, "nbot");
	private final Scanner scanner = new Scanner(System.in);
	private final Random random = new Random();
	private final PluginManager pluginManager;
	private final String version = "1.1.0";
	private final CommandMap commandMap;
	private final JDA jda;
	
	private boolean running;
	
	private NBot(String token, String tag) throws Exception{
		commandMap = new CommandMap(tag);
		pluginManager = new PluginManager(commandMap);
		jda = new JDABuilder(AccountType.BOT).setToken(token).buildAsync();
		jda.addEventListener(new BotListener(commandMap));
	}
	
	/**
	 * Retrieves the instance of the class JDA.
	 * @return JDA
	 * @since 1.0
	 */
	public JDA getJDA() {
		return jda;
	}
	
	public String getVersion() {
		return version;
	}
	
	public ConsoleEntity getConsoleEntity() {
		return consoleEntity;
	}
	
	/**
	 * Retrieves the instance of the class PluginManager.
	 * @return PluginManager
	 * @since 1.0
	 */
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	/**
	 * Retrieves a class ServerBot.
	 * @param guild
	 * @return ServerBot
	 * @since 1.0
	 */
	public ServerBot getServer(Guild guild){
		return loadGuild(guild);
	}
	
	/**
	 * Retrieves the tag of command.
	 * @return String
	 * @since 1.0
	 */
	public String getCommandTag() {
		return commandMap.getTag();
	}
	
	/**
	 * Load a instance of ServerBot.
	 * @param guild
	 * @return ServerBot
	 * @since 1.0
	 */
	public ServerBot loadGuild(Guild guild){
		if(!servers.containsKey(guild.getId())) servers.put(guild.getId(), new ServerBot(guild));
		return servers.get(guild.getId());
	}
	
	/**
	 * The nextInt of the class {@link Random}
	 * @param index
	 * @return Integer
	 * @since 1.0
	 */
	public int nextInt(int index){
		return random.nextInt(index);
	}
	
	/**
	 * Start the application.
	 * @since 1.0
	 */
	public void start(){
		if(running) return;
		running = true;
		thread.start();
	}
	
	/**
	 * Stop the application.
	 * @since 1.0
	 */
	public void stop(){
		if(!running) return;
		running = false;
	}
	
	public void run() {
		while (running){
			if(scanner.hasNextLine()){
				String[] commands = scanner.nextLine().replace("\\|", "&SPLITNBOT&").split("&SPLITNBOT&");
				for(int i = 0; i < commands.length; i++){
					String command = commands[i];
					while(command.startsWith(" ")) command = command.replaceFirst(" ", "");
					commandMap.onCommand(consoleEntity, command, null);
				}
			}
		}
		
		consoleEntity.sendMessage("Stopping a bot...");
		pluginManager.disablePlugins();
		consoleEntity.sendMessage("Guilds saving...");
		servers.values().forEach(s->s.save());
		consoleEntity.sendMessage("Guilds saved.");
		jda.shutdown(false);
		consoleEntity.sendMessage("Bot stopped.");
		NBotLogger.getLogger().close();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		NBotLogger logger = NBotLogger.getLogger();
		try{
			File folder = new File("config");
			if(!folder.exists()) folder.mkdirs();
			File file = new File(folder, "info.txt");
			if(!file.exists() && args.length < 1){
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.write("token=Insert your token\n");
				writer.write("tag=bot.\n");
				writer.flush();
				writer.close();
				logger.log("Please, complete the file \"info.txt\" in the folder \"config\".");
				return;
			}
			if(args.length < 1){
				args = new String[2];
				BufferedReader reader = new BufferedReader(new FileReader(file));
				while (reader.ready()){
					final String line = reader.readLine();
					if(line.length() < 2) continue;
					final String[] option = line.split("=");
					if(option.length == 1 && option[0].length() == 0){
						reader.close();
						logger.logThrowable(new IllegalArgumentException("error to the file \"info.txt\"."));
						return;
					}
					switch (option[0]){
						case "token": args[0] = line.replaceFirst("token=", ""); break;
						case "tag": args[1] = line.replaceFirst("tag=", ""); break;
					}
				}
				reader.close();
			}
			
			nBot = new NBot(args[0], args.length < 2 ? "selfbot." : args[1]);
			nBot.pluginManager.loadPlugins();
		}catch(Exception exception){
			logger.logThrowable(exception);
		}
	}
}
