import org.jetbrains.annotations.NotNull;

import java.awt.*;

class ImageProperties {
    /**
     * Проверка на соответсвие проверяемого цвета черному
     *
     * @param id Цвет для проверки
     * @return 1, если цвет - черный, иначе - 0
     */
    static int IsBlack(int id) {
        return (id == Color.BLACK.getRGB()) ? 1 : 0;
    }

    /**
     * Проверка строки на содержание в ней черных пикселей
     *
     * @param pixels Массив пикселей строки для проверки
     * @param edge   Граница определения наличия черного
     * @return true, если строка содержит черный цвет
     */
    static boolean HasBlackRow(@NotNull int[] pixels, int edge) {
        int sum = 0;
        for (int pixel : pixels)
            sum += IsBlack(pixel);
        return sum >= edge;
    }

    /**
     * Вычисляет сумму цветов пикселя
     *
     * @param id Цвет пикселя
     * @return Сумма цветов
     */
    static int GetColorSum(int id) {
        Color color = new Color(id);
        return color.getRed() + color.getGreen() + color.getBlue();
    }
}