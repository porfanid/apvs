package ch.cern.atlas.apvs.ptu.daq.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PtuDaqQueue extends Thread {

	private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	private List<PtuDaqListener> listeners;

	public PtuDaqQueue() {
		listeners = new ArrayList<PtuDaqListener>();
	}
	
	public void addListener(PtuDaqListener l) {
		listeners.add(l);
	}
	
	public void removeListener(PtuDaqListener l) {
		listeners.remove(l);
	}
	
	public boolean add(String item) {
		return queue.add(item);
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				String item = queue.take();
				for (Iterator<PtuDaqListener> i = listeners.iterator(); i.hasNext(); ) {
					PtuDaqListener listener = i.next();
					try {
						listener.itemAvailable(item);
					} catch (IOException e) {
						System.err.println(e);
						i.remove();
					}
				}
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
	}
}
