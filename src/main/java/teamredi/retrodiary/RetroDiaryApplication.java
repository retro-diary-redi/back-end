package teamredi.retrodiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RetroDiaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetroDiaryApplication.class, args);
    }

}
