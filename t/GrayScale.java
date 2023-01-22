import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
public class GrayScale extends Thread {
    BufferedImage image;
    int width;
    int width_start;
    int height;
    int height_start;
    HelperGrayScale h;
    public GrayScale(int width_start, int width, int height_start, int height, HelperGrayScale h, BufferedImage image) {
        this.width_start = width_start;
        this.width = width;
        this.height_start = height_start;
        this.height = height;
        this.h = h;
        this.image = image;
    }

    public void run() {
        for(int i=height_start; i<height-1; i++){
            for(int j=width_start; j<width-1; j++){

                //odczyt składowych koloru RGB
                Color c = new Color(image.getRGB(j, i));
                int red = (int)(c.getRed());
                int green = (int)(c.getGreen());
                int blue = (int)(c.getBlue());

                int final_red, final_green, final_blue;

                //negatyw
                final_red = 255-red;
                final_green = 255-green;
                final_blue = 255-blue;
                h.setColor(final_red, final_green, final_blue, i, j);
            } //koniec dwóch pętli po kolumnach i wierszach obrazu
        }
    }
}

class MainGrayScale {
    public static void main(String[] args) {
        BufferedImage image;
        int width;
        int height;
        List<Thread> threadList = new ArrayList<>();
        try {
            File input = new File("src/image.jpg");
            image = ImageIO.read(input);
            width = image.getWidth();
            height = image.getHeight();
            HelperGrayScale h = new HelperGrayScale(image);
            int start_width = 1;
            int start_height = 1;
            int width_dif = width / 2;
            int height_dif = height / 2;

            GrayScale gs1 = new GrayScale(start_width, width_dif, start_height, height_dif, h, image);
            gs1.start();
            threadList.add(gs1);
            GrayScale gs2 = new GrayScale(width_dif - 1, width, start_height, height_dif, h, image);
            gs2.start();
            threadList.add(gs2);
            GrayScale gs3 = new GrayScale(start_width, width_dif, height_dif - 1, height, h, image);
            gs3.start();
            threadList.add(gs3);
            GrayScale gs4 = new GrayScale(width_dif - 1, width, height_dif - 1, height, h, image);
            gs4.start();
            threadList.add(gs4);

            for (Thread t : threadList) {
                t.join();
            }

            h.save();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class HelperGrayScale {
    BufferedImage image;
    int width, height;
    Color rgb[][];
    HelperGrayScale(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.rgb = new Color[height][width];
    }
    synchronized public void setColor(int r, int g, int b, int height, int width) {
        Color newColor = new Color(r, g, b);
        rgb[height][width] = newColor;
    }

    public void save() throws IOException {
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++){
                try {
                    image.setRGB(j, i, rgb[i][j].getRGB());
                } catch (Exception ignored) {
                }
            }
        }
        File ouptut = new File("grayscale.jpg");
        ImageIO.write(image, "jpg", ouptut);
    }
}