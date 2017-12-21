package ru.eninja.iot;

import org.h2.server.web.WebServlet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import ru.eninja.iot.model.Illumination;
import ru.eninja.iot.repository.IlluminationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@EnableScheduling
public class SpringConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    ServletRegistrationBean h2ServletRegistrationBean() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
        registrationBean.addUrlMappings("/h2/*");
        return registrationBean;
    }

    @Bean
    public CommandLineRunner loadData(IlluminationRepository repository) {
        return args -> {
            Random random = new Random();
            List<Illumination> illuminations = new ArrayList<>();
            for (int month = 10; month <= 11; month++) {
                for (int day = 1; day <= 30; day++) {
                    for (int hour = 0; hour <= 23; hour++) {
                        illuminations.add(new Illumination(
                                LocalDateTime.of(2017, month, day, hour, 0), random.nextInt(700)));
                    }
                }
            }
            repository.save(illuminations);
        };
    }
}
