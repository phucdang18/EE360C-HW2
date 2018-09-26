package q3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monkey {
	
	static int leftNum = 0;
	static int rightNum = 0;
	static boolean kongFlag = false;
	static boolean kongWants = false;
	ReentrantLock lock = new ReentrantLock();
	Condition full = lock.newCondition();
	Condition kong = lock.newCondition();
	

    public Monkey() {

    }

    public void ClimbRope(int direction) throws InterruptedException {
    	lock.lock();
    	try {    	
    		// Left Monkey
    		if(direction == 0) {
    			while(rightNum > 0 || (leftNum == 3) || kongWants) {full.await();}
    			leftNum++;
    		}
    		// Right Monkey
    		else if (direction == 1){
    			while(leftNum > 0 || (rightNum == 3) || kongWants) {full.await();}
    			rightNum++;
    		}
    		// Kong
    		else {
    			kongWants = true;
    			while(leftNum + rightNum > 0) {kong.await();}
    			kongFlag = true;
    		}
    			
    		
    	} finally {
    		lock.unlock();
    	}
    	

    }

    public void LeaveRope() {
    	try {
    		lock.lock();
    		if(leftNum > 0) {
    			leftNum--;
    			if(kongWants) {
    				kong.signal();
    			}
    			else {
    				full.signalAll();
    			}
    		}
    		else if(rightNum > 0){
    			rightNum--;
    			if(kongWants) {
    				kong.signal();
    			}
    			else {
    				full.signalAll();
    			}
    		}
    		else {
    			kongWants = false;
    			kongFlag = false;
    			full.signalAll();
    		}
    					
    		
    	} finally {
    		lock.unlock();
    	}

    }

    /**
     * Returns the number of monkeys on the rope currently for test purpose.
     *
     * @return the number of monkeys on the rope
     *
     * Positive Test Cases:
     * case 1: when normal monkey (0 and 1) is on the rope, this value should <= 3, >= 0
     * case 2: when Kong is on the rope, this value should be 1
     */
    public int getNumMonkeysOnRope() {
    	if(kongFlag) {
    		return leftNum + rightNum + 1;
    	}
    	else {
            return leftNum + rightNum; 		
    	}
    }

}
