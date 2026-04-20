package MultiThreading;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

/**
 * PRODUCTION-LEVEL THREAD CASES
 * Critical threading patterns and anti-patterns for enterprise applications
 * 
 * These examples demonstrate:
 * 1. Race Conditions & Thread Safety
 * 2. Deadlock Scenarios
 * 3. Starvation & Fairness
 * 4. Memory Visibility Issues
 * 5. Thread Pool Best Practices
 * 6. Proper Synchronization Techniques
 * 7. Lock Ordering & Reentrance
 * 8. Volatile vs Synchronized
 */
public class ThreadProductionCase {

	// ============================================================================
	// CASE 1: RACE CONDITION - UNSAFE COUNTER (Production Anti-Pattern)
	// ============================================================================
	/**
	 * PROBLEM: Multiple threads incrementing counter without synchronization
	 * RESULT: Lost updates due to non-atomic read-modify-write operation
	 * FIX: Use synchronized, AtomicInteger, or locks
	 */
	static class UnsafeCounter {
		private int count = 0;
		
		// UNSAFE - Don't use in production
		public void incrementUnsafe() {
			count++; // NOT ATOMIC: read, add 1, write (3 steps)
		}
		
		public int getCountUnsafe() {
			return count;
		}
	}
	
	// SOLUTION 1: Synchronized Method
	static class SynchronizedCounter {
		private int count = 0;
		
		public synchronized void increment() {
			count++; // Thread-safe
		}
		
		public synchronized int getCount() {
			return count;
		}
	}
	
	// SOLUTION 2: AtomicInteger (Recommended for simple counters)
	static class AtomicCounter {
		private AtomicInteger count = new AtomicInteger(0);
		
		public void increment() {
			count.incrementAndGet(); // Lock-free, high performance
		}
		
		public int getCount() {
			return count.get();
		}
	}
	
	// SOLUTION 3: Synchronized Block (Finer-grained control)
	static class SynchronizedBlockCounter {
		private int count = 0;
		private final Object lock = new Object();
		
		public void increment() {
			synchronized(lock) {
				count++;
			}
		}
		
		public int getCount() {
			synchronized(lock) {
				return count;
			}
		}
	}
	
	// ============================================================================
	// CASE 2: DEADLOCK - CIRCULAR LOCK DEPENDENCY
	// ============================================================================
	/**
	 * PROBLEM: Two threads acquiring locks in opposite order
	 * RESULT: Both threads wait forever - DEADLOCK
	 * FIX: Always acquire locks in same order, use timeouts, or ReentrantLock
	 */
	static class Account {
		private long balance;
		private final String id;
		
		public Account(String id, long balance) {
			this.id = id;
			this.balance = balance;
		}
		
		// UNSAFE - Deadlock Risk
		public synchronized void transferTo(Account recipient, long amount) throws InterruptedException {
			if (this.balance >= amount) {
				this.balance -= amount;
				// DANGER: If recipient.transfer() tries to lock 'this', DEADLOCK!
				Thread.sleep(100); // Simulate processing
				synchronized(recipient) {
					recipient.balance += amount;
				}
			}
		}
		
		// SAFE SOLUTION 1: Lock ordering by ID
		public void safeTransferLockOrdering(Account recipient, long amount) {
			Account first, second;
			if (this.id.compareTo(recipient.id) < 0) {
				first = this;
				second = recipient;
			} else {
				first = recipient;
				second = this;
			}
			
			synchronized(first) {
				synchronized(second) {
					if (this.balance >= amount) {
						this.balance -= amount;
						recipient.balance += amount;
					}
				}
			}
		}
		
		// SAFE SOLUTION 2: Timeout with ReentrantLock
		private final ReentrantLock lock = new ReentrantLock();
		
		public boolean safeTransferWithTimeout(Account recipient, long amount, long timeoutMs) 
				throws InterruptedException {
			long deadline = System.currentTimeMillis() + timeoutMs;
			
			if (!this.lock.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
				return false; // Timeout - avoid deadlock
			}
			
			try {
				long remainingTime = deadline - System.currentTimeMillis();
				if (remainingTime <= 0 || !recipient.lock.tryLock(remainingTime, TimeUnit.MILLISECONDS)) {
					return false; // Timeout on second lock
				}
				
				try {
					if (this.balance >= amount) {
						this.balance -= amount;
						recipient.balance += amount;
						return true;
					}
					return false;
				} finally {
					recipient.lock.unlock();
				}
			} finally {
				this.lock.unlock();
			}
		}
	}
	
	// ============================================================================
	// CASE 3: THREAD STARVATION - Fair Lock Acquisition
	// ============================================================================
	/**
	 * PROBLEM: Some threads never get chance to acquire lock
	 * RESULT: Indefinite starvation of low-priority threads
	 * FIX: Use fair ReentrantLock or minimize lock hold time
	 */
	static class SemaphoreService {
		private final Semaphore unfairSemaphore = new Semaphore(1, false);
		private final Semaphore fairSemaphore = new Semaphore(1, true);
		
		// May starve some threads
		public void unfairAccess() throws InterruptedException {
			unfairSemaphore.acquire();
			try {
				// Critical section
				Thread.sleep(10);
			} finally {
				unfairSemaphore.release();
			}
		}
		
		// Ensures fair FIFO access
		public void fairAccess() throws InterruptedException {
			fairSemaphore.acquire();
			try {
				// Critical section
				Thread.sleep(10);
			} finally {
				fairSemaphore.release();
			}
		}
	}
	
	// ============================================================================
	// CASE 4: MEMORY VISIBILITY ISSUE - VOLATILE vs SYNCHRONIZED
	// ============================================================================
	/**
	 * PROBLEM: Thread cache inconsistency - writes not visible to other threads
	 * SOLUTION 1: volatile - ensures visibility, not atomicity
	 * SOLUTION 2: synchronized - ensures both atomicity and visibility
	 * SOLUTION 3: AtomicReference - combines both
	 */
	static class VisibilityIssue {
		// UNSAFE: No guarantee of visibility
		private boolean flag = false;
		
		// SAFE: Volatile ensures visibility (but not atomicity)
		private volatile boolean volatileFlag = false;
		
		// SAFE: Synchronized ensures both visibility and atomicity
		private boolean syncFlag = false;
		
		public void setFlagVolatile() {
			volatileFlag = true; // Visible to all threads immediately
		}
		
		public boolean getFlagVolatile() {
			return volatileFlag;
		}
		
		public synchronized void setSyncFlag() {
			syncFlag = true;
		}
		
		public synchronized boolean getSyncFlag() {
			return syncFlag;
		}
	}
	
	// ============================================================================
	// CASE 5: THREAD POOL MISCONFIGURATION
	// ============================================================================
	/**
	 * PROBLEM: Wrong executor configuration causes resource exhaustion or deadlock
	 * SOLUTION: Right executor type for workload
	 */
	static class ThreadPoolExample {
		// For CPU-bound tasks: fixed pool size = CPU cores
		static ExecutorService cpuBoundPool = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors()
		);
		
		// For I/O-bound tasks: scalable pool
		static ExecutorService ioBoundPool = Executors.newCachedThreadPool();
		
		// For scheduled tasks: scheduled pool
		static ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(10);
		
		// BEST PRACTICE: Custom ThreadPoolExecutor for control
		static ExecutorService customPool = new ThreadPoolExecutor(
			10,                          // Core threads
			50,                          // Max threads
			60, TimeUnit.SECONDS,        // Keep-alive time
			new LinkedBlockingQueue<>(1000),  // Bounded queue prevents OOM
			new ThreadPoolExecutor.CallerRunsPolicy() // Rejection policy
		);
		
		public void submitTask() {
			customPool.submit(() -> {
				// Task execution
			});
		}
	}
	
	// ============================================================================
	// CASE 6: DOUBLE-CHECKED LOCKING (Lazy Initialization)
	// ============================================================================
	/**
	 * PROBLEM: Thread-safe lazy initialization without always holding lock
	 * SOLUTION: Volatile + double-checked locking OR eager initialization
	 */
	static class Singleton {
		// UNSAFE: May create multiple instances due to visibility issues
		private static Singleton unsafeInstance;
		
		// SAFE: Volatile for visibility
		private static volatile Singleton instance;
		
		private Singleton() {}
		
		// UNSAFE: Every call acquires lock
		public static synchronized Singleton getInstanceSlow() {
			if (unsafeInstance == null) {
				unsafeInstance = new Singleton();
			}
			return unsafeInstance;
		}
		
		// SAFE: Double-checked locking (faster after first initialization)
		public static Singleton getInstance() {
			if (instance == null) { // First check (no lock)
				synchronized(Singleton.class) {
					if (instance == null) { // Second check (with lock)
						instance = new Singleton();
					}
				}
			}
			return instance;
		}
		
		// SAFEST: Eager initialization
		private static final Singleton eagerInstance = new Singleton();
		
		public static Singleton getEagerInstance() {
			return eagerInstance;
		}
	}
	
	// ============================================================================
	// CASE 7: PRODUCER-CONSUMER WITH QUEUE (Most Common in Production)
	// ============================================================================
	/**
	 * PATTERN: Safe data exchange between threads
	 * USE: BlockingQueue, ArrayBlockingQueue, LinkedBlockingQueue
	 */
	static class ProducerConsumer {
		static class Message {
			String content;
			Message(String content) { this.content = content; }
		}
		
		// UNSAFE: Manual synchronization
		static class UnsafeQueue {
			private Queue<Message> queue = new LinkedList<>();
			
			public void produce(Message msg) throws InterruptedException {
				synchronized(queue) {
					queue.add(msg);
					queue.notifyAll(); // Wake up consumers
				}
			}
			
			public Message consume() throws InterruptedException {
				synchronized(queue) {
					while (queue.isEmpty()) {
						queue.wait(); // Release lock and wait
					}
					return queue.poll();
				}
			}
		}
		
		// SAFE: BlockingQueue (Recommended)
		static class SafeQueue {
			private BlockingQueue<Message> queue = new LinkedBlockingQueue<>(100);
			
			public void produce(Message msg) throws InterruptedException {
				queue.put(msg); // Blocks if queue full
			}
			
			public Message consume() throws InterruptedException {
				return queue.take(); // Blocks if queue empty
			}
		}
	}
	
	// ============================================================================
	// CASE 8: CALLABLE vs RUNNABLE - Future Based Execution
	// ============================================================================
	/**
	 * PROBLEM: Need return value and exception handling from thread
	 * SOLUTION: Use Callable + Future instead of Runnable
	 */
	static class FutureExample {
		static class DataService {
			public String fetchData(String id) throws Exception {
				// Simulate I/O delay
				Thread.sleep(1000);
				if (id.equals("error")) {
					throw new IOException("Data not found");
				}
				return "Data: " + id;
			}
		}
		
		// UNSAFE: Runnable - no return value, exceptions lost
		public void unsafeApproach() {
			ExecutorService executor = Executors.newFixedThreadPool(5);
			executor.execute(() -> {
				try {
					DataService service = new DataService();
					String result = service.fetchData("123");
					System.out.println(result); // Cannot propagate to caller
				} catch (Exception e) {
					e.printStackTrace(); // Silent failure
				}
			});
		}
		
		// SAFE: Callable + Future
		public void safeApproach() throws Exception {
			ExecutorService executor = Executors.newFixedThreadPool(5);
			
			Future<String> future = executor.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					DataService service = new DataService();
					return service.fetchData("123");
				}
			});
			
			try {
				String result = future.get(5, TimeUnit.SECONDS); // Get with timeout
				System.out.println(result);
			} catch (TimeoutException e) {
				future.cancel(true); // Cancel if taking too long
				System.out.println("Operation timed out");
			} catch (ExecutionException e) {
				System.out.println("Task failed: " + e.getCause());
			}
		}
	}
	
	// ============================================================================
	// CASE 9: THREAD INTERRUPTION - Graceful Shutdown
	// ============================================================================
	/**
	 * PROBLEM: How to stop threads gracefully
	 * SOLUTION: Use Thread.interrupt() and check InterruptedException
	 */
	static class InterruptionExample {
		// UNSAFE: Cannot stop
		static class UnstoppableWorker implements Runnable {
			@Override
			public void run() {
				while (true) { // Infinite loop - cannot stop
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// Ignoring interrupt - BAD!
					}
				}
			}
		}
		
		// SAFE: Respects interruption
		static class InterruptibleWorker implements Runnable {
			@Override
			public void run() {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						// Do work
						Thread.sleep(1000);
						
						// Check interruption flag
						if (Thread.currentThread().isInterrupted()) {
							break;
						}
					}
				} catch (InterruptedException e) {
					// Restore interrupt status
					Thread.currentThread().interrupt();
				}
			}
		}
		
		public void example() {
			ExecutorService executor = Executors.newFixedThreadPool(1);
			Future<?> future = executor.submit(new InterruptibleWorker());
			
			// Graceful shutdown
			try {
				future.get(5, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				future.cancel(true); // Send interrupt signal
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// ============================================================================
	// CASE 10: THREAD-LOCAL STORAGE - Connection Pooling Pattern
	// ============================================================================
	/**
	 * PROBLEM: Each thread needs its own instance (DB connection, Session)
	 * SOLUTION: ThreadLocal - one value per thread
	 * WARNING: Must clean up in try-finally or memory leak occurs
	 */
	static class ConnectionPool {
		static class Connection {
			String id;
			Connection(String id) { this.id = id; }
		}
		
		private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
		
		public static Connection getConnection() {
			Connection conn = connectionHolder.get();
			if (conn == null) {
				conn = new Connection("conn-" + Thread.currentThread().getId());
				connectionHolder.set(conn);
			}
			return conn;
		}
		
		// CRITICAL: Must call in finally block to prevent memory leak
		public static void closeConnection() {
			connectionHolder.remove(); // Essential cleanup
		}
		
		public static void executeQuery() {
			try {
				Connection conn = getConnection();
				// Use connection
				System.out.println("Using: " + conn.id);
			} finally {
				closeConnection(); // MUST DO THIS
			}
		}
	}
	
	// ============================================================================
	// CASE 11: CONCURRENT COLLECTIONS - Thread-Safe Collections
	// ============================================================================
	/**
	 * PROBLEM: Synchronized collections vs concurrent collections
	 * SOLUTION: Use ConcurrentHashMap, CopyOnWriteArrayList for performance
	 */
	static class ConcurrentCollectionsExample {
		// SLOW: Synchronizes entire collection
		Map<String, String> synchronizedMap = Collections.synchronizedMap(new HashMap<>());
		
		// FAST: Fine-grained locking with segments (ConcurrentHashMap)
		Map<String, String> concurrentMap = new ConcurrentHashMap<>();
		
		// SLOW: Synchronizes entire list
		List<String> synchronizedList = Collections.synchronizedList(new ArrayList<>());
		
		// FAST: Copy-on-write (good for read-heavy workloads)
		List<String> copyOnWriteList = new CopyOnWriteArrayList<>();
		
		public void demonstration() {
			// ConcurrentHashMap allows multiple threads to write simultaneously
			ExecutorService executor = Executors.newFixedThreadPool(10);
			for (int i = 0; i < 100; i++) {
				final int index = i;
				executor.submit(() -> {
					concurrentMap.put("key" + index, "value" + index);
				});
			}
		}
	}
	
	// ============================================================================
	// CASE 12: LOCKS & CONDITIONS - Advanced Synchronization
	// ============================================================================
	/**
	 * PROBLEM: Complex synchronization beyond synchronized keyword
	 * SOLUTION: ReentrantLock with Conditions
	 */
	static class LockConditionExample {
		private final ReentrantLock lock = new ReentrantLock();
		private final Condition notEmpty = lock.newCondition();
		private final Condition notFull = lock.newCondition();
		private final Queue<Integer> queue = new LinkedList<>();
		private final int capacity = 10;
		
		public void put(int value) throws InterruptedException {
			lock.lock();
			try {
				while (queue.size() >= capacity) {
					notFull.await(); // Release lock, wait for signal
				}
				queue.add(value);
				notEmpty.signalAll(); // Wake up waiting consumers
			} finally {
				lock.unlock(); // Must unlock in finally
			}
		}
		
		public int take() throws InterruptedException {
			lock.lock();
			try {
				while (queue.isEmpty()) {
					notEmpty.await(); // Release lock, wait for signal
				}
				int value = queue.poll();
				notFull.signalAll(); // Wake up waiting producers
				return value;
			} finally {
				lock.unlock();
			}
		}
	}
	
	// ============================================================================
	// CASE 13: LATCH & BARRIER - Synchronization Utilities
	// ============================================================================
	/**
	 * CountDownLatch: One-time synchronization point (N -> 0)
	 * CyclicBarrier: Reusable synchronization point (round-robin)
	 */
	static class SynchronizationUtilities {
		
		// One-time wait: Start race only after all threads ready
		public void countDownLatchExample() throws InterruptedException {
			int numThreads = 5;
			CountDownLatch startGate = new CountDownLatch(1);
			CountDownLatch endGate = new CountDownLatch(numThreads);
			
			for (int i = 0; i < numThreads; i++) {
				new Thread(() -> {
					try {
						startGate.await(); // Wait for signal to start
						// Do work
						endGate.countDown(); // Signal completion
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}).start();
			}
			
			startGate.countDown(); // Signal all threads to start
			endGate.await(); // Wait for all threads to finish
			System.out.println("All done");
		}
		
		// Reusable barrier: Each round, wait for N threads
		public void cyclicBarrierExample() throws BrokenBarrierException, InterruptedException {
			int numThreads = 5;
			CyclicBarrier barrier = new CyclicBarrier(numThreads, () -> {
				System.out.println("All threads reached barrier");
			});
			
			ExecutorService executor = Executors.newFixedThreadPool(numThreads);
			for (int i = 0; i < numThreads; i++) {
				final int threadId = i;
				executor.submit(() -> {
					try {
						for (int round = 0; round < 3; round++) {
							// Do work
							System.out.println("Thread " + threadId + " round " + round);
							barrier.await(); // Wait for all threads
						}
					} catch (InterruptedException | BrokenBarrierException e) {
						e.printStackTrace();
					}
				});
			}
		}
	}
	
	// ============================================================================
	// CASE 14: ATOMIC OPERATIONS - Lock-Free Programming
	// ============================================================================
	/**
	 * PROBLEM: Need thread-safe operations without locks
	 * SOLUTION: Atomic classes (better performance in high-contention scenarios)
	 */
	static class AtomicOperationsExample {
		private AtomicInteger counter = new AtomicInteger(0);
		private AtomicReference<String> reference = new AtomicReference<>();
		private AtomicLong longValue = new AtomicLong(0);
		
		public void atomicOperations() {
			// Atomic increment
			counter.incrementAndGet();
			
			// Atomic get and set
			int oldValue = counter.getAndSet(100);
			
			// Atomic compare-and-swap (CAS)
			counter.compareAndSet(100, 200); // If value is 100, set to 200
			
			// Reference operations
			reference.set("value");
			String value = reference.get();
			reference.compareAndSet("value", "newValue");
		}
	}
	
	// ============================================================================
	// CASE 15: EXCEPTION HANDLING IN THREADS
	// ============================================================================
	/**
	 * PROBLEM: Exceptions in threads are silent by default
	 * SOLUTION: Use UncaughtExceptionHandler or Future.get()
	 */
	static class ExceptionHandlingExample {
		
		// UNSAFE: Exception is silent
		public void unsafeThreadException() {
			Thread thread = new Thread(() -> {
				throw new RuntimeException("Oops!"); // Lost
			});
			thread.start();
		}
		
		// SAFE: Handle with UncaughtExceptionHandler
		public void safeThreadException() {
			Thread thread = new Thread(() -> {
				throw new RuntimeException("Oops!");
			});
			
			thread.setUncaughtExceptionHandler((t, e) -> {
				System.out.println("Thread " + t.getName() + " threw exception: " + e);
				// Log to monitoring system
			});
			
			thread.start();
		}
		
		// SAFE: Using Future
		public void futureException() throws Exception {
			ExecutorService executor = Executors.newFixedThreadPool(1);
			Future<?> future = executor.submit(() -> {
				throw new RuntimeException("Task failed");
			});
			
			try {
				future.get(); // Rethrows exception
			} catch (ExecutionException e) {
				System.out.println("Caught: " + e.getCause());
			}
		}
	}
	
	// ============================================================================
	// MAIN METHOD - TESTING EXAMPLES
	// ============================================================================
	public static void main(String[] args) throws Exception {
		System.out.println("=== Production-Level Thread Cases ===\n");
		
		// Test Case 1: Race Condition
		System.out.println("1. RACE CONDITION TEST");
		AtomicCounter atomicCounter = new AtomicCounter();
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 1000; i++) {
			executor.submit(atomicCounter::increment);
		}
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.SECONDS);
		System.out.println("Final count (expected 1000): " + atomicCounter.getCount() + "\n");
		
		// Test Case 2: Producer-Consumer
		System.out.println("2. PRODUCER-CONSUMER TEST");
		ProducerConsumer.SafeQueue queue = new ProducerConsumer.SafeQueue();
		ExecutorService executor2 = Executors.newFixedThreadPool(2);
		
		executor2.submit(() -> {
			try {
				for (int i = 0; i < 5; i++) {
					queue.produce(new ProducerConsumer.Message("Message " + i));
					System.out.println("Produced: Message " + i);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		
		executor2.submit(() -> {
			try {
				for (int i = 0; i < 5; i++) {
					ProducerConsumer.Message msg = queue.consume();
					System.out.println("Consumed: " + msg.content);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		
		executor2.shutdown();
		executor2.awaitTermination(10, TimeUnit.SECONDS);
		System.out.println();
		
		// Test Case 3: Connection Pool with ThreadLocal
		System.out.println("3. THREAD-LOCAL CONNECTION POOL TEST");
		ExecutorService executor3 = Executors.newFixedThreadPool(3);
		for (int i = 0; i < 3; i++) {
			executor3.submit(ConnectionPool::executeQuery);
		}
		executor3.shutdown();
		executor3.awaitTermination(5, TimeUnit.SECONDS);
		
		System.out.println("\n=== All test cases completed ===");
	}
}
