import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Ilia
 * Date: 28.09.13
 */
class ImageProcessing {

    private static final double BRACKETS_RATE = 0.03;
    //Удалять ли пробелы между минусом и цифрой автоматически
    private static final boolean OPTIMIZE_COLUMNS_BY_DEFAULT = true;
    private static final double MIN_PERCENT_OF_BLACK_ROW = 0.005;
    private static final double MIN_PERCENT_OF_BLACK_COLUMN = 0.01;
    private static final double PERCENT_OF_BLACK_PIXELS = 0.7;
    private static final int BRACKET_FINDER_STEP = 25;

    //Массивы с координатами начал и концов столлбцов и строк соответственно.
    //TODO: Придумать новые имена
    @NotNull
    private static ArrayList<Integer[]> NumbersX = new ArrayList<Integer[]>();
    private static final ArrayList<Integer[]> NumbersY = new ArrayList<Integer[]>();
    private static final int FADE_STEP = 25;


    /**
     * Удаление шума с изображения
     *
     * @param raw  Изображение для обработки
     * @param step Шаг обработки
     * @return Обработанное изображение
     */
    @NotNull
    public static BufferedImage RemoveNoise(Image raw, int step) {
        BufferedImage bufferedImage = (BufferedImage) raw;
        BufferedImage noiseFree = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        for (int i = step - 1; i < bufferedImage.getWidth(); i += step)
            for (int j = step - 1; j < bufferedImage.getHeight(); j += step) {

                int sum = GetCountOfBlack(bufferedImage, i, j, step);

                if (sum < step * step / 3) {
                    PaintBlock(noiseFree, i, j, step, Color.WHITE);
                } else if (sum > step * step * 2 / 3) {
                    PaintBlock(noiseFree, i, j, step, Color.BLACK);
                } else {
                    CopyBlock(bufferedImage, noiseFree, i, j, step);
                }
            }
        return noiseFree;
    }

    private static void CopyBlock(@NotNull BufferedImage source, @NotNull BufferedImage dest, int x, int y, int step) {
        for (int _i = x - step + 1; _i <= x; _i++)
            for (int _j = y - step + 1; _j <= y; _j++)
                dest.setRGB(_i, _j, source.getRGB(_i, _j));
    }

    private static void PaintBlock(@NotNull BufferedImage image, int x, int y, int step, @NotNull Color color) {

        for (int _i = x - step + 1; _i <= x; _i++)
            for (int _j = y - step + 1; _j <= y; _j++)
                image.setRGB(_i, _j, color.getRGB());
    }

    private static int GetCountOfBlack(@NotNull BufferedImage bufferedImage, int x, int y, int step) {
        int sum = 0;

        //Проверка х, чтобы не выходил за пределы image
        int xStart = x - step + 1;
        if (xStart < 0)
            xStart = 0;

        //Проверка y, чтобы не выходил за пределы image
        int yStart = y - step + 1;
        if (yStart < 0)
            yStart = 0;

        for (int _i = xStart; _i < x; _i++)
            for (int _j = yStart; _j < y; _j++)
                sum += ImageProperties.IsBlack(bufferedImage.getRGB(_i, _j));
        return sum;
    }

    //TODO рефакторить функцию RemoveBrackets

    /**
     * Удаление скобок с изображения
     *
     * @param raw Исходное изображение
     * @return Обработанное изображение
     */
    public static BufferedImage RemoveBrackets(BufferedImage raw) {
        BufferedImage bufferedImage = raw;
        int startX = 0, endX = 0;
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            int sum = 0;
            for (int j = 0; j < bufferedImage.getHeight(); j++)
                sum += ImageProperties.IsBlack(bufferedImage.getRGB(i, j));
            if (sum >= bufferedImage.getHeight() * BRACKETS_RATE) {
                startX = i;
                break;
            }
        }
        for (int i = startX; i < bufferedImage.getWidth(); i += BRACKET_FINDER_STEP) {
            int sum = 0;
            for (int j = 0; j < bufferedImage.getHeight(); j++)
                sum += ImageProperties.IsBlack(bufferedImage.getRGB(i, j));
            if (sum == 0) {
                endX = i;
                break;
            }
        }
        bufferedImage = ImageDrawing.ReplaceColumnsWith(bufferedImage, startX, endX, Color.WHITE);
        startX = 0;
        endX = 0;
        for (int i = bufferedImage.getWidth() - 1; i >= 0; i--) {
            int sum = 0;
            for (int j = 0; j < bufferedImage.getHeight(); j++)
                sum += ImageProperties.IsBlack(bufferedImage.getRGB(i, j));
            if (sum >= bufferedImage.getHeight() * BRACKETS_RATE) {
                endX = i;
                break;
            }
        }
        for (int i = endX; i >= 0; i -= BRACKET_FINDER_STEP) {
            int sum = 0;
            for (int j = 0; j < bufferedImage.getHeight(); j++)
                sum += ImageProperties.IsBlack(bufferedImage.getRGB(i, j));
            if (sum == 0) {
                startX = i;
                break;
            }
        }

        bufferedImage = ImageDrawing.ReplaceColumnsWith(bufferedImage, startX, endX, Color.WHITE);
        return bufferedImage;
    }

    /**
     * Заполняет массив NumbersX координатами начал и концов строк
     *
     * @param raw Изображения для анализа
     */
    public static void FindRows(@NotNull BufferedImage raw) {

        int pixels[] = new int[raw.getWidth()];
        raw.getRGB(0, 0, raw.getWidth(), 1, pixels, 0, 1);
        boolean previous = ImageProperties.HasBlackRow(pixels, (int) (raw.getWidth() * MIN_PERCENT_OF_BLACK_ROW));

        //StartY - у начала строки, EndY - y конца строки
        for (int i = 1, StartY = 0; i < raw.getHeight(); i++) {
            raw.getRGB(0, i, raw.getWidth(), 1, pixels, 0, 1);
            boolean current = ImageProperties.HasBlackRow(pixels, (int) (raw.getWidth() * MIN_PERCENT_OF_BLACK_ROW));

            //Переход с белого на черный
            if (StartY == 0 && !previous && current) {
                previous = current;
                StartY = i;
            }
            //Переход с черного на белый
            //            if ( StartY!= 0 && previous && !current)
            //Это то же самое условие, но Идея догадалась его упростить
            if (StartY != 0 && !current) {
                previous = current;
                int EndY = i;
                Integer[] temp = new Integer[2];
                temp[0] = StartY;
                temp[1] = EndY;
                NumbersY.add(temp);
                StartY = 0;
            }
        }
    }

    /**
     * Заполняет массив NumbersY координатами начал и концов столбцов
     *
     * @param raw Изображения для анализа
     */
    public static void FindColumns(@NotNull BufferedImage raw) {

        int pixels[] = new int[raw.getHeight()];
        raw.getRGB(0, 0, 1, raw.getHeight(), pixels, 0, 1);
        boolean previous = ImageProperties.HasBlackRow(pixels, (int) (raw.getHeight() * MIN_PERCENT_OF_BLACK_COLUMN));

        //StartX - x начала колонки,  EndX - x её конца
        for (int i = 1, StartX = 0; i < raw.getWidth(); i++) {
            raw.getRGB(i, 0, 1, raw.getHeight(), pixels, 0, 1);
            boolean current = ImageProperties.HasBlackRow(pixels, (int) (raw.getHeight() * MIN_PERCENT_OF_BLACK_COLUMN));

            //Переход с белого на черный
            if (StartX == 0 && !previous && current) {
                StartX = i;
                previous = current;
            }
            //Переход с черного на белый
            if (StartX != 0 && !current) {
                previous = current;
                int EndX = i;
                //Добавляем в массив колонок информацию о текущей
                Integer[] temp = new Integer[2];
                temp[0] = StartX;
                temp[1] = EndX;
                NumbersX.add(temp);
                StartX = 0;
            }
        }

        if (OPTIMIZE_COLUMNS_BY_DEFAULT)
            NumbersX = OptimizeColumnsArray(NumbersX);

    }

    /**
     * Обводит прямоугольниками все цифры на image
     * FindColumns и FindRows Должны быть вызваны заранее
     *
     * @param image - изображение для отрисовки
     */
    static void DrawRectangles(@NotNull BufferedImage image) {
        for (Integer[] a : NumbersX)
            for (Integer[] b : NumbersY)
                ImageDrawing.DrawRectangle(image, a[0], b[0], a[1], b[1]);
    }

    /**
     * Убирает из массива колонок те, которые находятся на слишком малом расстоянии
     * Например Колонку знака объединяет с колонкой цифры
     *
     * @param numbersX - Лист координат начал и концов колонок
     * @return Оптимизированный лист
     */
    @NotNull
    private static ArrayList<Integer[]> OptimizeColumnsArray(@NotNull java.util.List<Integer[]> numbersX) {

        //Находим среднее расстояние между колонками
        int lastX = numbersX.get(0)[0], sumDistance = 0;
        for (Integer[] temp : numbersX) {
            int distance = temp[0] - lastX;
            sumDistance += distance;
            lastX = temp[1];
        }

        int distance = sumDistance / (numbersX.size() - 1);

        //Массив для вывода
        ArrayList<Integer[]> resultList = new ArrayList<Integer[]>();

        Integer[] last, current;
        for (int i = 1; i < numbersX.size(); i++) {
            last = NumbersX.get(i - 1);
            current = NumbersX.get(i);
            if (current[0] - last[1] > distance / 2) {
                resultList.add(last);
            } else {
                i++;
                Integer[] temp = new Integer[2];
                temp[0] = last[0];
                temp[1] = current[1];
                resultList.add(temp);
            }
        }
        return resultList;
    }


    /**
     * Создает бинаризированное изображение
     *
     * @param raw Изображение для обработки
     * @return Бинаризированное изображение
     */
    @NotNull
    public static BufferedImage GetBinarized(@NotNull BufferedImage raw) {
        BufferedImage binarized = new BufferedImage(raw.getWidth(), raw.getHeight(), raw.getType());
        int colors[] = new int[256];

        for (int i = 0; i < raw.getWidth(); i++)
            for (int j = 0; j < raw.getHeight(); j++) {
                int tmp = raw.getRGB(i, j);
                // Определяем количество каждого оттенка серого
                colors[ImageProperties.GetColorSum(tmp) / 3]++;
            }

        // Определяем опорную костанту изоборажения для определения граней как самый встречающийся цвет
        int edge = IndexOfMaximum(colors);

        BinarizeByEdge(raw, binarized, 2 * edge);
        // Размер квадарата для поиска теней.
        // Обходим изобрадение с шагом 2 FADE_STEP
        for (int i = 0; i < raw.getWidth() - FADE_STEP; i += 2 * FADE_STEP)
            for (int j = 0; j < raw.getHeight() - FADE_STEP; j += 2 * FADE_STEP) {

                int sum = GetCountOfBlack(binarized, i + FADE_STEP, j + FADE_STEP, FADE_STEP);
                if (sum >= FADE_STEP * FADE_STEP * PERCENT_OF_BLACK_PIXELS)
                    RemoveBlackSquare(raw, binarized, edge, FADE_STEP, i, j);
            }
        return binarized;
    }

    private static void RemoveBlackSquare(@NotNull BufferedImage raw, @NotNull BufferedImage binarized, int edge, int step, int x, int y) {

        //Проверка улсовий,чтобы х,у не велезли за предел массива
        int xStart = x - step;
        if (xStart < 0)
            xStart = 0;

        int xEnd = x + 2 * step;
        if (xEnd > raw.getWidth())
            xEnd = raw.getWidth();

        int yStart = y - step;
        if (yStart < 0)
            yStart = 0;

        int yEnd = y + 2 * step;
        if (yEnd > raw.getHeight())
            yEnd = raw.getHeight();

        for (int i = xStart; i < xEnd; i++)
            for (int j = yStart; j < yEnd; j++)

                if (ImageProperties.GetColorSum(raw.getRGB(i, j)) >= edge + 5)
                    // Бинаризируем квадрат с порогом для затемненных изображений
                    binarized.setRGB(i, j, Color.WHITE.getRGB());
                else
                    binarized.setRGB(i, j, Color.BLACK.getRGB());
    }

    private static void BinarizeByEdge(@NotNull BufferedImage raw, @NotNull BufferedImage binarized, int edge) {
        for (int i = 0; i < raw.getWidth(); i++)
            for (int j = 0; j < raw.getHeight(); j++)
                if (ImageProperties.GetColorSum(raw.getRGB(i, j)) >= edge)
                    binarized.setRGB(i, j, Color.WHITE.getRGB());
                else
                    binarized.setRGB(i, j, Color.BLACK.getRGB());
    }

    /**
     * Возвращает индекс максимального элемента массива
     *
     * @param colors Массив для поиска
     * @return Индек максимального элмента
     */
    private static int IndexOfMaximum(int[] colors) {
        int edge = 0;
        for (int i = 254, max = 0; i > 0; i--) {
            if (max < colors[i]) {
                max = colors[i];
                edge = i;
            }
        }
        return edge;
    }

}