package service.visitas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import service.visitas.config.VisitasProperties;

@SpringBootApplication
@EnableConfigurationProperties(VisitasProperties.class)
public class VisitasApplication {

    public static void main(String[] args) {
        SpringApplication.run(VisitasApplication.class, args);
    }

}
