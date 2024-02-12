import java.util.LinkedList;

import static java.lang.Thread.sleep;

public class Philosopher3 implements Runnable {
    private final Object leftFork;
    private final Object rightFork;
    private final int index;

    public static LinkedList<Integer> avgWaitingTime = new LinkedList<>();
    public static long totalWaitingTime = 0;
    public static int totalWaitingCounter = 0;

    private long waitingTime = 0;
    private int waitingCounter = 0;

    public Philosopher3(Object leftFork, Object rightFork, int index) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.index = index;
    }

    private void doAction(String action) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + action);
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

                if (index % 2 == 0) {
                    synchronized (rightFork) {
                        doAction(": Picked up right fork");
                        synchronized (leftFork) {
                            waitingTime += System.currentTimeMillis() - start;
                            waitingCounter += 1;

                            doAction(": Picked up left fork - eating");
                            sleeping();

                            doAction(": Put down left fork");
                        }
                        doAction(": Put down right fork. Back to thinking");
                    }
                } else {
                    synchronized (leftFork) {
                        doAction(": Picked up left fork");
                        synchronized (rightFork) {
                            waitingTime += System.currentTimeMillis() - start;
                            waitingCounter += 1;

                            doAction(": Picked up right fork - eating");
                            sleeping();

                            doAction(": Put down right fork");
                        }
                        doAction(": Put down left fork. Back to thinking");
                    }
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