package exe2.learningapp.logineko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LogiNekoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogiNekoApplication.class, args);
    }

}
