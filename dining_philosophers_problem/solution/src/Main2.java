import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class Main2 extends Plot {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService e = Executors.newCachedThreadPool();
        Philosopher2[] philosophers = new Philosopher2[Main.numberOfPhilosophers];
        ReentrantLock[] forks = new ReentrantLock[philosophers.length];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new ReentrantLock();
        }

        for (int i = 0; i < philosophers.length; i++) {
            ReentrantLock leftFork = forks[i];
            ReentrantLock rightFork = forks[(i + 1) % forks.length];

            philosophers[i] = new Philosopher2(leftFork, rightFork);

            Thread t = new Thread(philosophers[i], "Philosopher " + (i + 1));
            e.execute(t);
        }
        sleep(Main.simulationTime);
        e.shutdownNow();
        sleep(100);
        System.out.println(Philosopher2.avgWaitingTime);

        setData(Philosopher2.avgWaitingTime);
        setTitle("z możliwością zagłodzenia");
        SwingUtilities.invokeLater(Plot::createAndShowGUI);
    }


}