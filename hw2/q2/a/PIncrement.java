package q2.a;

import java.util.ArrayList;


public class PIncrement implements Runnable{	
	static int c;
	static volatile int turn = -1;
	int id;
	int work;
	
	public PIncrement(int work, int id) {
		this.work = work;
		this.id = id;
	}

    public static int parallelIncrement(int c, int numThreads) {
		ArrayList<Thread> threads = new ArrayList<>();
		int [] work = new int [numThreads];
		
		
		PIncrement.c = c;
		
		/* Initialize array to 0 */
		for(int i = 0; i < numThreads; i++) {
			work[i] = 0;
		}
		
		for(int i = 0; i < 120000; i++) {
			work[i%numThreads]++;
		}   
		
		/* Create and run all threads */
		for(int i = 0; i < numThreads; i++) {
			Thread t = new Thread(new PIncrement(work[i], i));
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
    
    public void requestCS(int i) {
    	while(true) {
    		while(turn != -1) {};
    		turn = i;
    		try {Thread.sleep(0, 10000);}
    		catch(InterruptedException e) {};
    		if(turn == i) return;		
    	}
    }
    
    public void releaseCS(int i) {
    	turn = -1;	
    }

	@Override
	public void run() {
		for(int i = 0; i < work; i++) {
			requestCS(id);
			c++;
			releaseCS(id);		
		}
	}

}
