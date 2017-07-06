package fr.neutronstars.nbot.runnable;

import fr.neutronstars.nbot.NBot;

/**
 * Class Runnable.
 * 1 Ticks = 1/10s
 * @author NeutronStars
 * @version 1.1.1
 * @since 1.1.1
 */
public abstract class NBotRunnable implements Runnable{

	private boolean running, delete, later;
	private long ticksUpdate, ticks;
	
	public NBotRunnable(){
		NBot.getNBot().registerRunnable(this);
	}
	
	public final boolean isRunning() {
		return running;
	}
	
	public final boolean canUpdate(){
		return isRunning() && ticks >= ticksUpdate;
	}
	
	public final void update(){
		if(canUpdate()){
			ticks = 0;
			run();
			if(later) cancel();
		}
	}
	
	public final void addTicks(){
		if(isRunning()) ticks++;
	}
	
	public void start(){
		running = true;
	}

	public void runTaskTimer(long repeat){
		ticksUpdate = repeat;
		start();
	}

	public void runTaskLater(long delay){
		ticksUpdate = delay;
		later = true;
		start();
	}
	
	public final void stop(){
		running = false;
	}
	
	public final void cancel(){
		stop();
		close();
	}
	
	public final boolean isClosed(){
		return delete;
	}
	
	public final void close(){
		delete = true;
	}
}
