package p2p.filesharing;

import java.util.concurrent.BlockingQueue;

public class TaskExecution implements Runnable {

	private BlockingQueue<Runnable> tasks;

	public TaskExecution(BlockingQueue<Runnable> tasks) {
		this.tasks = tasks;
	}
	
	@Override
	public void run() {
		while(true) {
			Runnable p = null;
			try {
				p = tasks.take();
			} catch (InterruptedException e) {
				continue;
			}

			p.run();
		}
	}

}
