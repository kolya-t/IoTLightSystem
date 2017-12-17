package ru.eninja.iot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@RequiredArgsConstructor
public class Illumination implements Persistable<Long> {

    private final LocalDateTime measuringTime;
    private final Integer value;
    private @Id Long id;

    @Override
    public boolean isNew() {
        return getId() == null;
    }
}
