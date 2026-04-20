# Production-Level Thread Cases - Complete Guide

## Overview
This document provides a comprehensive guide to critical thread cases in production Java applications. Each case includes the problem, symptoms, solutions, and best practices.

---

## CASE 1: RACE CONDITION - UNSAFE COUNTER

### Problem
```
Multiple threads access and modify shared data without synchronization.
The read-modify-write operation is NOT atomic.
```

### Example (UNSAFE)
```java
private int count = 0;
public void incrementUnsafe() {
    count++;  // THREE operations: read, increment, write
}
```

### Why It's Unsafe
- Thread A reads count=5
- Thread B reads count=5
- Thread A writes count=6
- Thread B writes count=6
- Expected: 7, Actual: 6 (lost update!)

### Solutions

#### Solution 1: Synchronized Method
```java
public synchronized void increment() {
    count++;
}
```
**Pros:** Simple, lock-free for readers  
**Cons:** Every call acquires lock (slower)

#### Solution 2: AtomicInteger (Recommended)
```java
private AtomicInteger count = new AtomicInteger(0);
public void increment() {
    count.incrementAndGet();  // Atomic, lock-free
}
```
**Pros:** High performance, lock-free  
**Cons:** Limited to simple operations

#### Solution 3: Synchronized Block
```java
private final Object lock = new Object();
public void increment() {
    synchronized(lock) {
        count++;
    }
}
```
**Pros:** Fine-grained control  
**Cons:** More verbose

### Production Guidelines
- Use AtomicInteger for counters
- Use AtomicLong for larger numbers
- Use synchronized for complex operations
- Prefer ConcurrentHashMap over HashMap

---

## CASE 2: DEADLOCK - CIRCULAR LOCK DEPENDENCY

### Problem
```
Two or more threads wait indefinitely for locks held by each other.
Results in complete system hang.
```

### Example (UNSAFE)
```java
// Thread A
synchronized(account1) {
    synchronized(account2) {  // Waiting for account2
        transfer();
    }
}

// Thread B (SAME TIME)
synchronized(account2) {       // Holds account2
    synchronized(account1) {   // DEADLOCK: Waiting for account1
        transfer();
    }
}
```

### Deadlock Conditions (All Must Be True)
1. **Mutual Exclusion:** Resource cannot be shared
2. **Hold and Wait:** Thread holds resources while waiting for others
3. **No Preemption:** Resources cannot be forcibly taken
4. **Circular Wait:** Cycle of threads waiting for resources

### Solution 1: Lock Ordering
```java
public void safeTransfer(Account recipient, long amount) {
    // Always acquire locks in consistent order
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
```

### Solution 2: Timeout with ReentrantLock
```java
private final ReentrantLock lock = new ReentrantLock();

public boolean safeTransferWithTimeout(Account recipient, 
        long amount, long timeoutMs) throws InterruptedException {
    
    if (!this.lock.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
        return false;  // Timeout - no deadlock
    }
    
    try {
        if (!recipient.lock.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
            return false;  // Timeout - no deadlock
        }
        try {
            if (this.balance >= amount) {
                this.balance -= amount;
                recipient.balance += amount;
                return true;
            }
        } finally {
            recipient.lock.unlock();
        }
    } finally {
        this.lock.unlock();
    }
}
```

### Deadlock Prevention Strategies
| Strategy | How It Works | Use Case |
|----------|-------------|----------|
| Lock Ordering | Always acquire locks in same order | Simple transfers, hierarchical locks |
| Timeout | Abandon operation if lock not acquired | I/O operations, microservices |
| Try-lock Pattern | Non-blocking lock acquisition | High-frequency operations |
| Resource Ordering | Assign global order to resources | Complex multi-resource transactions |
| Nested Locks | Limit depth of lock nesting | Avoid by design |

### Production Guidelines
- **NEVER** acquire multiple locks without ordering
- Use tryLock() with timeouts for robustness
- Minimize time holding locks
- Document lock acquisition order
- Monitor for deadlocks in production

---

## CASE 3: THREAD STARVATION - FAIR LOCK ACQUISITION

### Problem
```
Some threads never get access to resources while others monopolize them.
Low-priority threads starve indefinitely.
```

### Example (UNSAFE)
```java
private final Semaphore unfairSemaphore = new Semaphore(1, false);

public void unfairAccess() throws InterruptedException {
    unfairSemaphore.acquire();  // UNFAIR - aggressive threads get more access
    try {
        // Critical section
        Thread.sleep(10);
    } finally {
        unfairSemaphore.release();
    }
}
```

### Symptoms of Starvation
- Some threads blocked forever
- Thread dumps show threads in WAITING state
- Uneven thread activity in monitoring
- One type of request never completes

### Solution: Fair Lock
```java
private final Semaphore fairSemaphore = new Semaphore(1, true);

public void fairAccess() throws InterruptedException {
    fairSemaphore.acquire();  // FAIR - FIFO order
    try {
        // Critical section
        Thread.sleep(10);
    } finally {
        fairSemaphore.release();
    }
}
```

### Fair vs Unfair Locks
```
Unfair Lock (Performance):
T1 -> [LOCK] -> Acquired
T2 -> [LOCK] -> T2 can jump queue (faster but unfair)
T3 -> [LOCK] ->

Fair Lock (Justice):
T1 -> [QUEUE] -> Acquired
T2 -> [QUEUE] -> Must wait for T1
T3 -> [QUEUE] -> Must wait for T1, T2
```

### Using Fair ReentrantLock
```java
private final ReentrantLock fairLock = new ReentrantLock(true);  // true = fair

public void fairOperation() {
    fairLock.lock();
    try {
        // Critical section - all threads get equal chance
    } finally {
        fairLock.unlock();
    }
}
```

### Trade-offs
| Aspect | Unfair | Fair |
|--------|--------|------|
| Performance | Higher throughput | Lower throughput |
| Fairness | Unfair (starvation possible) | Fair (FIFO) |
| CPU Overhead | Lower | Higher |
| Latency Variance | High | Low |

### Production Guidelines
- Use fair locks for long-running operations
- Use unfair locks for short, frequent operations
- Monitor for starvation with thread dumps
- Set thread priorities carefully (use default)
- Use thread pools to prevent starvation

---

## CASE 4: MEMORY VISIBILITY ISSUE - VOLATILE vs SYNCHRONIZED

### Problem
```
CPU caches: Writes by one thread not immediately visible to others.
Compiler reorders operations: Instructions executed out-of-order.
JVM optimizations: Removes "unnecessary" code.
Result: Unpredictable behavior with shared fields.
```

### Example (UNSAFE)
```java
private boolean running = true;  // NO GUARANTEE OF VISIBILITY

// Thread 1
public void start() {
    running = true;
}

// Thread 2
public void stop() {
    while (running) {  // May see stale value forever!
        // keep looping
    }
}
```

### Memory Visibility Issues
```
CPU Cache Layout:
Core 1 Cache: running=true   <- Old value stays here
Core 2 Cache: running=false  <- New value goes here
              Main Memory: running=? (which one?)

Result: Different threads see different values!
```

### Solution 1: Volatile (Visibility Only)
```java
private volatile boolean running = true;  // Write-through to memory

// Guarantees:
// 1. Write to volatile immediately visible to all threads
// 2. Read of volatile sees latest write
// 3. NOT atomic for compound operations
```

### Solution 2: Synchronized (Visibility + Atomicity)
```java
private boolean syncFlag = false;

public synchronized void setFlag() {
    syncFlag = true;  // Atomic + visible to all threads
}

public synchronized boolean getFlag() {
    return syncFlag;
}
```

### Solution 3: AtomicReference
```java
private AtomicReference<String> reference = new AtomicReference<>();

reference.set("value");      // Atomic + visible
String val = reference.get(); // Visible read
reference.compareAndSet("value", "newValue");  // Atomic CAS
```

### Volatile Semantics
```
BEFORE volatile write: All operations before are complete
VOLATILE WRITE
AFTER volatile write: All threads see new value

Memory Barrier Example:
    x = 1
    y = 2
    volatile write z = 3  <- MEMORY BARRIER
    a = 4
    b = 5
    
All threads see x=1, y=2, z=3 before seeing a=4, b=5
```

### When to Use What

| Scenario | Solution |
|----------|----------|
| Single boolean flag | volatile |
| Simple numeric field | volatile |
| Multiple operations | synchronized |
| Counter | AtomicInteger |
| Reference with CAS | AtomicReference |
| Complex state | ReentrantLock |

### Production Guidelines
- Use volatile for flags
- Use volatile for simple fields
- Use synchronized for methods modifying multiple fields
- Use Atomic* for simple operations
- Never rely on timing for visibility

---

## CASE 5: THREAD POOL MISCONFIGURATION

### Problem
```
Wrong executor configuration causes:
- Resource exhaustion (memory/threads)
- Thread starvation
- Deadlock between tasks
- Queue overflow
- Task rejection
```

### Thread Pool Types

#### Fixed Thread Pool (CPU-Bound Tasks)
```java
// For CPU-intensive work: pool size = CPU cores
ExecutorService cpuBoundPool = Executors.newFixedThreadPool(
    Runtime.getRuntime().availableProcessors()  // Usually 8-16
);

// Example: Image processing, calculations
cpuBoundPool.submit(() -> {
    // CPU-intensive work
    for (int i = 0; i < 1000000; i++) {
        // computation
    }
});
```

#### Cached Thread Pool (I/O-Bound Tasks)
```java
// Creates threads on demand, reuses idle threads
ExecutorService ioBoundPool = Executors.newCachedThreadPool();

// Example: HTTP requests, database queries
ioBoundPool.submit(() -> {
    // I/O operation - thread sleeps, doesn't consume CPU
    httpClient.get("https://example.com");
});
```

#### Scheduled Thread Pool
```java
ScheduledExecutorService scheduledPool = 
    Executors.newScheduledThreadPool(10);

// One-time execution after delay
scheduledPool.schedule(() -> {
    System.out.println("Delayed task");
}, 5, TimeUnit.SECONDS);

// Repeated execution
scheduledPool.scheduleAtFixedRate(() -> {
    System.out.println("Periodic task");
}, 0, 10, TimeUnit.SECONDS);

// Fixed delay between end and next start
scheduledPool.scheduleWithFixedDelay(() -> {
    System.out.println("Fixed delay task");
}, 0, 10, TimeUnit.SECONDS);
```

#### Custom Thread Pool (RECOMMENDED)
```java
ExecutorService customPool = new ThreadPoolExecutor(
    10,                                    // Core threads (always running)
    50,                                    // Max threads
    60, TimeUnit.SECONDS,                  // Keep-alive for excess threads
    new LinkedBlockingQueue<>(1000),       // Task queue (bounded!)
    new ThreadPoolExecutor.CallerRunsPolicy()  // Rejection policy
);

// Key points:
// - Core threads always running, max threads spin up on demand
// - Keep-alive time: how long excess threads stay idle
// - Queue: bounded to prevent OOM
// - Rejection policy: what to do when queue full
```

### Rejection Policies

```java
// 1. CallerRunsPolicy - Caller thread executes task
new ThreadPoolExecutor.CallerRunsPolicy()
// Use: I/O-bound tasks that can backpressure caller

// 2. AbortPolicy (DEFAULT) - Throw RejectedExecutionException
new ThreadPoolExecutor.AbortPolicy()
// Use: Strict SLA, don't want to lose tasks

// 3. DiscardPolicy - Silently drop task
new ThreadPoolExecutor.DiscardPolicy()
// Use: Monitoring metrics, losing one is OK

// 4. DiscardOldestPolicy - Drop oldest task, retry new
new ThreadPoolExecutor.DiscardOldestPolicy()
// Use: Latest data most important (e.g., sensor readings)
```

### Task Queue Sizing

```
Queue too small:
- Tasks rejected frequently
- Caller threads block (CallerRunsPolicy)

Queue too large:
- Memory exhaustion (OOM)
- High latency (waiting in queue)
- Backpressure not visible

Formula for I/O-bound:
Queue Size = (Available Memory / Task Memory) / Thread Count
Example: 1GB / 1MB / 100 = ~10,000 tasks

Formula for CPU-bound:
Queue Size = Expected Peak Tasks / Thread Count
Example: 1000 peak tasks / 8 threads = ~125 queue size
```

### Deadlock in Thread Pool

```java
// DANGEROUS: Core threads can deadlock on dependent tasks
ExecutorService pool = Executors.newFixedThreadPool(1);

Future<Integer> future = pool.submit(() -> {
    // Submits another task and waits for it
    Future<Integer> inner = pool.submit(() -> 42);
    return inner.get();  // DEADLOCK! Only 1 thread, both tasks waiting
});
```

**Solution:** Use more threads than max task nesting
```java
// Safe: 2 threads can handle nested tasks
ExecutorService pool = Executors.newFixedThreadPool(2);
```

### Thread Pool Configuration Guide

| Workload | Pool Type | Size | Queue | Policy |
|----------|-----------|------|-------|--------|
| CPU-Bound | Fixed | CPUs | Small (10-100) | Abort |
| I/O-Bound | Cached | Auto | Large | CallerRuns |
| Mixed | Custom | CPUs*2 | Bounded | Abort |
| Scheduled | Scheduled | Min(10, CPUs) | N/A | Abort |

### Production Guidelines
- **CPU-bound:** pool size = available processors
- **I/O-bound:** larger pool (threads wait for I/O)
- **Always bound queue size** (prevent memory leak)
- **Monitor:** active threads, queue depth, rejections
- **Never use Executors.newFixedThreadPool()** without knowing workload
- **Always shutdown gracefully:**
```java
pool.shutdown();
if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
    pool.shutdownNow();
}
```

---

## CASE 6: DOUBLE-CHECKED LOCKING (Lazy Initialization)

### Problem
```
Need lazy initialization that is thread-safe.
Don't want to pay lock cost after initialization.
```

### Example (UNSAFE)
```java
private static Singleton instance;

public static Singleton getInstance() {
    if (instance == null) {  // Check 1: Maybe null
        synchronized(Singleton.class) {
            if (instance == null) {  // Check 2: Safety
                instance = new Singleton();  // Visibility issue!
            }
        }
    }
    return instance;  // Problem: Might return partial object
}
```

### Why Original DCL Failed

```java
new Singleton() performs 3 steps:
1. Allocate memory for object
2. Call constructor (initialize fields)
3. Assign reference to instance

JVM can reorder (without volatile):
1. Allocate memory
3. Assign reference to instance  <- PROBLEM: instance != null
2. Call constructor (initialize fields) <- HAPPENS LATER

Thread A: Sees instance != null, returns partially constructed object!
Thread B: Calls constructor fields are uninitialized!
```

### Solution: Volatile
```java
private static volatile Singleton instance;  // VOLATILE!

public static Singleton getInstance() {
    if (instance == null) {  // First check (no lock)
        synchronized(Singleton.class) {
            if (instance == null) {  // Second check (with lock)
                instance = new Singleton();  // Volatile write, proper ordering
            }
        }
    }
    return instance;
}
```

### Volatile Prevents Reordering
```
Step 1: Allocate memory
Step 2: Call constructor (fields initialized)
Step 3: [MEMORY BARRIER from volatile write]
Step 4: Assign to instance variable

Now all fields definitely initialized before reference visible!
```

### Solution: Eager Initialization (SIMPLEST)
```java
// Initialized when class loads (thread-safe by JVM)
private static final Singleton INSTANCE = new Singleton();

public static Singleton getInstance() {
    return INSTANCE;
}
```

### Solution: Class Loader Pattern (BEST)
```java
public class Singleton {
    private Singleton() {}
    
    // Inner class loaded only on first call
    private static class SingletonHolder {
        static final Singleton INSTANCE = new Singleton();
    }
    
    public static Singleton getInstance() {
        return SingletonHolder.INSTANCE;  // Thread-safe, lazy, no volatile needed
    }
}
```

### Comparison

| Approach | Thread-Safe | Lazy | Simple | Recommended |
|----------|-------------|------|--------|-------------|
| Unsafe DCL | No | Yes | No | NO |
| Volatile DCL | Yes | Yes | No | OK |
| Eager | Yes | No | Yes | For most cases |
| Class Loader | Yes | Yes | Yes | **BEST** |

### Production Guidelines
- Use Class Loader Pattern (SafeHolder)
- Avoid Volatile DCL (confusing)
- Use Eager for simple cases
- Never skip synchronization
- Test with multiple threads

---

## CASE 7: PRODUCER-CONSUMER (MOST COMMON PATTERN)

### Problem
```
Multiple threads producing data
Multiple threads consuming data
Need thread-safe data exchange
Should block when queue full (producer) or empty (consumer)
```

### Example (UNSAFE)
```java
public class UnsafeQueue {
    private Queue<Message> queue = new LinkedList<>();
    
    public void produce(Message msg) {
        synchronized(queue) {
            queue.add(msg);  // Data race if consumer removes while adding
            queue.notifyAll();
        }
    }
    
    public Message consume() throws InterruptedException {
        synchronized(queue) {
            while (queue.isEmpty()) {
                queue.wait();  // Might lose notification
            }
            return queue.poll();
        }
    }
}
```

### Solution: BlockingQueue (RECOMMENDED)
```java
public class SafeQueue {
    private BlockingQueue<Message> queue = 
        new LinkedBlockingQueue<>(100);  // Bounded queue
    
    public void produce(Message msg) throws InterruptedException {
        queue.put(msg);  // Blocks if queue full (backpressure)
    }
    
    public Message consume() throws InterruptedException {
        return queue.take();  // Blocks if queue empty
    }
}
```

### BlockingQueue Types

```java
// 1. LinkedBlockingQueue - Unbounded or bounded
// Good for: General producer-consumer
BlockingQueue<Message> queue = new LinkedBlockingQueue<>(1000);

// 2. ArrayBlockingQueue - Fixed size, fastest
// Good for: When size is known, need max performance
BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1000);

// 3. PriorityBlockingQueue - Unbounded, ordered
// Good for: Priority-based processing
BlockingQueue<Message> queue = new PriorityBlockingQueue<>();

// 4. SynchronousQueue - Size 0 (producer waits for consumer)
// Good for: Handoff pattern, work stealing
BlockingQueue<Message> queue = new SynchronousQueue<>();
```

### Producer Implementation
```java
class Producer implements Runnable {
    private BlockingQueue<Message> queue;
    
    public Producer(BlockingQueue<Message> queue) {
        this.queue = queue;
    }
    
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = createMessage();
                queue.put(msg);  // Blocks if full
                System.out.println("Produced: " + msg);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Producer interrupted");
        }
    }
}
```

### Consumer Implementation
```java
class Consumer implements Runnable {
    private BlockingQueue<Message> queue;
    
    public Consumer(BlockingQueue<Message> queue) {
        this.queue = queue;
    }
    
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = queue.take();  // Blocks if empty
                processMessage(msg);
                System.out.println("Consumed: " + msg);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Consumer interrupted");
        }
    }
}
```

### Using Multiple Producers/Consumers
```java
BlockingQueue<Message> queue = new LinkedBlockingQueue<>(1000);
ExecutorService executor = Executors.newFixedThreadPool(6);

// 3 producers
for (int i = 0; i < 3; i++) {
    executor.submit(new Producer(queue));
}

// 3 consumers
for (int i = 0; i < 3; i++) {
    executor.submit(new Consumer(queue));
}

// Graceful shutdown
executor.shutdown();
executor.awaitTermination(1, TimeUnit.MINUTES);
```

### Handling Poison Pills (Shutdown Pattern)
```java
class Message {
    public static final Message POISON = new Message("POISON");
    String content;
    Message(String content) { this.content = content; }
}

// Producer: Add poison pill to signal end
queue.put(Message.POISON);

// Consumer: Stop when poison received
while (!Thread.currentThread().isInterrupted()) {
    Message msg = queue.take();
    if (msg == Message.POISON) {
        break;  // Stop consuming
    }
    processMessage(msg);
}
```

### Production Guidelines
- Always use bounded queue (prevent OOM)
- Use LinkedBlockingQueue for most cases
- Handle InterruptedException properly
- Implement graceful shutdown with poison pill
- Monitor queue size in production
- Set appropriate timeouts for operations

---

## CASE 8: CALLABLE vs RUNNABLE - FUTURE EXECUTION

### Problem
```
Runnable: No return value, exceptions lost
Callable: Need result from task, need exception handling
Solution: Use Callable + Future
```

### Runnable (UNSAFE for result/exception)
```java
ExecutorService executor = Executors.newFixedThreadPool(1);

executor.execute(() -> {
    try {
        String result = fetchData("123");
        System.out.println(result);  // Can only print, can't return
    } catch (Exception e) {
        e.printStackTrace();  // Silent failure
    }
});
```

### Callable + Future (SAFE)
```java
ExecutorService executor = Executors.newFixedThreadPool(1);

Future<String> future = executor.submit(new Callable<String>() {
    @Override
    public String call() throws Exception {
        return fetchData("123");
    }
});

try {
    String result = future.get();  // Blocks until result available
    System.out.println("Result: " + result);
} catch (ExecutionException e) {
    System.out.println("Task failed: " + e.getCause());
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

### Get with Timeout
```java
try {
    String result = future.get(5, TimeUnit.SECONDS);  // Wait max 5 seconds
    System.out.println("Result: " + result);
} catch (TimeoutException e) {
    future.cancel(true);  // Cancel task if taking too long
    System.out.println("Task timed out");
} catch (ExecutionException e) {
    System.out.println("Task failed: " + e.getCause());
}
```

### Multiple Futures - invokeAll()
```java
List<Callable<String>> tasks = new ArrayList<>();
tasks.add(() -> fetchData("1"));
tasks.add(() -> fetchData("2"));
tasks.add(() -> fetchData("3"));

try {
    List<Future<String>> futures = executor.invokeAll(tasks, 10, TimeUnit.SECONDS);
    
    for (Future<String> future : futures) {
        if (future.isDone()) {
            System.out.println("Result: " + future.get());
        } else {
            System.out.println("Task cancelled");
        }
    }
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

### First Result - invokeAny()
```java
List<Callable<String>> tasks = new ArrayList<>();
tasks.add(() -> fetchDataFromServer1());
tasks.add(() -> fetchDataFromServer2());
tasks.add(() -> fetchDataFromServer3());

try {
    String result = executor.invokeAny(tasks, 10, TimeUnit.SECONDS);
    System.out.println("First result: " + result);  // Returns first successful result
} catch (TimeoutException e) {
    System.out.println("All tasks timed out");
} catch (ExecutionException e) {
    System.out.println("All tasks failed");
}
```

### Lambda Expression (Modern Java)
```java
ExecutorService executor = Executors.newFixedThreadPool(1);

// Callable as lambda
Future<String> future = executor.submit(() -> {
    return fetchData("123");  // Returns value
});

try {
    String result = future.get(5, TimeUnit.SECONDS);
} catch (TimeoutException | ExecutionException | InterruptedException e) {
    e.printStackTrace();
}
```

### Production Guidelines
- Always use Callable for tasks with results
- Always use Future.get() with timeout
- Handle ExecutionException for task failures
- Use invokeAll() for batch processing
- Use invokeAny() for first-result patterns
- Cancel tasks that exceed timeout

---

## CASE 9: THREAD INTERRUPTION - GRACEFUL SHUTDOWN

### Problem
```
Need to stop threads gracefully, not abruptly.
Don't use Thread.stop() (deprecated, dangerous)
Don't use Thread.suspend()/resume() (deprecated)
Use interrupt() and check InterruptedException
```

### Example (UNSAFE)
```java
class UnstoppableWorker implements Runnable {
    @Override
    public void run() {
        while (true) {  // Infinite loop
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  // Ignoring interrupt - WRONG!
            }
        }
    }
}
```

### Proper Interrupt Handling
```java
class InterruptibleWorker implements Runnable {
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Do work
                System.out.println("Working...");
                Thread.sleep(1000);
                
                // Check interruption flag periodically
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Interrupted flag detected");
                    break;
                }
            }
        } catch (InterruptedException e) {
            // Restore interrupt status (important!)
            Thread.currentThread().interrupt();
            System.out.println("Caught InterruptedException");
        }
    }
}
```

### Interrupt Semantics
```
Thread.interrupt():
- Sets interrupt flag = true
- If thread sleeping: wakes it up, throws InterruptedException
- If thread running: flag set, task checks it

InterruptedException:
- Thrown when thread interrupted while blocked
- Clears interrupt flag automatically!
- MUST restore: Thread.currentThread().interrupt()
```

### Graceful Shutdown Pattern
```java
ExecutorService executor = Executors.newFixedThreadPool(10);

// Submit tasks
for (int i = 0; i < 100; i++) {
    executor.submit(new InterruptibleWorker());
}

// Request shutdown
executor.shutdown();

try {
    // Wait for tasks to complete
    if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
        System.out.println("Timeout, forcing shutdown");
        
        // Force interrupt all threads
        List<Runnable> remaining = executor.shutdownNow();
        System.out.println("Remaining tasks: " + remaining.size());
        
        // Wait again for interrupted threads to clean up
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            System.out.println("Pool did not terminate");
        }
    }
} catch (InterruptedException e) {
    executor.shutdownNow();
    Thread.currentThread().interrupt();
}
```

### Responding to Interruption
```java
// Option 1: Propagate up
public void doWork() throws InterruptedException {
    if (Thread.currentThread().isInterrupted()) {
        throw new InterruptedException();
    }
    // Work...
}

// Option 2: Restore flag and return
public void doWork() {
    if (Thread.currentThread().isInterrupted()) {
        Thread.currentThread().interrupt();  // Restore flag
        return;
    }
    // Work...
}

// Option 3: Cleanup and exit
public void doWork() {
    try {
        while (!Thread.currentThread().isInterrupted()) {
            // Work...
        }
    } finally {
        cleanup();  // Always cleanup
    }
}
```

### Production Guidelines
- Check isInterrupted() in loops
- Restore interrupt status after catching InterruptedException
- Use shutdown() + awaitTermination() for graceful shutdown
- Use shutdownNow() as last resort
- Implement finally blocks for cleanup
- Never ignore InterruptedException

---

## CASE 10: THREAD-LOCAL STORAGE - CONNECTION POOLING

### Problem
```
Each thread needs its own instance (DB connection, Session, SimpleDateFormat)
Cannot share with other threads
ThreadLocal: One value per thread, isolated
WARNING: Memory leak if not cleaned up!
```

### Connection Pool Example
```java
class ConnectionPool {
    static class Connection {
        String id;
        Connection(String id) { this.id = id; }
    }
    
    // One Connection per thread
    private static final ThreadLocal<Connection> connectionHolder = 
        new ThreadLocal<>();
    
    public static Connection getConnection() {
        Connection conn = connectionHolder.get();
        if (conn == null) {
            conn = new Connection("conn-" + Thread.currentThread().getId());
            connectionHolder.set(conn);
            System.out.println("Created: " + conn.id);
        }
        return conn;
    }
    
    // CRITICAL: Must remove to prevent memory leak
    public static void closeConnection() {
        connectionHolder.remove();
        System.out.println("Closed connection");
    }
    
    public static void executeQuery() {
        try {
            Connection conn = getConnection();
            System.out.println("Using: " + conn.id);
            // Use connection
        } finally {
            closeConnection();  // MUST DO THIS
        }
    }
}
```

### Memory Leak Scenario (WRONG)
```java
// WRONG: Never cleans up
for (int i = 0; i < 1000; i++) {
    executor.submit(() -> {
        Connection conn = ConnectionPool.getConnection();
        // Use connection
        // NO closeConnection() - CONNECTION LEAKED!
    });
}

// Thread pool keeps threads alive
// Each thread's ThreadLocal references Connection
// Connections accumulate in memory (OOM)
```

### Session Example
```java
class SessionManager {
    private static final ThreadLocal<Session> sessionHolder = 
        new ThreadLocal<>();
    
    // For web requests (HttpServlet)
    public static void setSession(Session session) {
        sessionHolder.set(session);
    }
    
    public static Session getSession() {
        return sessionHolder.get();
    }
    
    // In servlet filter
    public void doFilter(ServletRequest req, ServletResponse res) {
        Session session = createSession();
        setSession(session);
        try {
            // request handling - any code can access getSession()
        } finally {
            sessionHolder.remove();  // CRITICAL for servlet pools
        }
    }
}
```

### SimpleDateFormat (Not Thread-Safe)
```java
// WRONG: SimpleDateFormat not thread-safe
private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

public String formatDate(Date date) {
    return formatter.format(date);  // DATA RACE if called from multiple threads
}

// RIGHT: ThreadLocal each thread gets own formatter
private static final ThreadLocal<SimpleDateFormat> formatterHolder = 
    new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

public String formatDate(Date date) {
    return formatterHolder.get().format(date);  // Thread-safe
}
```

### ThreadLocal + ExecutorService (Important!)
```java
class Worker implements Runnable {
    @Override
    public void run() {
        // Get or create for this thread
        Connection conn = ConnectionPool.getConnection();
        
        try {
            // Work with connection
        } finally {
            // MUST cleanup before thread returns to pool
            ConnectionPool.closeConnection();
        }
    }
}

// Main
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 100; i++) {
    executor.submit(new Worker());
}
```

### Production Guidelines
- Use ThreadLocal for connection pooling
- Use ThreadLocal for request context
- **ALWAYS remove in finally block**
- Use initialValue() for creation
- Document ThreadLocal usage
- Monitor for memory leaks
- Clean up before thread pool reuse

---

## CASE 11: CONCURRENT COLLECTIONS - THREAD-SAFE COLLECTIONS

### Problem
```
HashMap not thread-safe: Concurrent modification causes infinite loops
Collections.synchronizedMap: Entire map locked (low performance)
ConcurrentHashMap: Fine-grained locking (high performance)
```

### Synchronized Collections (SLOW)
```java
// Synchronizes entire collection on every operation
Map<String, String> synchronizedMap = 
    Collections.synchronizedMap(new HashMap<>());

// Each operation locks the entire map
synchronizedMap.put("key", "value");  // Locks whole map
synchronizedMap.get("key");           // Locks whole map

// Multiple threads wait for same lock - bottleneck!
```

### ConcurrentHashMap (FAST)
```java
// Segment-based locking: Multiple threads write simultaneously
Map<String, String> concurrentMap = new ConcurrentHashMap<>();

concurrentMap.put("key1", "value1");  // Lock segment 1
concurrentMap.put("key2", "value2");  // Lock segment 2 (NO WAIT)

// Multiple threads write to different segments simultaneously
// Much better throughput!
```

### Segment Structure
```
ConcurrentHashMap with 16 segments:

Segment 1: [key0][key16][key32]... <- Thread A can lock
Segment 2: [key1][key17][key33]... <- Thread B can lock
Segment 3: [key2][key18][key34]... <- Thread C can lock
...

16 threads can write simultaneously (one per segment)
100 threads: most wait, but far better than synchronized map
```

### List Comparison
```java
// SLOW: Synchronizes entire list
List<String> synchronizedList = 
    Collections.synchronizedList(new ArrayList<>());

// FAST: Copy-on-write (good for read-heavy)
List<String> copyOnWriteList = 
    new CopyOnWriteArrayList<>();

// Copy-on-write strategy:
// Read: No lock, just read (fast!)
// Write: Copy array, modify copy, replace reference
// Good for: More reads than writes (e.g., event listeners)
// Bad for: Frequent writes (copies overhead)
```

### Queue Collections
```java
// ConcurrentLinkedQueue: Lock-free, high throughput
Queue<String> queue = new ConcurrentLinkedQueue<>();

// BlockingQueue: Blocks on empty/full
BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

// Deque for work stealing
Deque<String> deque = new ConcurrentLinkedDeque<>();
```

### Example: Multi-threaded Cache
```java
class ConcurrentCache<K, V> {
    private final Map<K, V> cache = new ConcurrentHashMap<>();
    
    public V get(K key) {
        return cache.get(key);
    }
    
    public void put(K key, V value) {
        cache.put(key, value);  // Safe, no external synchronization needed
    }
    
    public V getOrCompute(K key, Function<K, V> loader) {
        // Common pattern: Check, compute, put
        V value = cache.get(key);
        if (value == null) {
            // Multiple threads might compute (OK for performance)
            value = loader.apply(key);
            V existing = cache.putIfAbsent(key, value);
            if (existing != null) {
                value = existing;  // Use first one computed
            }
        }
        return value;
    }
}
```

### Collection Selection Guide

| Collection | Use Case | Concurrency | Performance |
|------------|----------|-------------|-------------|
| Collections.synchronizedMap | Legacy code | Low | Poor |
| ConcurrentHashMap | General caching | High | Excellent |
| Collections.synchronizedList | Legacy code | Low | Poor |
| CopyOnWriteArrayList | Read-heavy | Medium | Good |
| BlockingQueue | Producer-consumer | High | Good |
| ConcurrentLinkedQueue | Work queue | High | Excellent |

### Production Guidelines
- Always use ConcurrentHashMap instead of synchronized
- Use CopyOnWriteArrayList for listeners, event handlers
- Use BlockingQueue for producer-consumer
- Avoid synchronized collections (legacy)
- Monitor collection size and performance
- Consider custom Collection for special needs

---

## CASE 12: LOCKS & CONDITIONS - ADVANCED SYNCHRONIZATION

### Problem
```
synchronized keyword limitations:
- Only one condition (wait/notify)
- Cannot timeout
- Cannot be fair
Solution: ReentrantLock with Conditions
```

### Producer-Consumer with Conditions
```java
class BoundedBuffer {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;
    
    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }
    
    // Producer: put item, wait if full
    public void put(int value) throws InterruptedException {
        lock.lock();
        try {
            // Wait while queue is full
            while (queue.size() >= capacity) {
                System.out.println("Queue full, producer waiting");
                notFull.await();  // Release lock and wait
                // Notified, reacquire lock, check condition again
            }
            
            // Now safe to add
            queue.add(value);
            System.out.println("Produced: " + value);
            
            // Wake up waiting consumers
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    // Consumer: take item, wait if empty
    public int take() throws InterruptedException {
        lock.lock();
        try {
            // Wait while queue is empty
            while (queue.isEmpty()) {
                System.out.println("Queue empty, consumer waiting");
                notEmpty.await();  // Release lock and wait
            }
            
            // Now safe to remove
            int value = queue.poll();
            System.out.println("Consumed: " + value);
            
            // Wake up waiting producers
            notFull.signalAll();
            return value;
        } finally {
            lock.unlock();
        }
    }
}
```

### Lock Features
```java
// 1. Reentrant: Same thread can acquire multiple times
ReentrantLock lock = new ReentrantLock();
lock.lock();
lock.lock();  // OK, same thread
lock.unlock();
lock.unlock();

// 2. Fair lock: FIFO order (slower)
ReentrantLock fairLock = new ReentrantLock(true);

// 3. Try lock: Non-blocking
if (lock.tryLock()) {
    try {
        // Critical section
    } finally {
        lock.unlock();
    }
} else {
    System.out.println("Could not acquire lock");
}

// 4. Try lock with timeout
try {
    if (lock.tryLock(5, TimeUnit.SECONDS)) {
        try {
            // Critical section
        } finally {
            lock.unlock();
        }
    }
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}

// 5. Interruptible lock
try {
    lock.lockInterruptibly();  // Respects interrupts
    try {
        // Critical section
    } finally {
        lock.unlock();
    }
} catch (InterruptedException e) {
    System.out.println("Lock acquisition interrupted");
}
```

### Condition Variables
```java
// Multiple conditions for different scenarios
private final ReentrantLock lock = new ReentrantLock();
private final Condition conditionA = lock.newCondition();
private final Condition conditionB = lock.newCondition();
private final Condition conditionC = lock.newCondition();

// Signal specific threads
conditionA.signalAll();  // Wake only threads waiting on A
conditionB.signalAll();  // Wake only threads waiting on B
// Other threads continue waiting

// Await with timeout
lock.lock();
try {
    if (!conditionA.await(5, TimeUnit.SECONDS)) {
        System.out.println("Timeout waiting for condition");
    }
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    lock.unlock();
}
```

### Lock vs Synchronized

| Feature | Synchronized | ReentrantLock |
|---------|--------------|---------------|
| Reentrant | Yes | Yes |
| Fair lock | No | Yes (configurable) |
| Try lock | No | Yes |
| Timeout | No | Yes |
| Conditions | 1 (implicit) | Multiple |
| Interruptible | No | Yes (lockInterruptibly) |
| Performance | Fast | Slightly slower |

### Production Guidelines
- Use ReentrantLock for complex scenarios
- Use multiple Conditions for different wait scenarios
- Always use try-finally for unlock
- Use fairLock only if fairness required (performance cost)
- Use tryLock for deadlock avoidance
- Document condition semantics

---

## CASE 13: LATCH & BARRIER - SYNCHRONIZATION UTILITIES

### CountDownLatch: One-Time Gate

```java
// One-time synchronization: N -> 0

public void countDownLatchExample() throws InterruptedException {
    int numThreads = 5;
    
    // Latch1: Wait for all threads ready
    CountDownLatch startLatch = new CountDownLatch(1);
    
    // Latch2: Wait for all threads done
    CountDownLatch endLatch = new CountDownLatch(numThreads);
    
    for (int i = 0; i < numThreads; i++) {
        new Thread(new Worker(startLatch, endLatch, i)).start();
    }
    
    System.out.println("All threads created, waiting 2 seconds before start");
    Thread.sleep(2000);
    
    System.out.println("Starting all threads");
    startLatch.countDown();  // Signal: go!
    
    System.out.println("Waiting for all to finish");
    endLatch.await();  // Wait for all countDown calls
    
    System.out.println("All threads finished");
}

static class Worker implements Runnable {
    CountDownLatch startLatch, endLatch;
    int id;
    
    Worker(CountDownLatch start, CountDownLatch end, int id) {
        this.startLatch = start;
        this.endLatch = end;
        this.id = id;
    }
    
    @Override
    public void run() {
        try {
            System.out.println("Thread " + id + " ready");
            startLatch.await();  // Wait for signal
            
            System.out.println("Thread " + id + " working");
            Thread.sleep(1000 + (id * 100));
            
            System.out.println("Thread " + id + " done");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            endLatch.countDown();  // Signal completion
        }
    }
}
```

### CyclicBarrier: Reusable Gate

```java
// Reusable barrier: All N threads wait, then proceed together

public void cyclicBarrierExample() throws BrokenBarrierException, InterruptedException {
    int numThreads = 5;
    
    // Barrier resets after every phase
    CyclicBarrier barrier = new CyclicBarrier(numThreads, () -> {
        System.out.println("=== All threads at barrier, proceeding to next phase ===");
    });
    
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    
    for (int i = 0; i < numThreads; i++) {
        final int threadId = i;
        executor.submit(() -> {
            try {
                for (int phase = 0; phase < 3; phase++) {
                    // Do work
                    System.out.println("Thread " + threadId + " phase " + phase + " working");
                    Thread.sleep(500 + (threadId * 100));
                    
                    // All threads wait here
                    System.out.println("Thread " + threadId + " at barrier");
                    barrier.await();  // Waits for all N threads
                    
                    System.out.println("Thread " + threadId + " proceeding");
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
    }
}
```

### Comparison

| Aspect | CountDownLatch | CyclicBarrier |
|--------|----------------|---------------|
| Reset | No (one-time) | Yes (resets) |
| Parties | Variable (count) | Fixed (N) |
| Use | Completion wait | Phase synchronization |
| Barrier action | No | Yes (callback) |
| Thread pool | Any | Must be exactly N |

### Practical Examples

**Example 1: Wait for all threads to initialize**
```java
CountDownLatch initialized = new CountDownLatch(10);

// Each thread initializes and signals
for (int i = 0; i < 10; i++) {
    executor.submit(() -> {
        initializeResources();
        initialized.countDown();
    });
}

initialized.await();  // Wait for all initialized
startProcessing();
```

**Example 2: Stage pipeline with phases**
```java
// Each stage waits for all threads to complete phase

// Phase 1: Parse
CyclicBarrier phase1 = new CyclicBarrier(4);
// Phase 2: Process
CyclicBarrier phase2 = new CyclicBarrier(4);
// Phase 3: Write
CyclicBarrier phase3 = new CyclicBarrier(4);

executor.submit(() -> {
    parseData();
    phase1.await();
    
    processData();
    phase2.await();
    
    writeData();
    phase3.await();
});
```

### Production Guidelines
- Use CountDownLatch for one-time synchronization
- Use CyclicBarrier for repeated phases
- Implement barrier action for logging/monitoring
- Handle BrokenBarrierException (thread failure)
- Monitor barrier wait times in production

---

## CASE 14: ATOMIC OPERATIONS - LOCK-FREE PROGRAMMING

### Problem
```
Locks cause:
- Context switching overhead
- Priority inversion
- Deadlock risk
Atomic operations: Lock-free, high performance
```

### Atomic Types
```java
// Atomic primitives
AtomicInteger atomicInt = new AtomicInteger(0);
AtomicLong atomicLong = new AtomicLong(0);
AtomicBoolean atomicBool = new AtomicBoolean(false);

// Atomic reference
AtomicReference<String> atomicRef = new AtomicReference<>();

// Atomic arrays
AtomicIntegerArray intArray = new AtomicIntegerArray(10);
```

### Atomic Operations
```java
AtomicInteger counter = new AtomicInteger(0);

// Get and Set
counter.set(5);
int value = counter.get();

// Increment/Decrement
counter.incrementAndGet();  // ++counter
counter.getAndIncrement();  // counter++
counter.decrementAndGet();  // --counter
counter.getAndDecrement();  // counter--

// Add
counter.addAndGet(5);    // counter += 5
counter.getAndAdd(5);    // returns old value

// Compare and Swap (CAS)
boolean success = counter.compareAndSet(5, 10);
// If counter == 5, set to 10 and return true
// Otherwise, don't change and return false

// Get and Set
int oldValue = counter.getAndSet(20);  // Set and return old

// Lazy set (performance optimization)
counter.lazySet(100);  // Eventually visible
```

### CAS Loop Pattern
```java
AtomicInteger value = new AtomicInteger(0);

// Non-atomic operation wrapped in CAS loop
public void incrementByPercent(int percent) {
    int current;
    int next;
    do {
        current = value.get();
        next = current + (current * percent / 100);
    } while (!value.compareAndSet(current, next));
}

// If another thread modifies value during loop,
// compareAndSet fails, loop retries with new current value
```

### AtomicReference for Immutable Objects
```java
class Node {
    final int data;
    final Node next;
    
    Node(int data, Node next) {
        this.data = data;
        this.next = next;
    }
}

AtomicReference<Node> head = new AtomicReference<>();

// Lock-free stack push
public void push(int data) {
    Node newHead = new Node(data, null);
    Node oldHead;
    do {
        oldHead = head.get();
        newHead.next = oldHead;
    } while (!head.compareAndSet(oldHead, newHead));
}

// Lock-free stack pop
public Integer pop() {
    Node oldHead;
    Node newHead;
    do {
        oldHead = head.get();
        if (oldHead == null) return null;
        newHead = oldHead.next;
    } while (!head.compareAndSet(oldHead, newHead));
    return oldHead.data;
}
```

### AtomicInteger as Synchronized Alternative
```java
// UNSAFE: Non-atomic increment
private int counter = 0;
public void increment() {
    counter++;  // Race condition
}

// SAFE: AtomicInteger
private AtomicInteger counter = new AtomicInteger(0);
public void increment() {
    counter.incrementAndGet();  // Lock-free
}

// Same result, better performance!
```

### Performance Comparison
```java
// Synchronized (locks)
private int counter = 0;
public synchronized void increment() {
    counter++;
}

// AtomicInteger (lock-free)
private AtomicInteger counter = new AtomicInteger(0);
public void increment() {
    counter.incrementAndGet();
}

// Throughput under high contention:
// Synchronized: ~5-10 million ops/sec
// AtomicInteger: ~50-100 million ops/sec
// 5-10x faster!
```

### When to Use Atomic vs Synchronized

| Scenario | Use |
|----------|-----|
| Simple counter | AtomicInteger |
| Single boolean flag | AtomicBoolean |
| Reference swap | AtomicReference |
| Complex operations | synchronized |
| Multiple fields | ReentrantLock |
| High contention | Atomic |
| Low contention | synchronized |

### Production Guidelines
- Use Atomic* for simple operations
- Use CAS loops for non-blocking updates
- Avoid busy-waiting (high CPU)
- Monitor CAS failure rate (indicates contention)
- Use for lock-free data structures
- AtomicInteger preferred over synchronized for counters

---

## CASE 15: EXCEPTION HANDLING IN THREADS

### Problem
```
Exceptions in threads are silent by default.
No automatic propagation to caller.
Can crash thread without warning.
```

### Example (UNSAFE)
```java
Thread thread = new Thread(() -> {
    throw new RuntimeException("Oops!");  // Silent - nobody knows!
});
thread.start();

// Main thread continues, completely unaware of failure
System.out.println("Main: everything OK");  // Printed even though thread crashed
```

### Solution 1: UncaughtExceptionHandler
```java
Thread thread = new Thread(() -> {
    throw new RuntimeException("Oops!");
});

thread.setUncaughtExceptionHandler((t, e) -> {
    System.out.println("Thread " + t.getName() + " failed!");
    System.out.println("Exception: " + e.getClass().getName());
    System.out.println("Message: " + e.getMessage());
    e.printStackTrace();
    
    // Log to monitoring system
    logger.error("Thread " + t.getName() + " crashed", e);
});

thread.start();
```

### Global Exception Handler
```java
// Set default handler for all threads
Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
    System.out.println("GLOBAL: Thread " + t.getName() + " threw " + e);
    
    // Send alert to monitoring
    alertMonitoring(t, e);
    
    // Graceful recovery
    restartThread(t);
});

// Now all threads get this handler
new Thread(() -> {
    throw new RuntimeException("Caught globally");
}).start();
```

### Solution 2: Future-Based Execution
```java
ExecutorService executor = Executors.newFixedThreadPool(1);

Future<?> future = executor.submit(() -> {
    throw new RuntimeException("Task failed");
});

try {
    future.get();  // Blocks until complete
} catch (ExecutionException e) {
    System.out.println("Task threw exception: " + e.getCause());
    // Properly handle the exception
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

### Solution 3: Wrapper Runnable
```java
class SafeRunnable implements Runnable {
    private Runnable task;
    private ExceptionHandler handler;
    
    public SafeRunnable(Runnable task, ExceptionHandler handler) {
        this.task = task;
        this.handler = handler;
    }
    
    @Override
    public void run() {
        try {
            task.run();
        } catch (Exception e) {
            handler.handle(Thread.currentThread(), e);
        }
    }
}

// Usage
executor.submit(new SafeRunnable(() -> {
    // Task code
}, (thread, exception) -> {
    // Handle exception
}));
```

### Thread Pool Monitoring
```java
class MonitoredThreadPool extends ThreadPoolExecutor {
    
    public MonitoredThreadPool(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }
    
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        
        if (t != null) {
            System.out.println("Task exception: " + t);
        }
        
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                future.get();  // Check for ExecutionException
            } catch (ExecutionException e) {
                System.out.println("Task threw exception: " + e.getCause());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
```

### Best Practices
```java
// 1. Use Future for task-based work
ExecutorService executor = Executors.newFixedThreadPool(10);
Future<String> future = executor.submit(() -> doWork());
try {
    String result = future.get(10, TimeUnit.SECONDS);
} catch (ExecutionException e) {
    handleException(e.getCause());
}

// 2. Set handler for threads
Thread thread = new Thread(task);
thread.setUncaughtExceptionHandler(exceptionHandler);
thread.start();

// 3. Wrap checked exceptions
executor.submit(() -> {
    try {
        riskyOperation();
    } catch (IOException e) {
        handleException(e);
    }
});

// 4. Always clean up in finally
Thread thread = new Thread(() -> {
    try {
        // Task
    } finally {
        cleanup();  // Always runs
    }
});
```

### Production Guidelines
- Always set UncaughtExceptionHandler
- Use Future for return values and exceptions
- Handle ExecutionException separately
- Implement global exception handler
- Log exceptions with full context
- Monitor thread failure rates
- Implement graceful recovery

---

## SUMMARY TABLE: WHEN TO USE WHAT

| Problem | Solution | Example |
|---------|----------|---------|
| Race condition | AtomicInteger or synchronized | counter++ |
| Deadlock | Lock ordering or timeout | account transfer |
| Starvation | Fair locks | ReentrantLock(true) |
| Memory visibility | volatile or synchronized | flag changes |
| Thread pool | Custom ThreadPoolExecutor | executor config |
| Lazy init | Double-checked locking or eager | Singleton |
| Producer-consumer | BlockingQueue | queue put/take |
| Return value | Callable + Future | async task |
| Thread shutdown | interrupt() + InterruptedException | graceful stop |
| Per-thread state | ThreadLocal | connection pool |
| Collection access | ConcurrentHashMap | concurrent cache |
| Complex sync | ReentrantLock + Condition | bounded buffer |
| One-time sync | CountDownLatch | wait for init |
| Phase sync | CyclicBarrier | pipeline stages |
| Lock-free ops | AtomicInteger | high frequency |
| Exception handling | Future.get() or handler | task failure |

---

## PRODUCTION CHECKLIST

- [ ] Use AtomicInteger for counters, not synchronized int
- [ ] Use ConcurrentHashMap, not HashMap + synchronization
- [ ] Establish lock ordering to prevent deadlock
- [ ] Always use try-finally to unlock
- [ ] Handle InterruptedException properly (restore flag)
- [ ] Use BlockingQueue for producer-consumer
- [ ] Set thread pool size based on workload
- [ ] Bound queue size (prevent OOM)
- [ ] Implement exception handler for threads
- [ ] Use Future.get() with timeout
- [ ] Clean up ThreadLocal in finally block
- [ ] Monitor queue depth, thread count, rejections
- [ ] Graceful shutdown: shutdown() -> awaitTermination() -> shutdownNow()
- [ ] Document all threading assumptions
- [ ] Test with different thread counts and timing variations
- [ ] Use thread dumps to detect deadlocks and starvation
- [ ] Monitor memory usage for leaks
- [ ] Implement circuit breakers for cascading failures

---

END OF COMPREHENSIVE GUIDE
