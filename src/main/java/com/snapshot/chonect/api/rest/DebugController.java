package com.snapshot.chonect.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/debug")
public class DebugController {

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/beans")
    public Map<String, String> listBeans() {
        return Arrays.stream(applicationContext.getBeanDefinitionNames())
                .filter(name -> name.toLowerCase().contains("graphql"))
                .collect(Collectors.toMap(name -> name, name -> applicationContext.getBean(name).getClass().getName()));
    }

    @GetMapping("/graphql-status")
    public String graphqlStatus() {
        boolean hasGraphqlController = Arrays.stream(applicationContext.getBeanDefinitionNames())
                .anyMatch(name -> name.contains("graphqlController") || name.contains("GraphqlController"));

        boolean hasGraphqlAutoConfig = Arrays.stream(applicationContext.getBeanDefinitionNames())
                .anyMatch(name -> name.toLowerCase().contains("graphqlautoconfig"));

        return "GraphQL Controller exists: " + hasGraphqlController + "\n" +
                "GraphQL AutoConfig exists: " + hasGraphqlAutoConfig;
    }
}
