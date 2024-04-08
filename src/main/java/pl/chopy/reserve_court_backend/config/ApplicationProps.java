package pl.chopy.reserve_court_backend.config;

import io.vavr.control.Option;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.chopy.reserve_court_backend.model.UserRole;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Data
@ConfigurationProperties(prefix = "reserve-court.security")
public class ApplicationProps {
    private List<String> allowedOrigins;
    private String defaultRole;
    private Map<String, Set<String>> privileges = new LinkedHashMap<>();

    public Set<SimpleGrantedAuthority> getPrivileges(UserRole role) {
        return Option
                .of(privileges.get(role.toString()))
                .map(Collection::stream)
                .map(stream -> stream.map(SimpleGrantedAuthority::new).collect(Collectors.toSet()))
                .getOrElse(Collections::emptySet);
    }
}