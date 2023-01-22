import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Random;

public class MonteCarlo extends Thread {
    int radius, id;
    double x, y;
    double area_square;
    Random rand = new Random();
    int total, in_circle;
    Helper h;
    int sample_size;
    MonteCarlo(int radius, Helper h, int id, int sample_size) {
        this.radius = radius;
        area_square = radius * 2 * radius * 2;
        this.total = 0;
        this.in_circle = 0;
        this.h = h;
        this.id = id;
        this.sample_size = sample_size;
    }
    static boolean isPointInCircle(double x, double y, int radius) {
        return Math.sqrt(Math.pow((x - radius), 2) + Math.pow((y - radius), 2)) <= radius;
    }
    public void run() {
        for (int i = 0; i < sample_size; i++) {
            double x = rand.nextDouble(radius * 2);
            double y = rand.nextDouble(radius * 2);
            if (isPointInCircle(x, y, radius)) {
                in_circle++;
            }
            total++;
        }
        h.getData(id, in_circle, total, area_square);
    }
}

class Helper {
    List<Integer> in_circle_arr = new ArrayList<>();
    List<Integer> total_arr = new ArrayList<>();
    synchronized void getData(int id, int in_circle, int total, double area_square) {
        in_circle_arr.add(in_circle);
        total_arr.add(total);
        double area = (in_circle / (double)total) * area_square;
        System.out.println("#" + id + " area: " + area + " in_circle/total: " + in_circle + "/" + total
                + " (" + (in_circle / (double)total) * 100 + "%)");
    }

    double result(double area_square) {
        OptionalDouble average_in_circle = in_circle_arr
                .stream()
                .mapToDouble(a -> a)
                .average();
        OptionalDouble average_total = total_arr
                .stream()
                .mapToDouble(a -> a)
                .average();
        return average_in_circle.getAsDouble() / average_total.getAsDouble() * area_square;
    }
}

class Main {
    public static void main(String[] args) throws InterruptedException {
        Helper h = new Helper();
        int radius = 3;
        double expected = Math.pow(radius, 2) * Math.PI;
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            MonteCarlo m = new MonteCarlo(radius, h, i, 10000);
            m.start();
            threadList.add(m);
        }
        for (Thread t : threadList) {
            t.join();
        }
        System.out.println("Result: " + h.result(radius * 2 * radius * 2) + " Expected: " + expected);
    }
}
