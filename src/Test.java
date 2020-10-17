import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        BufferedImage bImage;
        try {
            File initialImage = new File("Shot 0006.png");
            bImage = ImageIO.read(initialImage);
            //ImageIO.write(bImage, "gif", new File("C://Users/Rou/Desktop/image.gif"));
            ImageIO.write(bImage, "jpg", new File("TEST.jpg"));
           // ImageIO.write(bImage, "bmp", new File("C://Users/Rou/Desktop/image.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

