package edu.kalinin.acc.configuration;

import graphql.schema.*;
import lombok.experimental.UtilityClass;
import edu.kalinin.acc.dto.DownloadedFile;

@UtilityClass
public class GraphQlScalarRegistry {

    public static final GraphQLScalarType downloadedFile = new GraphQLScalarType.Builder()
            .name("DownloadedFile")
            .coercing(new Coercing<Void, DownloadedFile>() {

                @Override
                public DownloadedFile serialize(Object input) {
                    if (input instanceof DownloadedFile) {
                        return (DownloadedFile) input;
                    } else {
                        throw new CoercingSerializeException("Input is not download type object");
                    }
                }

                @Override
                public Void parseValue(Object input) {
                    throw new CoercingParseValueException(
                                "Expected type " + DownloadedFile.class.getName() + " but was " + input.getClass().getName());
                }

                @Override
                public Void parseLiteral(Object input) {
                    throw new CoercingParseLiteralException(
                            "Must use variables to specify Download values");
                }
            }).build();
}