import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
public class JuliaFractal extends Thread {
    final static int N = 4096;
    static int[][] colors = new int[N][N];

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        JuliaFractal thread0 = new JuliaFractal(0);
        JuliaFractal thread1 = new JuliaFractal(1);
        JuliaFractal thread2 = new JuliaFractal(2);
        JuliaFractal thread3 = new JuliaFractal(3);
        thread0.start();
        thread1.start();
        thread2.start();
        thread3.start();
        thread0.join();
        thread1.join();
        thread2.join();
        thread3.join();
        long endTime = System.currentTimeMillis();
        System.out.println("Obliczenia zakończone w czasie " + (endTime - startTime) + " millisekund");
        BufferedImage img = new BufferedImage(N, N, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                img.setRGB(i, j, colors[i][j]);
            }
        }
        ImageIO.write(img, "PNG", new File("Julia.png"));
    }
    int me;
    int MAX_ITERATIONS = 300;
    double ZOOM = 1;
    double CX = -0.7;
    double CY = 0.27015;
    double MOVE_X = 0;
    double MOVE_Y = 0;
    public JuliaFractal(int me) {
        this.me = me;
    }
    public void run() {
        int begin = 0, end = 0;
        if (me == 0) {
            begin = 0;
            end = (N / 4) * 1;
        }
        else if (me == 1) {
            begin = (N / 4) * 1;
            end = (N / 4) * 2;
        }
        else if (me == 2) {
            begin = (N / 4) * 2;
            end = (N / 4) * 3;
        }
        else if (me == 3) {
            begin = (N / 4) * 3;
            end = N;
        }
        for (int i = begin; i < end; i++) {
            for (int j = 0; j < N; j++) {
                double zx = 1.5 * (i - N / 2.0) / (0.5 * ZOOM * N) + MOVE_X;
                double zy = (j - N / 2.0) / (0.5 * ZOOM * N) + MOVE_Y;
                float iter = MAX_ITERATIONS;
                while (zx * zx + zy * zy < 4 && iter > 0) {
                    double tmp = zx * zx - zy * zy + CX;
                    zy = 2.0 * zx * zy + CY;
                    zx = tmp;
                    iter--;
                }
                colors[i][j] = Color.HSBtoRGB((MAX_ITERATIONS / iter) % 1, 1, iter > 0 ? 1 : 0);
            }
        }
    }
}