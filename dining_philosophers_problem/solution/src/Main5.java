import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.lang.Thread.sleep;

public class Main5 extends Plot {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService e = Executors.newCachedThreadPool();

        Philosopher5[] philosophers = new Philosopher5[Main.numberOfPhilosophers];
        Object[] forks = new Object[philosophers.length];
        Philosopher5.setSemaphore(philosophers.length);

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < philosophers.length; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % forks.length];

            philosophers[i] = new Philosopher5(leftFork, rightFork);

            Thread t = new Thread(philosophers[i], "Philosopher " + (i + 1));
            e.execute(t);
        }

        sleep(Main.simulationTime);
        e.shutdownNow();
        sleep(100);

        setData(Philosopher5.avgWaitingTime);
        setTitle("z arbitrem");
        SwingUtilities.invokeLater(Plot::createAndShowGUI);
    }
}