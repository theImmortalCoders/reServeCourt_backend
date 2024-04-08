package pl.chopy.reserve_court_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Image {
    @Id
    @GeneratedValue
    private Long id;
    private String path;
    private boolean hasThumbnail;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "author_id")
    private User author;
}
