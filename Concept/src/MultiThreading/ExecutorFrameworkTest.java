package MultiThreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorFrameworkTest {

	public static void main(String[] args) {
		
		
//		ThreadPoolExecutor exec = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
		//Creating a Thread Pool Using a Thread Pool Executor

		//Creating a manual Thread Pool with ExecutorService and submitting the task	
//		ExecutorService exe = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
//				Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
//		
//		for (int i =0 ; i <20;i++) {
//			final int taskId = i;
//			exe.submit(() -> {
//				System.out.println("Task " + taskId + " performed by "+ Thread.currentThread().getName());
//				try {
//					Thread.sleep(3000);
//				} catch(InterruptedException  ex) {
//					System.out.println(" Error Occured = " +  ex);
//				}
//				
//				exe.shutdown();
//			});
//		}
		
//		Potential Issues
//		Task Rejection:
//		The pool can only handle 4 active threads initially, and 6 threads at most.
//		The queue can only store 10 tasks.
//		When all 16 slots (6 running threads + 10 queued tasks) are occupied, the remaining tasks will be rejected, leading to RejectedExecutionException.		
		
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		ExecutorService executorCache = Executors.newCachedThreadPool();
		
		
		List<Future<String>> futures = new ArrayList<>();

		for (int i =0; i<100; i++) {
			final int taskId = i;

			futures.add(executorCache.submit(() -> {
//				System.out.println("Executing "+taskId);
				return "Task " + taskId + " performed by "+ Thread.currentThread().getName() ;
			}));
		}
			for (Future<String> future : futures) {
				try {
					System.out.println(future.get());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		
		executor.shutdown();
		
//		List<Future<String>> futures = new ArrayList<>();
//
//		for (int i =0; i<100; i++) {
//			final int taskId = i;
//			Future<String> future = executor.submit(new Callable<String> () {
//
//				@Override
//				public String call() {
//					System.out.println("Executing");
//					return "Task " + taskId + " performed by "+ Thread.currentThread().getName() ;
//				}
//				
//			});
//			try {
//				System.out.println(future.get());
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}

	}

}
