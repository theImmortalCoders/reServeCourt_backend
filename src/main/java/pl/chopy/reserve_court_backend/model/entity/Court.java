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
public class Court {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "club_id")
    private Club club;
    @ManyToMany(cascade = CascadeType.REMOVE)
    private List<Reservation> reservations = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private CourtType type;
    @Enumerated(EnumType.STRING)
    private Surface surface;
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Location location;
    private boolean closed = false;
    @ManyToMany
    private List<Image> images = new ArrayList<>();

    public enum CourtType {
        INDOOR, OUTDOOR
    }

    public enum Surface {
        CLAY, CONCRETE, GRASS, ACRYLIC
    }
}
