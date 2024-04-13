package edu.kalinin.acc.helper;

import lombok.Value;
import lombok.experimental.UtilityClass;

import java.util.function.Function;

@UtilityClass
public class CollectorHelper {

    public static <T, E1, E2> Function<T, Dimension2<E1, E2>> dimension(Function<T, E1> getter1, Function<T, E2> getter2) {
        return obj -> Dimension2.of(getter1.apply(obj), getter2.apply(obj));
    }

    public static <T, E1, E2, E3> Function<T, Dimension3<E1, E2, E3>> dimension(Function<T, E1> getter1, Function<T, E2> getter2, Function<T, E3> getter3) {
        return obj -> Dimension3.of(getter1.apply(obj), getter2.apply(obj), getter3.apply(obj));
    }

    public static <T, E1, E2, E3, E4> Function<T, Dimension4<E1, E2, E3, E4>> dimension(Function<T, E1> getter1, Function<T, E2> getter2, Function<T, E3> getter3, Function<T, E4> getter4) {
        return obj -> Dimension4.of(getter1.apply(obj), getter2.apply(obj), getter3.apply(obj), getter4.apply(obj));
    }

    @Value(staticConstructor = "of")
    public static class Dimension2<E1, E2> {
        E1 e1;
        E2 e2;
    }

    @Value(staticConstructor = "of")
    public static class Dimension3<E1, E2, E3> {
        E1 e1;
        E2 e2;
        E3 e3;
    }

    @Value(staticConstructor = "of")
    public static class Dimension4<E1, E2, E3, E4> {
        E1 e1;
        E2 e2;
        E3 e3;
        E4 e4;
    }
}
