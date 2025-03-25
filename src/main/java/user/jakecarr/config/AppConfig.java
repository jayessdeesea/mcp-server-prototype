package user.jakecarr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for application-wide dependencies.
 */
@Configuration
public class AppConfig {
    
    /**
     * Provides an ObjectMapper instance configured with the JavaTimeModule.
     *
     * @return The configured ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
