import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

public abstract class Plot {
    private static Integer[] data;
    private static String title;
    static void createAndShowGUI() {
        JFrame frame = new JFrame("Colored Bar Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel());
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    static void setData(LinkedList<Integer> data) {
        Plot.data = Arrays.copyOf(data.toArray(), Philosopher2.avgWaitingTime.size(), Integer[].class);
    }

    static void setTitle(String title) {
        Plot.title = "Średni czas oczekiwania każdego filozofa na dostęp do widelców w wariancie " + title + " [ms]";
    }

    private static class ChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            String[] categories = new String[data.length];
            for (int i=0; i<categories.length; i++) {
                categories[i] = String.valueOf(i);
            }
            Color[] colors = {Color.blue, Color.magenta};

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
                int x = padding + i * barWidth + barWidth/2 - 5;
                g.drawString(categories[i], x, height - padding + 20);
            }

            // Rysowanie etykiety osi Y
            g.drawString(title, 10, 20);

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

                g.setColor(colors[i%2]);
                g.fillRect(x, y, barWidth, barHeight);

                // Opcjonalnie, rysowanie wartości nad słupkiem
                g.setColor(Color.black);
                g.drawString(String.valueOf(data[i]), x + barWidth / 2 - 3 * String.valueOf(data[i]).length(), y - 5);
            }
        }
    }
}
