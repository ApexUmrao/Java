package MultiThreading;

public class ThreadLifecycle extends Thread {
	
	@Override
	public void run() {
			
			try {
				Thread.sleep(400);
				System.out.println("Thread-0 --> RUNNING");
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		ThreadLifecycle t1 = new ThreadLifecycle();
		System.out.println(t1.getName() + " --> " + t1.getState());

		t1.start();
		System.out.println(t1.getName() + " --> " + t1.getState());
		
		t1.sleep(1200);
		System.out.println(t1.getName() + " --> " + t1.getState());
		
		t1.join();
		System.out.println(t1.getName() + " --> " + t1.getState());

	}
}
