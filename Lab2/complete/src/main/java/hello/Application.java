package hello;

import org.mariuszgromada.math.mxparser.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		License.iConfirmNonCommercialUse("");
		SpringApplication.run(Application.class, args);
	}
}