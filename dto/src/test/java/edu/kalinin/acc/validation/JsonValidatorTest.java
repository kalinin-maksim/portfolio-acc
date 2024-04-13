package edu.kalinin.acc.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import edu.kalinin.acc.dto.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.*;
import static org.assertj.core.api.BDDAssertions.then;
import static org.instancio.Select.fields;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonValidatorTest {
    public static final String ACCOUNTING_ID = "1";
    public static final String SCHEMA_EVENT_JSON = "/schema/Event.json";
    private final DateTimeFormatter TIME = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();
    private final DateTimeFormatter DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(TIME)
            .toFormatter();

    @Test
    void validateTest() throws JsonValidateException, JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(getEvent());
        var eventValidator = new EventValidator(SCHEMA_EVENT_JSON);
        var validate = eventValidator.validate(json, objectMapper);

        then(validate).isNotEmpty();
    }

    @Test
    void validateFailedCauseOfNoiseTest() {
        assertThrows(JsonValidateException.class, () -> {
            var objectMapper = new ObjectMapper();
            var json = "noise" + objectMapper.writeValueAsString(getEvent());
            var eventValidator = new EventValidator(SCHEMA_EVENT_JSON);
            eventValidator.validate(json, objectMapper);
        });
    }

    @Test
    void validateFailedCauseOfInvalidDateTest() {
        assertThrows(JsonValidateException.class, () -> {
            var objectMapper = new ObjectMapper();
            var event = getEvent();
            event.getHeader().setOperationTimeStamp("1-2-3");
            var json = objectMapper.writeValueAsString(event);
            var eventValidator = new EventValidator(SCHEMA_EVENT_JSON);
            eventValidator.validate(json, objectMapper);
        });
    }

    private Event getEvent() {
        var message = Instancio.of(Event.class)
                .withSettings(Settings.create()
                        .set(Keys.SEED, 1L)
                        .set(Keys.MAX_DEPTH, 2))
                .generate(fields(field -> field.getName().contains("Date")
                        || field.getName().contains("TimeStamp")), gen -> gen.oneOf(getDateTimeAsString().concat(".711971+03:00")))
                .create();
        message.getHeader().setAccountingId(ACCOUNTING_ID);
        message.getHeader().setModuleId("module");
        message.getHeader().setReverse(false);
        message.getHeader().setMultiOffset(false);
        return message;
    }

    private String getDateTimeAsString() {
        return Instancio.create(LocalDateTime.class).format(DATE_TIME).concat(".711971+03:00");
    }
}