package edu.kalinin.acc.helper;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@UtilityClass
public class ProcessHelper {

    public static <T> void failSaveProcess(Collection<T> collection, Consumer<T> consumer, BiConsumer<T, Exception> exceptionConsumer) {
        for (T element : collection) {
            try {
                consumer.accept(element);
            } catch (Exception ex) {
                exceptionConsumer.accept(element, ex);
            }
        }
    }
}
