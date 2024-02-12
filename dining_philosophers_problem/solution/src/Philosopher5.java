import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Philosopher5 implements Runnable {
    private final Object leftFork;
    private final Object rightFork;
    private static Semaphore semaphore;

    public static LinkedList<Integer> avgWaitingTime = new LinkedList<>();
    public static long totalWaitingTime = 0;
    public static int totalWaitingCounter = 0;

    private long waitingTime = 0;
    private int waitingCounter = 0;

    public Philosopher5(Object leftFork, Object rightFork) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    private void doAction(String action) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + action);
    }

    public static void setSemaphore(int n) {
        semaphore = new Semaphore(n-1);
    }

    private static synchronized void addToTotalTime(long waitingTime, int waitingCounter) {
        totalWaitingTime += waitingTime;
        totalWaitingCounter += waitingCounter;
        avgWaitingTime.add((Math.round((float) waitingTime / waitingCounter)));
    }

    @Override
    public void run() {
        try {
            while (true) {
                doAction(": Thinking");
                sleeping();

                long start = System.currentTimeMillis();

                while (!semaphore.tryAcquire()){
                    sleep(1);
                }

                synchronized (leftFork) {
                    doAction(": Picked up left fork");

                    synchronized (rightFork) {
                        waitingTime += System.currentTimeMillis() - start;
                        waitingCounter += 1;

                        doAction(": Picked up right fork - eating");
                        sleeping();

                        doAction(": Put down right fork");
                    }

                    semaphore.release();
                    doAction(": Put down left fork. Back to thinking");
                }
            }
        } catch (InterruptedException e) {
            addToTotalTime(waitingTime, waitingCounter);
            Thread.currentThread().interrupt();
        }
    }

    private static void sleeping() throws InterruptedException {
        sleep(Main.thinkingAndEatingTime);
    }

}