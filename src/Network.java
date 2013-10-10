import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Anton
 * Date: 06.10.13
 * Time: 17:36
 * To change this template use File | Settings | File Templates.
 */
public class Network {
    private static final String DEFAULT_PATH = "C:\\Temp\\Network\\";
    private static final int DEFAULT_HEIGHT = 60, DEFAULT_WIDTH = 60;
    private static final int COUNT = 11; //цифры 0..9, '-'
    protected static final String DEFAULT_FORMAT = "jpg";
    private static final boolean AUTO_TEACH = true;
    //Константа, обозначающая нейрон "минус"
    protected static final int MINUS = 10;

    private Neuron[] neurons;


    //TODO Расширить на числа, большие 9 или меньшие 0
    //TODO Обрезать границы входных изобрадений, подгонять по размеру

    /**
     * Создает изображения нейронов, если они не существовали
     * Загружает их в neurons[]
     */
    Network()
    {
        File folder = new File(DEFAULT_PATH);

        if (!folder.exists())
        {
            try {

                folder.mkdir();
                for (int i = 0; i <COUNT; i++)
                    createImage(createPath(i));

            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        neurons = new Neuron[COUNT];
        for(int i = 0; i <COUNT; i++)
            neurons[i] = new Neuron(createPath(i));
    }

    /**
     * Возвращет путь для нейрона с цифрой number
     * @param number имя нейрона
     * @return путь к нейрону
     */
    private String createPath(int number) {
        return DEFAULT_PATH+ number +"."+DEFAULT_FORMAT;
    }

    /**
     * Создает изобраение белого цвета стандартных размеров
     * @param path  адрес будущего изображения
     * @throws IOException
     */
    private void createImage(String path) throws IOException {
        File file = new File(path);
        file.createNewFile();

        BufferedImage image = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < DEFAULT_WIDTH; x++)
            for (int y = 0; y < DEFAULT_HEIGHT; y++)
                image.setRGB(x,y, Color.WHITE.getRGB());
        ImageIO.write(image, DEFAULT_FORMAT, file );

    }

    /**
     * Обучает нейрон
     * @param image изобрадение для обучения
     * @param number название нейрона
     */
    public  void Teach(BufferedImage image, int number)
    {
        if (number < COUNT)
        {
            neurons[number].Teach(image);
        }

    }

    /**
     * Возвращет препалагаему цифру
     * @param image изображение для определения цифры
     * @return
     */
    public int getNumber(BufferedImage image)
    {
        double concurrence = 0;
        int number = -1;
        for (int i = 0; i < COUNT; i++)
        {
            double tempConcurrence = neurons[i].getConcurrence(image);
            if (tempConcurrence > concurrence)
            {
                concurrence = tempConcurrence;
                number = i;
            }
        }
        if (AUTO_TEACH)
            neurons[number].Teach(image);
        //Если нашли минус...
        if (number == 10)
            return MINUS;
        return number;
    }

    public void saveNetwork()
    {
        for (Neuron neuron : neurons)
            neuron.Save();
    }

}
