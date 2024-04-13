package edu.kalinin.acc.helper.util;

import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class ConstructionHelper {

    public static <T> T let(Supplier<T> supplier){
        return supplier.get();
    }

}
