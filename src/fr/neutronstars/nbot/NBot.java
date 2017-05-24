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
 */

public final class NBot implements Runnable{

	private static NBot nBot;
	
	public static NBot getNBot() {
		return nBot;
	}
	
	private final Map<String, ServerBot> servers = new HashMap<>();
	private final Thread thread = new Thread(this, "nbot");
	private final Scanner scanner = new Scanner(System.in);
	private final Random random = new Random();
	private final PluginManager pluginManager;
	private final CommandMap commandMap;
	private final JDA jda;
	
	private boolean running;
	
	private NBot(String token, String tag) throws Exception{
		commandMap = new CommandMap(tag);
		pluginManager = new PluginManager(commandMap);
		jda = new JDABuilder(AccountType.BOT).setToken(token).buildAsync();
		jda.addEventListener(new BotListener(commandMap));
	}
	
	public JDA getJDA() {
		return jda;
	}
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	public ServerBot getServer(Guild guild){
		return loadGuild(guild);
	}
	
	public String getCommandTag() {
		return commandMap.getTag();
	}
	
	public ServerBot loadGuild(Guild guild){
		if(!servers.containsKey(guild.getId())) servers.put(guild.getId(), new ServerBot(guild));
		return servers.get(guild.getId());
	}
	
	public int nextInt(int index){
		return random.nextInt(index);
	}
	
	public void start(){
		if(running) return;
		running = true;
		thread.start();
	}
	
	public void stop(){
		if(!running) return;
		running = false;
	}
	
	public void run() {
		while (running){
			if(scanner.hasNextLine()) commandMap.commandConsole(scanner.nextLine());
		}
		
		NBotLogger.LOGGER.log("Stopping a bot...\nGuilds saving...");
		servers.values().forEach(s->s.save());
		NBotLogger.LOGGER.log("Guilds saved.");
		jda.shutdown(false);
		NBotLogger.LOGGER.log("Bot stopped.");
		NBotLogger.LOGGER.close();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		NBotLogger logger = NBotLogger.LOGGER;
		try{
			File folder = new File("config");
			if(!folder.exists()) folder.mkdirs();
			File file = new File(folder, "info.txt");
			if(!file.exists() && args.length < 1){
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.write("token=Insert your token\n");
				writer.write("tag=selfbot.\n");
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
