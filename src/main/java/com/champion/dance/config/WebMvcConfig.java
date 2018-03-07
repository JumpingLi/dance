package com.champion.dance.config;

import com.champion.dance.utils.page.PaginationList;
import com.champion.dance.utils.page.PaginationListJsonSerializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Slf4j
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.forEach(converter -> {
            if (converter.getClass().equals(MappingJackson2HttpMessageConverter.class)) {
                final ObjectMapper om = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
                om.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
                final SimpleModule module = new SimpleModule("PaginationListJSONModule",
                        new Version(1, 0, 0, null, null, null));
                module.addSerializer(PaginationList.class, new PaginationListJsonSerializer(om));
                om.registerModule(module);
            }
        });
    }
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
