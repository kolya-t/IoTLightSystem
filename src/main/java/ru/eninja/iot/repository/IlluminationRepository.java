package ru.eninja.iot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.eninja.iot.model.Illumination;

import java.util.List;

public interface IlluminationRepository extends CrudRepository<Illumination, Long> {

    @Override
    List<Illumination> findAll();
}
