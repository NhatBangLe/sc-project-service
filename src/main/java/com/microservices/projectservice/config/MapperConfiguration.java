package com.microservices.projectservice.config;

import com.microservices.projectservice.mapper.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    @Bean
    public ProjectMapper projectMapper() {
        return new ProjectMapper();
    }

    @Bean
    public FormMapper formMapper() {
        return new FormMapper();
    }

    @Bean
    public SampleMapper sampleMapper() {
        return new SampleMapper();
    }

    @Bean
    public FieldMapper fieldMapper() {
        return new FieldMapper();
    }

    @Bean
    public StageMapper stageMapper() {
        return new StageMapper();
    }

}
