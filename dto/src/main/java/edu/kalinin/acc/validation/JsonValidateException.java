package edu.kalinin.acc.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.networknt.schema.ValidationMessage;

import java.util.Set;
import java.util.stream.Collectors;

public class JsonValidateException extends Exception {
    public JsonValidateException(JsonProcessingException e) {
        super(e);
    }
    public JsonValidateException(Set<ValidationMessage> errors) {
        super("validation errors:\n" +
                errors.stream().map(ValidationMessage::getMessage).collect(Collectors.joining("\n")));
    }
}
