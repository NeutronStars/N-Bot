package fr.neutronstars.nbot.runnable;

import java.util.ArrayList;
import java.util.List;

/**
 * TaskManager
 * @author NeutronStars
 * @version 1.0.0
 * @since 1.1.1
 */
public final class TaskManager implements Runnable{

	private final List<NBotRunnable> tasks = new ArrayList<>();
	private final Thread thread = new Thread(this);
	private boolean running;
	
	public void registerRunnable(NBotRunnable runnable){
		if(!tasks.contains(runnable)) tasks.add(runnable);
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
		long lns = System.nanoTime();
		double ns = 1000000000/10;
		while (running){
			if(System.nanoTime() - lns > ns){
				lns+=ns;
				for(NBotRunnable runnable : new ArrayList<>(tasks)){
					runnable.addTicks();
					runnable.update();
					if(runnable.isClosed()) tasks.remove(runnable);
				}
			}
		}
	}
	
	public void runTaskTimer(NBotRunnable runnable, long repeat) {
		runnable.runTaskTimer(repeat);
	}

	public void runTaskLater(NBotRunnable runnable, long delay) {
		runnable.runTaskLater(delay);
	}
}
