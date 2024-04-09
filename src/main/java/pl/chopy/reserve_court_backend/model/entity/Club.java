package pl.chopy.reserve_court_backend.model.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import pl.chopy.reserve_court_backend.model.Location;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Club {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;
    @ManyToOne
    private Image logo;
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Location location;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "club")
    private List<Court> courts = new ArrayList<>();
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "owner_id")
    private User owner;
    private double rating = 0.0;
}
