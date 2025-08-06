package JavaSpring_API.JavaSpring_API;

// src/main/java/com/example/api/ApiApplication.java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

@SpringBootApplication
@RestController
public class JavaSpringApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(JavaSpringApiApplication.class, args);
	}

	@GetMapping("/health")
	public Map<String, String> health() {
		Map<String, String> response = new HashMap<>();
		response.put("status", "UP");
		response.put("timestamp", new Date().toString());
		return response;
	}

	@GetMapping("/api/users")
	public List<Map<String, Object>> getUsers() {
		// Sample data
		return Arrays.asList(
				Map.of("id", 1, "name", "John Doe", "email", "john@example.com"),
				Map.of("id", 2, "name", "Jane Smith", "email", "jane@example.com")
		);
	}
}