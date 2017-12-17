package ru.eninja.iot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.eninja.iot.model.Illumination;
import ru.eninja.iot.repository.IlluminationRepository;

import java.time.LocalDateTime;

@RestController
@RequestMapping("illumination")
public class IlluminationController {

    @Autowired
    private IlluminationRepository repository;

    @PostMapping
    public void write(Integer illumination) {
        repository.save(new Illumination(LocalDateTime.now(), illumination));
    }
}
