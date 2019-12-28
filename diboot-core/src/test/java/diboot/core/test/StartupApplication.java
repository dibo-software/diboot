package diboot.core.test;

import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Administrator
 */
@SpringBootApplication
public class StartupApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(StartupApplication.class, args);
	}

}