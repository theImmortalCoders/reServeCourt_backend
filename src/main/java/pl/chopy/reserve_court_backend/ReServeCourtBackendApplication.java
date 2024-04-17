package pl.chopy.reserve_court_backend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@OpenAPIDefinition(info = @Info(title = "reServeCourt backend app documentation", description = "Author: Marcin Bator"))
public class ReServeCourtBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReServeCourtBackendApplication.class, args);
	}
}
