package q2.b;

import java.util.ArrayList;

public class PIncrement implements Runnable{
	static int c;
	int id;
	int work;
	
	static volatile int x = -1;
	static volatile int y = -1;
	static volatile boolean flag[];		// True == UP, False == DOWN
	
	public PIncrement(int work, int id) {
		this.work = work;
		this.id = id;
	}

    public static int parallelIncrement(int c, int numThreads) {
		ArrayList<Thread> threads = new ArrayList<>();
		int [] work = new int [numThreads];
			
		PIncrement.c = c;
		
		flag = new boolean[numThreads];
		
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
    		flag[i] = true;
    		x = i;
    		if(y != -1) {
    			flag[i] = false;
    			while(y != -1) {};
    			continue;
    		}
    		else {
    			y = i;
    			if(x == i) {
    				return;
    			}
    			else {
    				flag[i] = false;
    				for(int j = 0; j < flag.length; j++) {
    					while(flag[j] != false) {};
    				}
    				if(y == i) {
    					return;
    				}
    				else {
    					while(y!= -1) {};
    					continue;
    				}
    			}
    		}
    		
    		
    	}
    	
    	
    }
    
    public void releaseCS(int i) {
    	y = -1;
    	flag[i] = false;
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
