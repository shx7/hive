package util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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

    @NotNull
    public static <T> List<T> filterNot(@NotNull T[] array, @NotNull Predicate<T> predicate) {
        return filter(array, predicate.negate());
    }

    @NotNull
    private static <T> List<T> filter(@NotNull T[] array, @NotNull Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T x : array) {
            if (predicate.test(x)) {
                result.add(x);
            }
        }
        return result;
    }

    @SafeVarargs
    @NotNull
    public static <T> Set<T> setOf(@NotNull T ... args) {
        return new HashSet<>(Arrays.asList(args));
    }
}
