import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Anton
 * Date: 06.10.13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
class Neuron {

    //Картинка, содержащая вероятности нахождения пикселей. черный - 100% -> Белый 0%
    private BufferedImage base;
    //Путь к текущему нейрону
    private final String path;


    Neuron(String path) {
        this.path = path;
        try {
            base = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Neuron(String path, BufferedImage image) {
        this.path = path;
        try {
            File file = new File(path);
            file.createNewFile();
            //Resize image
            BufferedImage resized = resizeImage(image, Network.DEFAULT_WIDTH, Network.DEFAULT_HEIGHT);
            base = resized;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обучает нейрон изображению, беря для каждого пикселя среднее между имеющимся цветом
     * и предлагаемым.
     *
     * @param sample Изображение для обучения
     */
    public void Teach(BufferedImage sample) {
        for (int x = 0; x < base.getWidth(); x++)
            for (int y = 0; y < base.getHeight(); y++) {
                int baseColor = base.getRGB(x, y);
                int sampleColor = sample.getRGB(x, y);
                //Не оптимизировать полусумму!
                int resultColor = (baseColor) / 2 + sampleColor / 2;
                base.setRGB(x, y, resultColor);
            }
    }

    /**
     * Некое подобие вероятности того, что данный нейрон совпадает с приведенным
     * (отличие от понятие вероятности в return)
     *
     * @param sample Изображение для сравнения с
     * @return Степень схожести изображений (может быть >1)
     */
    public double getConcurrence(BufferedImage sample) {
        double result = 0.0;
        /* return result; */
        BufferedImage mesurable = resizeImage(sample, 60, 60);

        for (int x = 0; x < base.getWidth(); x++)
            for (int y = 0; y < base.getHeight(); y++) {
                int baseColor = base.getRGB(x, y);
                int sampleColor = mesurable.getRGB(x, y);
                if (sampleColor == Color.BLACK.getRGB())
                    result += baseColor / sampleColor;
            }

        return result;
    }

    /**
     * Сохраняет нейрон в файл
     */
    public void Save() {
        try {
            ImageIO.write(base, Network.DEFAULT_FORMAT, new File(path));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @NotNull
    private BufferedImage resizeImage(@NotNull BufferedImage source, int newWidth, int newHeight) {

        Image temp = source.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);


        //Преобразуем Image temp в BufferedImage
        BufferedImage buffered = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        buffered.getGraphics().drawImage(temp, 0, 0, null);
        buffered.getGraphics().dispose();
        return buffered;
    }
}
