package pl.chopy.reserve_court_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Data
@ConfigurationProperties(prefix = "reserve-court.security")
public class ApplicationProps {
	private List<String> allowedOrigins;
	private String defaultRole;
	private String backendDomain;
	private String socketDomain;
	private Map<String, Set<String>> privileges = new LinkedHashMap<>();
}