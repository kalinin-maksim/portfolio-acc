package edu.kalinin.acc.helper;

import java.util.function.Consumer;

public class Observer<T> {
    private final Consumer<T> successConsumer;
    private final Consumer<Exception> failureConsumer;

    public Observer(Consumer<T> successConsumer, Consumer<Exception> failureConsumer) {
        this.successConsumer = successConsumer;
        this.failureConsumer = failureConsumer;
    }

    public void run(UnSafeSupplier<T> action) {
        try {
            successConsumer.accept(action.get());
        } catch (Exception ex) {
            failureConsumer.accept(ex);
        }
    }

    public <E> Consumer<E> on(UnSafeFunction<E, T> action){
        return (E messageDto) -> run(() -> action.apply(messageDto));
    }

    public Observer<T> combine(Observer<T> observer) {
        return new Observer<>(successConsumer.andThen(observer.successConsumer), failureConsumer.andThen(observer.failureConsumer));
    }

    @FunctionalInterface
    public interface UnSafeSupplier<T> {

        T get() throws Exception;
    }

    @FunctionalInterface
    public interface UnSafeFunction<T, R> {

        R apply(T t) throws Exception;
    }
}
