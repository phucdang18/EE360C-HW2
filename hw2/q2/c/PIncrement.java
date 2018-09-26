package q2.c;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class PIncrement implements Runnable{
	static int c;
	volatile int work;
	static int numThreads;
	
	static AtomicInteger tailslot = new AtomicInteger(0);
	static volatile boolean [] Available;
	volatile ThreadLocal<Integer> myslot = new ThreadLocal<Integer> () {
		protected Integer initialValue () {
			return 0;
		}
	};	

	
	public PIncrement(int work) {
		this.work = work;
	}

    public static int parallelIncrement(int c, int numThreads) {
		ArrayList<Thread> threads = new ArrayList<>();
		int [] work = new int [numThreads];
		
		PIncrement.c = c;
		PIncrement.numThreads = numThreads;
		
		Available = new boolean[numThreads];
		
		/* Initialize array to 0 */
		for(int i = 0; i < numThreads; i++) {
			work[i] = 0;
			if(i == 0) {
				Available[i] = true;
			}
			else {
				Available[i] = false;
			}
		}
		
		for(int i = 0; i < 120000; i++) {
			work[i%numThreads]++;
		}   
		
		/* Create and run all threads */
		for(int i = 0; i < numThreads; i++) {
			Thread t = new Thread(new PIncrement(work[i]));
			threads.add(t);
			t.start();		
		}
		
		
		/* Wait for all threads to finish */
		for(int i = 0; i < numThreads; i++) {
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
        return PIncrement.c;
    }
    
    
    public void requestCS() {
    	myslot.set(tailslot.getAndIncrement()%numThreads);
    	while(!Available[myslot.get()]) {}; 	
    }
    
    public void releaseCS() {
    	Available[myslot.get()] = false;
    	Available[(myslot.get()+1)%numThreads] = true; 	
    }
    

	@Override
	public void run() {
		for(int i = 0; i < work; i++) {
			requestCS();	    	

			c++;
			releaseCS();		
		}
		
	}

}
