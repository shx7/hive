package util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.function.Predicate;

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

    @Nullable
    public static <T> T findNot(@NotNull T[] array, @NotNull Predicate<T> predicate) {
        return find(array, predicate.negate());
    }

    @Nullable
    private static <T> T find(@NotNull T[] array, @NotNull Predicate<T> predicate) {
        for (T x : array) {
            if (predicate.test(x)) {
                return x;
            }
        }
        return null;
    }
}
