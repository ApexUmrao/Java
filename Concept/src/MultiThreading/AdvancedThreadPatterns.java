package MultiThreading;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

/**
 * ADVANCED PRODUCTION-LEVEL THREAD PATTERNS
 * Real-world scenarios and complex threading implementations
 */
public class AdvancedThreadPatterns {

	// ============================================================================
	// PATTERN 1: SAFE LAZY INITIALIZATION WITH CLASS LOADER
	// ============================================================================
	static class SafeLazySingleton {
		private SafeLazySingleton() {
			System.out.println("Instance created");
		}
		
		private static class SingletonHolder {
			static final SafeLazySingleton INSTANCE = new SafeLazySingleton();
		}
		
		public static SafeLazySingleton getInstance() {
			return SingletonHolder.INSTANCE;
		}
	}
	
	// ============================================================================
	// PATTERN 2: READ-WRITE LOCK
	// ============================================================================
	static class CacheWithReadWriteLock<K, V> {
		private final Map<K, V> cache = new HashMap<>();
		private final ReadWriteLock lock = new ReentrantReadWriteLock();
		
		public V get(K key) {
			lock.readLock().lock();
			try {
				return cache.get(key);
			} finally {
				lock.readLock().unlock();
			}
		}
		
		public void put(K key, V value) {
			lock.writeLock().lock();
			try {
				cache.put(key, value);
			} finally {
				lock.writeLock().unlock();
			}
		}
	}
	
	// ============================================================================
	// PATTERN 3: CIRCUIT BREAKER EXECUTOR
	// ============================================================================
	static class CircuitBreakerExecutor {
		private final ExecutorService executor;
		private final AtomicInteger failureCount = new AtomicInteger(0);
		private final int failureThreshold = 5;
		private final AtomicBoolean circuitOpen = new AtomicBoolean(false);
		
		public CircuitBreakerExecutor(int poolSize) {
			this.executor = Executors.newFixedThreadPool(poolSize);
		}
		
		public void submit(Runnable task) throws RejectedExecutionException {
			if (circuitOpen.get()) {
				throw new RejectedExecutionException("Circuit breaker open");
			}
			
			executor.submit(() -> {
				try {
					task.run();
					failureCount.set(0);
				} catch (Exception e) {
					failureCount.incrementAndGet();
					if (failureCount.get() >= failureThreshold) {
						circuitOpen.set(true);
					}
				}
			});
		}
	}
	
	// ============================================================================
	// PATTERN 4: EVENT BROKER WITH COPY-ON-WRITE
	// ============================================================================
	static class EventBroker {
		interface EventListener {
			void onEvent(String event);
		}
		
		private final List<EventListener> listeners = new CopyOnWriteArrayList<>();
		
		public void addEventListener(EventListener listener) {
			listeners.add(listener);
		}
		
		public void publishEvent(String event) {
			for (EventListener listener : listeners) {
				listener.onEvent(event);
			}
		}
	}
	
	// ============================================================================
	// PATTERN 5: SEMAPHORE RESOURCE POOL
	// ============================================================================
	static class SemaphoreResourcePool {
		private final Semaphore semaphore;
		private final List<String> resources = new ArrayList<>();
		
		public SemaphoreResourcePool(int capacity) {
			this.semaphore = new Semaphore(capacity, true);
			for (int i = 0; i < capacity; i++) {
				resources.add("Resource-" + i);
			}
		}
		
		public String acquireResource() throws InterruptedException {
			semaphore.acquire();
			synchronized (resources) {
				return resources.remove(0);
			}
		}
		
		public void releaseResource(String resource) {
			synchronized (resources) {
				resources.add(resource);
			}
			semaphore.release();
		}
	}
	
	// ============================================================================
	// PATTERN 6: MONITORED THREAD POOL
	// ============================================================================
	static class MonitoredThreadPool extends ThreadPoolExecutor {
		private final AtomicInteger tasksSubmitted = new AtomicInteger(0);
		private final AtomicInteger tasksCompleted = new AtomicInteger(0);
		private final AtomicLong totalExecutionTime = new AtomicLong(0);
		
		private static final ThreadLocal<Long> startTime = new ThreadLocal<>();
		
		public MonitoredThreadPool(int coreSize, int maxSize, 
				long keepAlive, TimeUnit unit, BlockingQueue<Runnable> queue) {
			super(coreSize, maxSize, keepAlive, unit, queue);
		}
		
		@Override
		public void execute(Runnable command) {
			tasksSubmitted.incrementAndGet();
			super.execute(command);
		}
		
		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			startTime.set(System.nanoTime());
		}
		
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			
			try {
				long duration = System.nanoTime() - startTime.get();
				totalExecutionTime.addAndGet(duration);
				tasksCompleted.incrementAndGet();
			} finally {
				startTime.remove();
			}
		}
		
		public void printMetrics() {
			System.out.println("=== Thread Pool Metrics ===");
			System.out.println("Tasks Submitted: " + tasksSubmitted.get());
			System.out.println("Tasks Completed: " + tasksCompleted.get());
			System.out.println("Active Threads: " + getActiveCount());
			System.out.println("Queue Size: " + getQueue().size());
		}
	}
}
