import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

class ImageDrawing {
    private static final int LINE_SIZE = 3;

    //TODO: Прокомментировать эти скчные функции
    //TODO:  Подумать, надо ли делать вовсех этих функциях проверку на допустимость входных параметров

    /**
     * Заменяет выбранный столбец в изображении на заданный цвет
     *
     * @param raw         Исходное изображение
     * @param columnStart Номер начальной колонки
     * @param columnEnd   Номер последней колонки для замены
     * @param color       Цвет для замены
     * @return Обработанное изображение
     */
    @NotNull
    static BufferedImage ReplaceColumnsWith(@NotNull BufferedImage raw, int columnStart, int columnEnd, @NotNull Color color) {
        for (int i = columnStart; i <= columnEnd; i++)
            for (int j = 0; j < raw.getHeight(); j++)
                raw.setRGB(i, j, color.getRGB());
        return raw;
    }

    //<editor-fold desc="Draw Horizontal line">
    static void DrawHorizontalLine(@NotNull BufferedImage image, int y) {
        DrawHorizontalLine(image, y, LINE_SIZE);
    }

    private static void DrawHorizontalLine(@NotNull BufferedImage image, int y, int lineSize) {
        DrawHorizontalLine(image, y, lineSize, Color.RED);
    }

    private static void DrawHorizontalLine(@NotNull BufferedImage image, int y, int lineSize, @NotNull Color color) {
        int lineSize1 = lineSize;
        int y1 = y;
        if (y1 + lineSize1 >= image.getHeight())
            return;

        if (lineSize1 < 0) {
            if (y1 + lineSize1 < 0)
                return;

            //Начинаем рисовать линию от   y+lineSize до y
            y1 += lineSize1;
            lineSize1 *= -1;
        }

        for (int yStart = y1; y1 < yStart + lineSize1; y1++)
            for (int x = 0; x < image.getWidth(); x++)
                image.setRGB(x, y1, color.getRGB());

    }

    static void DrawHorizontalLine(@NotNull BufferedImage image, int y, int xStart, int xEnd) {
        DrawHorizontalLine(image, y, xStart, xEnd, LINE_SIZE, Color.GREEN);


    }

    private static void DrawHorizontalLine(@NotNull BufferedImage image, int y, int xStart, int xEnd, int lineSize, @NotNull Color color) {
        int y1 = y;
        for (int yStart = y1; y1 < yStart + lineSize; y1++)
            for (int x = xStart; x <= xEnd; x++)
                image.setRGB(x, y1, color.getRGB());
    }

    static void DrawHorizontalLine(@NotNull BufferedImage image, @NotNull Iterable<Integer[]> list) {
        for (Integer[] temp : list) {
            DrawHorizontalLine(image, temp[0], LINE_SIZE, Color.RED);
            DrawHorizontalLine(image, temp[1], LINE_SIZE, Color.MAGENTA);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Draw Vertical line">
    static void DrawVerticalLine(@NotNull BufferedImage image, int x) {
        DrawVerticalLine(image, x, LINE_SIZE);
    }

    private static void DrawVerticalLine(@NotNull BufferedImage image, int x, int lineSize) {
        DrawVerticalLine(image, x, lineSize, Color.RED);
    }

    private static void DrawVerticalLine(@NotNull BufferedImage image, int x, int lineSize, @NotNull Color color) {
        int lineSize1 = lineSize;
        int x1 = x;
        if (x1 + lineSize1 >= image.getWidth())
            return;

        if (lineSize1 < 0) {
            if (x1 + lineSize1 < 0)
                return;

            //Начинаем рисовать линию от   x+lineSize до x
            x1 += lineSize1;
            lineSize1 *= -1;
        }

        for (int xStart = x1; x1 < xStart + lineSize1; x1++)
            for (int y = 0; y < image.getHeight(); y++)
                image.setRGB(x1, y, color.getRGB());

    }

    static void DrawVerticalLine(@NotNull BufferedImage image, int x, int yStart, int yEnd) {
        DrawVerticalLine(image, x, yStart, yEnd, LINE_SIZE, Color.GREEN);
    }

    private static void DrawVerticalLine(@NotNull BufferedImage image, int x, int yStart, int yEnd, int lineSize, @NotNull Color color) {
        int x1 = x;
        for (int xStart = x1; x1 < xStart + lineSize; x1++)
            for (int y = yStart; y <= yEnd; y++)
                image.setRGB(x1, y, color.getRGB());
    }

    static void DrawVerticalLine(@NotNull BufferedImage image, @NotNull Iterable<Integer[]> list) {
        for (Integer[] temp : list) {
            DrawVerticalLine(image, temp[0], LINE_SIZE, Color.BLUE);
            DrawVerticalLine(image, temp[1], LINE_SIZE, Color.CYAN);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Draw Rectangle">
    private static void DrawRectangle(@NotNull BufferedImage image, int x, int y, int xEnd, int yEnd, int lineSize, @NotNull Color color) {
        DrawHorizontalLine(image, y, x, xEnd, lineSize, color);
        DrawHorizontalLine(image, yEnd, x, xEnd, lineSize, color);
        DrawVerticalLine(image, x, y, yEnd, lineSize, color);
        DrawVerticalLine(image, xEnd, y, yEnd, lineSize, color);
    }

    static void DrawRectangle(@NotNull BufferedImage image, int x, int y, int xEnd, int yEnd) {
        DrawRectangle(image, x, y, xEnd, yEnd, LINE_SIZE, Color.GREEN);
    }
    //</editor-fold>

}