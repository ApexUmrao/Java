package MultiThreading;

public class EvenOddThread {

    // Two threads share a synchronized object. The odd thread waits when the counter is even,
    // and the even thread waits when the counter is odd. After printing, a thread increments
    // the counter and notifies the other thread. Using while around wait() handles spurious wakeups,
    // and notifyAll() avoids missed notifications. This ensures alternating printing of odd and even numbers.

    private int count = 1;
    private final static int MAX = 10;

    public void evenThread(){
        synchronized (this) {
            while (count < MAX) {
                while (count % 2 != 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(" Even  " + Thread.currentThread() + " --> " + count);
                count++;
                notify();
            }
        }
    }

    public void oddThread(){
        synchronized (this) {
            while (count < MAX) {
                while (count % 2 == 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(" Odd  " + Thread.currentThread() + " --> " + count);
                count++;
                notify();
            }
        }
    }

    public static void main(String[] args) {
        EvenOddThread obj = new EvenOddThread();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                obj.evenThread();
            }
        });
        Thread t2 = new Thread( new Runnable() {
            @Override
            public void run() {
                obj.oddThread();
            }
        });

        t1.start();
        t2.start();
    }
}
