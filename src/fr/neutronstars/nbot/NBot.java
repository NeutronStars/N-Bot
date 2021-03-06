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
import fr.neutronstars.nbot.runnable.NBotRunnable;
import fr.neutronstars.nbot.runnable.TaskManager;
import fr.neutronstars.nbot.server.ServerBot;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;

/**
 * Main Class.
 * @author NeutronStars
 * @version 1.1.3
 * @since 1.0.0
 */

public final class NBot implements Runnable{

	private static NBot nBot;
	
	/**
	 * Retrieves the instance of the class NBot.
	 * @return NBot
	 * @since 1.0.0
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
	private final String play, version = "1.1.3";
	private final CommandMap commandMap;
	private final JDA jda;
	
	/**
	 * Tasks List
	 * @since 1.1.1
	 */
	private final TaskManager tasks = new TaskManager();
	
	private boolean running;
	
	private NBot(String token, String tag, String play) throws Exception{
		this.play = play;
		commandMap = new CommandMap(tag);
		pluginManager = new PluginManager(commandMap);
		jda = new JDABuilder(AccountType.BOT).setToken(token).buildAsync();
		jda.addEventListener(new BotListener(commandMap));
	}
	
	/**
	 * Retrieves the instance of the class JDA.
	 * @return {@link JDA}
	 * @since 1.0.0
	 */
	public JDA getJDA() {
		return jda;
	}

	/**
	 * Get API version 
	 * @return version
	 * @since 1.1.0
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * Get the Console.
	 * @return {@link ConsoleEntity}
	 * @since 1.1.0
	 */
	public ConsoleEntity getConsoleEntity() {
		return consoleEntity;
	}

	/**
	 * Get default game
	 * @return play
	 * @since 1.1.3
	 */
	public String getPlay() {
		return play;
	}

	/**
	 * Retrieves the instance of the class PluginManager.
	 * @return {@link PluginManager}
	 * @since 1.0.0
	 */
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	/**
	 * Retrieves a class ServerBot.
	 * @param guild
	 * @return {@link ServerBot}
	 * @since 1.0.0
	 */
	public ServerBot getServer(Guild guild){
		return loadGuild(guild);
	}
	
	/**
	 * Retrieves the tag of command.
	 * @return String
	 * @since 1.0.0
	 */
	public String getCommandTag() {
		return commandMap.getTag();
	}
	
	/**
	 * Load a instance of ServerBot.
	 * @param guild
	 * @return {@link ServerBot}
	 * @since 1.0.0
	 */
	public ServerBot loadGuild(Guild guild){
		if(!servers.containsKey(guild.getId())) servers.put(guild.getId(), new ServerBot(guild));
		return servers.get(guild.getId());
	}
	
	/**
	 * The nextInt of the class {@link Random}
	 * @param index
	 * @return Integer
	 * @since 1.0.0
	 */
	public int nextInt(int index){
		return random.nextInt(index);
	}
	
	/**
	 * Start the application.
	 * @since 1.0.0
	 */
	public void start(){
		if(running) return;
		running = true;
		thread.start();
		tasks.start();
	}
	
	/**
	 * Stop the application.
	 * @since 1.0.0
	 */
	public void stop(){
		if(!running) return;
		running = false;
		tasks.stop();
	}
	
	/**
	 * Register NBotRunnable
	 * @param runnable
	 * @since 1.1.1
	 */
	public void registerRunnable(NBotRunnable runnable){
		tasks.registerRunnable(runnable);
	}
	
	/**
	 * Run task
	 * @param runnable
	 * @since 1.1.3
	 */
	public void runTaskTimer(NBotRunnable runnable, long repeat) {
		tasks.runTaskTimer(runnable, repeat);
	}

	/**
	 * Run task
	 * @param runnable
	 * @since 1.1.3
	 */
	public void runTaskLater(NBotRunnable runnable, long delay) {
		tasks.runTaskLater(runnable, delay);
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
				writer.write("play=\n");
				writer.flush();
				writer.close();
				logger.log("Please, complete the file \"info.txt\" in the folder \"config\".");
				return;
			}
			if(args.length < 1){
				args = new String[3];
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
						case "play": args[2] = line.replaceFirst("play=", ""); break;
					}
				}
				reader.close();
			}
			
			nBot = new NBot(args[0], args.length < 2 ? "bot." : args[1], args.length < 3 ? null : args[2].equalsIgnoreCase("") ? null : args[2]);
			nBot.pluginManager.loadPlugins();
		}catch(Exception exception){
			logger.logThrowable(exception);
		}
	}
}
