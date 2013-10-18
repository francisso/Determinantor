/**
 * Created with IntelliJ IDEA.
 * User: Ilia
 * Date: 15.09.13
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Ilia
 * Date: 01.09.13
 */

public class NewCore extends Component {
    private final static boolean DEBUG = true;
    private final static String DEBUG_FILENAME = "C:\\IMAG0242`.jpg";
    private static final float MAX_PICTURE_SIDE = 1000.f;
    @Nullable
    private BufferedImage img = null, image = null;

    public static void main(String[] args) {
        JFrame f = new JFrame("Load Image Sample");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

        });
        f.add(new NewCore());
        f.pack();
        f.setVisible(true);
    }

    private NewCore() {
        try {
            img = (BufferedImage) GetImage();
        } catch (Exception ignored) {
        }
    }

    public void paint(@NotNull Graphics g) {
        /*TODO перенести imageProcessing в соответствующий класс
        создавая здесь экзэпляр ImageProcessing инициализируемый image
         и функцией GetImage() возвращающей обработанную картинку*/
        if (image == null)
            image = imageProcessing();
        g.drawImage(image, 0, 0, null);
    }

    @Nullable
    private BufferedImage imageProcessing() {

        Network net = new Network();
        image = img;
        image = resizeImage(image);
        ImageProcessing processing = new ImageProcessing(image);
        processing.defaultProcess();
        image = processing.getImage();
        ImageProcessing.DrawRectangles(image);
        return image;
    }

    @NotNull
    private BufferedImage resizeImage(@NotNull BufferedImage source) {
        float k = MAX_PICTURE_SIDE / ((source.getWidth() > source.getHeight()) ? source.getWidth() : source.getHeight());
        int width = (int) (source.getWidth() * k);
        int height = (int) (source.getHeight() * k);
        Image temp = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);


        //Преобразуем Image temp в BufferedImage
        BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffered.getGraphics().drawImage(temp, 0, 0, null);
        buffered.getGraphics().dispose();
        return buffered;
    }

    @NotNull
    public Dimension getPreferredSize() {
        if (img == null) {
            return new Dimension(100, 100);
        } else {
            float k = MAX_PICTURE_SIDE / ((img.getWidth() > img.getHeight()) ? img.getWidth() : img.getHeight());
            int width = (int) (img.getWidth() * k);
            int height = (int) (img.getHeight() * k);
            return new Dimension(width, height);
        }
    }

    @Nullable
    private Image GetImage() throws IOException {
        //PC imp
        if (DEBUG)
            return (ImageIO.read(new File(DEBUG_FILENAME)));
        JFileChooser chooser = new JFileChooser();
        if (chooser.showDialog(this, "Choose") == JFileChooser.APPROVE_OPTION) {
            return ImageIO.read(chooser.getSelectedFile());
        }
        return null;
    }
}

