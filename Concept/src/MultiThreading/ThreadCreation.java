package MultiThreading;

public class ThreadCreation extends MyRunnable {
	
	public static void main(String[] args) {
		
		MyRunnable r1 = new MyRunnable();
	//	r1.run(); //This will not create a new thread, it will just execute the run() method in the main thread
		
		
        // 1. Default constructor
        Thread t1 = new Thread();
        System.out.println("Thread t1 (default): " + t1.getName());

        
        // 2. Thread(Runnable target)
        Thread t2 = new Thread(r1);
        t2.setName("RunnableThread");
        t2.start();

        
        // 3. Thread(Runnable target, String name)
        Thread t3 = new Thread(r1, "Worker-1");
        t3.start();

        
        // 4. Thread(String name)
        Thread t4 = new Thread("HelperThread");
        System.out.println("Thread t4 (only name): " + t4.getName());

        
        // 5. Thread(ThreadGroup group, Runnable target)
        ThreadGroup g1 = new ThreadGroup("GroupA");
        Thread t5 = new Thread(g1, r1);
        t5.setName("GroupThread-1");
        t5.start();

        
        // 6. Thread(ThreadGroup group, Runnable target, String name)
        ThreadGroup g2 = new ThreadGroup("GroupB");
        Thread t6 = new Thread(g2,r1, "GroupThread-2");
        t6.start();

        
        // 7. Thread(ThreadGroup group, Runnable target, String name, long stackSize)
        ThreadGroup g3 = new ThreadGroup("GroupC");
        Thread t7 = new Thread(g3, r1, "GroupThread-3", 1024);
        t7.start();
		
        
	}

}
