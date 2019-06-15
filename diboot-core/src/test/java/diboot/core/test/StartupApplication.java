package diboot.core.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author Administrator
 */
@SpringBootApplication
public class StartupApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(StartupApplication.class, args);
	}

}