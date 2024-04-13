package edu.kalinin.acc.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.JsonValidator;
import com.networknt.schema.SpecVersion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventValidator {

    private final String schemaPath;

    public String validate(String json, ObjectMapper objectMapper) throws JsonValidateException {
        var factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        var jsonSchema = factory.getSchema(
                JsonValidator.class.getResourceAsStream(schemaPath));
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new JsonValidateException(e);
        }
        var errors = jsonSchema.validate(jsonNode);
        if (!errors.isEmpty()) {
            throw new JsonValidateException(errors);
        }
        return json;
    }
}
