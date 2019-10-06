package util;

public class ContainerUtil {
    public static <T> T getCircular(T[] array, int index) {
        int length = array.length;
        if (index < 0) {
            index += length;
        }
        if (index >= length) {
            index -= length;
        }
        return array[index];
    }
}
