package pl.chopy.reserve_court_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "reserve-court.security")
public class ApplicationProps {
    private List<String> allowedOrigins;
}