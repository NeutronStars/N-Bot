# __**[** *N-Bot API* **]**__

N-Bot API is an __*open-source project*__ using **JDA** which will allow you **create/personalize** your bots simplest through a plugin system.

For use it download the [latest release](https://github.com/NeutronStars/N-Bot/releases) and execute the following command `java -jar N-Bot-VERSION-withDependencies-JDA-VERSION.jar` a **config** folder will appear who contains an **info.txt** file. Open it and insert your bot **Token**, now you can re-execute the previous command, folders are going to generate. When you want to stop the bot, just print `stop` in the console.

For create a **plugin**, add the **N-Bot API** on your project libraries, your main class will need to extends **NBotPlugin** who contains `onLoad()` and `onDisable()` methods with `@Override` annotation.

```java
public class MyPlugin extends NBotPlugin{

  @Override
  public void onLoad(){
     NBotLogger.getLogger().log("MyPlugin is loaded.");
  }

  @Override
  public void onDisable(){
     NBotLogger.getLogger().log("MyPlugin is disabled.");
  }
}
```

You can too create commands, create a class who implements **CommandManager**  and insert methods with **@Command** annotation like this.

```java
import fr.neutronstars.nbot.entity.User;
import fr.neutronstars.nbot.entity.Channel;
import fr.neutronstars.nbot.entity.Message;

public class MyCommand implements CommandManager{

  @Command(name="stop",description="Stop the Bot.",type=Executor.CONSOLE)
  public void onStop(){
     NBot.getNBot().getJDA().shutdown(false);
     System.exit(0);
  }
  @Command(name="info",description="Shows the bot informations.",type=Executor.USER,permission=Permission.OPERATOR,executePrivate=true)
  public void onInfo(User user, Channel channel, Message message){
      //Your Code.
  }
}
```

and register your command class in the `onLoad()` method like this.

```java
public class MyPlugin extends NBotPlugin{

  @Override
  public void onLoad(){
     NBotLogger.getLogger().log("MyPlugin is loaded.");
     super.registerCommand(new MyCommand());
  }
}
```

To ensure that your plugin is **valid** you will also have to add a **plugin.txt** at your root.

```
main=packages.MainClass
name=My Plugin
version=0.0.1-SNAPSHOT
```

Generate your `.jar` and put it in the **plugins** folder.
