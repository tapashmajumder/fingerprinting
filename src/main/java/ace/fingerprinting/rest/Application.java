package ace.fingerprinting.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    // This is only needed for Command line running.
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
