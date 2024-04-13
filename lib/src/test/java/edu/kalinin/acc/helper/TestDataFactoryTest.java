package edu.kalinin.acc.helper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class TestDataFactoryTest {

    @Test
    void getEventTest() {

        var event = TestDataFactory.EventFactory.getEvent();
        then(event).isNotNull();
    }
}