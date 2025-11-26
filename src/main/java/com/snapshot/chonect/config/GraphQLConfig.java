package com.snapshot.chonect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> {
            // Configuration placeholder
        };
    }

    @Bean
    public GraphQlSourceBuilderCustomizer sourceBuilderCustomizer() {
        return builder -> {
            builder.configureRuntimeWiring(runtimeWiringConfigurer());
        };
    }
}
