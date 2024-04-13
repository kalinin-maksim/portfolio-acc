package edu.kalinin.acc.helper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;
import static edu.kalinin.acc.helper.CollectorHelper.dimension;

class CollectorHelperTest {

    @Test
    void dimensionTest() {
        var source = "";

        then(dimension(Object::toString, s -> source).apply(source)).isNotNull();
        then(dimension(Object::toString, s -> source, s -> source).apply(source)).isNotNull();
    }
}