import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Texture {
    public static Texture text1 = new Texture("res/texture1.png", 64);
    public static Texture text2 = new Texture("res/texture2.png", 64);
    public static Texture text3 = new Texture("res/texture3.png", 64);
    public static Texture text4 = new Texture("res/texture4.png", 64);
    
    public int[] pixels;
    private String loc;
    public final int SIZE;

    public Texture(String location, int size) {
        loc = location;
        SIZE = size;
        pixels = new int[SIZE * SIZE];
        load();
    }

    private void load() {
        try {
            BufferedImage image = ImageIO.read(new File(loc));
            int w = image.getWidth();
            int h = image.getHeight();
            image.getRGB(0, 0, w, h, pixels, 0, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getSize() {
        return SIZE;
    }
}
