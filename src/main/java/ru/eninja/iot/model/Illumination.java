package ru.eninja.iot.model;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Entity
@ToString
public class Illumination implements Persistable<Long> {

    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime measuringTime;
    private Integer value;

    public Illumination() {
    }

    public Illumination(LocalDateTime measuringTime, Integer value) {
        this.measuringTime = measuringTime;
        this.value = value;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }
}
