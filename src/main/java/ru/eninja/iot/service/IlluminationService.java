package ru.eninja.iot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.eninja.iot.model.Illumination;
import ru.eninja.iot.repository.IlluminationRepository;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class IlluminationService {

    @Value("${ru.eninja.iot.switching-threshold}")
    private int switchingThreshold;

    @Value("${ru.eninja.iot.sensor-uri}")
    private URI sensorUri;

    @Value("${ru.eninja.iot.diode-turn-on-uri}")
    private URI diodeTurnOnUri;

    @Value("${ru.eninja.iot.diode-turn-off-uri}")
    private URI diodeTurnOffUri;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IlluminationRepository repository;

    @Scheduled(fixedRateString = "${ru.eninja.iot.sensor-rate}")
    public void getIlluminationValue() {
        int illuminationValue = restTemplate.getForObject(sensorUri, Integer.class);
        repository.save(new Illumination(LocalDateTime.now(), illuminationValue));
    }

    @Scheduled(fixedRateString = "${ru.eninja.iot.turning-diode-rate}")
    public void turnDiode() throws IOException {
        restTemplate.postForObject(isDarkNow() ? diodeTurnOnUri : diodeTurnOffUri, null, Void.class);
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
