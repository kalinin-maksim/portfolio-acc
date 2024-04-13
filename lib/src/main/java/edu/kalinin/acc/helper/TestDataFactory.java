package edu.kalinin.acc.helper;

import lombok.experimental.UtilityClass;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import edu.kalinin.acc.dto.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.*;

@UtilityClass
public class TestDataFactory {

    DateTimeFormatter TIME = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();
    DateTimeFormatter DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(TIME)
            .toFormatter();

    @UtilityClass
    public static class EventFactory {

        public static final String ACCOUNTING_ID = "1";
        public static final String TEST_SYSTEM_ID = "TestSystemId";

        public static Event getEvent() {
            var message = Instancio.of(Event.class)
                    .withSettings(Settings.create()
                            .set(Keys.SEED, 1L)
                            .set(Keys.MAX_DEPTH, 2))
                    .generate(Select.fields(field -> field.getName().contains("Date")
                            || field.getName().contains("TimeStamp")), gen -> gen.oneOf(getDateTimeAsString()))
                    .create();
            message.getHeader().setAccountingId(ACCOUNTING_ID);
            message.getHeader().setSystemId(TEST_SYSTEM_ID);
            message.getHeader().setReverse(false);
            message.getHeader().setMultiOffset(false);

            return message;
        }

        private static String getDateTimeAsString() {
            return Instancio.create(LocalDateTime.class).format(DATE_TIME).concat(".711971+03:00");
        }
    }
}
