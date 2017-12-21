package ru.eninja.iot.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.eninja.iot.model.Illumination;
import ru.eninja.iot.repository.IlluminationRepository;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class IlluminationService {

    private int switchingThreshold = 500;
    private URI sensorUri = URI.create("http://192.168.43.74");
    private URI diodeTurnOnUri = URI.create("http://192.168.43.74/on");
    private URI diodeTurnOffUri = URI.create("http://192.168.43.74/off");

    @Autowired
    private IlluminationRepository repository;

    @PostConstruct
    public void init() {
        log.info("Initialized");
    }

    @Scheduled(fixedRate = 2000)
    public void getIlluminationValue() throws IOException {
        HttpGet req = new HttpGet(sensorUri);
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(req);

        int illuminationValue = Integer.parseInt(EntityUtils.toString(response.getEntity()));
        repository.save(new Illumination(LocalDateTime.now(), illuminationValue));

        log.info("Got {}", illuminationValue);
    }

    @Scheduled(fixedRate = 2000, initialDelay = 2000)
    public void turnDiode() throws IOException {
        boolean isDarkNow = isDarkNow();
        HttpPost req = new HttpPost(isDarkNow ? diodeTurnOnUri : diodeTurnOffUri);
        CloseableHttpClient client = HttpClients.createDefault();
        client.execute(req);

        log.info("Turning diode {}", isDarkNow ? "ON" : "OFF");
    }

    private boolean isDarkNow() {
        List<Illumination> list = repository.findAll();
        list.sort(Comparator.comparingInt(o -> o.getMeasuringTime().getHour()));

        // get average value for current hour
        int hour = LocalTime.now().getHour();
        List<Integer> values = new ArrayList<>();
        for (Illumination illumination : list) {
            if (hour == illumination.getMeasuringTime().getHour()) {
                values.add(illumination.getValue());
            }
        }

        double avg = 0;
        for (Integer value : values) {
            avg += value;
        }
        avg /= values.size();

        return avg < switchingThreshold;
    }
}
