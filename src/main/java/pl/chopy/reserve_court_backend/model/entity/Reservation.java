package pl.chopy.reserve_court_backend.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class Reservation {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @ToString.Exclude
    private Court court;
    private boolean active = true;
}
