package edu.kalinin.acc.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.experimental.UtilityClass;
import edu.kalinin.acc.dto.MessageDto;
import edu.kalinin.acc.helper.util.ConstructionHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class JsonHelper {
    private static ObjectMapper mapper = createMapper();

    public static String toJson(Object obj) throws JsonProcessingException {
        return objectMapper().writeValueAsString(obj);
    }

    public static String toJsonSafe(Object obj){
        try {
            return objectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static <T> T getObject(MessageDto message, Class<T> valueType) {
        try {
            return objectMapper().readValue(message.getBody(), valueType);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }

    public static ObjectMapper objectMapper() {
        return mapper;
    }

    private static ObjectMapper createMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(ConstructionHelper.let(() -> {
            var javaTimeModule = new JavaTimeModule();
            var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
            return javaTimeModule;
        }));
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
