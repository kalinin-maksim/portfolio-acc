package edu.kalinin.acc.configuration;

import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphqlConfiguration {

    @Bean
    public GraphQLScalarType downloadedFile() {
        return GraphQlScalarRegistry.downloadedFile;
    }
}
