import javax.swing.*;
import java.awt.*;

public class Main {
    public static int numberOfPhilosophers = 5;
    public static int thinkingAndEatingTime = 100;
    public static int simulationTime = 5000;
    private static int time2;
    private static int time3;
    private static int time5;
    private static int time6;

    public static void main(String[] args) throws InterruptedException {
        Main2.main(null);
        Main3.main(null);
        Main5.main(null);
        Main6.main(null);

        time2 = (int) (Philosopher2.totalWaitingTime / Philosopher2.totalWaitingCounter);
        time3 = (int) (Philosopher3.totalWaitingTime / Philosopher3.totalWaitingCounter);
        time5 = (int) (Philosopher5.totalWaitingTime / Philosopher5.totalWaitingCounter);
        time6 = (int) (Philosopher6.totalWaitingTime / Philosopher6.totalWaitingCounter);

        System.out.println();
        System.out.println("2: " + time2);
        System.out.println("3: " + time3);
        System.out.println("5: " + time5);
        System.out.println("6: " + time6);
        System.out.println(Philosopher2.avgWaitingTime);
        System.out.println(Philosopher3.avgWaitingTime);
        System.out.println(Philosopher5.avgWaitingTime);
        System.out.println(Philosopher6.avgWaitingTime);

        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Colored Bar Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel());
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private static class ChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int[] data = {time2, time3, time5, time6};
            String[] categories = {"Z możliwością zagłodzenia", "Asymetryczne", "Z arbitrem", "Z jadalnią"};
            Color[] colors = {Color.red, Color.blue, Color.magenta, Color.orange};

            int width = getWidth();
            int height = getHeight();
            int padding = 50;
            int max = 0;
            int barWidth = (width - 2 * padding) / data.length;

            for (int time : data) {
                if (max < time) {
                    max = time;
                }
            }

            if (max % 30 != 0) max = max - max % 30 + 30;

            // Rysowanie osi X i Y
            g.drawLine(padding, height - padding, padding, padding);
            g.drawLine(padding, height - padding, width - padding, height - padding);

            // Rysowanie etykiet osi X
            for (int i = 0; i < data.length; i++) {
                int x = padding + i * barWidth + barWidth/2 - 3 * categories[i].length();
                g.drawString(categories[i], x, height - padding + 20);
            }

            // Rysowanie etykiety osi Y
            g.drawString("Średni czas oczekiwania filozofów na dostęp do widelców w zależności od wariantu implementacji [ms]", 10, 20);

            // Rysowanie etykiet osi Y
            for (int i = 0; i <= max; i+=30) {
                int y = height - padding - i * (height - 2 * padding) / max;
                g.drawString(String.valueOf(i), padding - 20, y);
            }

            // Rysowanie słupków z różnymi kolorami
            for (int i = 0; i < data.length; i++) {
                int x = padding + i * barWidth;
                int y = height - padding - data[i] * (height - 2 * padding) / max;
                int barHeight = data[i] * (height - 2 * padding) / max;

                g.setColor(colors[i]);
                g.fillRect(x, y, barWidth, barHeight);

                // Opcjonalnie, rysowanie wartości nad słupkiem
                g.setColor(Color.black);
                g.drawString(String.valueOf(data[i]), x + barWidth / 2 - 7, y - 5);
            }
        }
    }
}
