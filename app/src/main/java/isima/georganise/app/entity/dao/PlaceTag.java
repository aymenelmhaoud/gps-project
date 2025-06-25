package isima.georganise.app.entity.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "PLACESTAGS")
public class PlaceTag implements Serializable {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false, unique = true)
    private Long placeTagId;

    @ManyToOne
    @JoinColumn(name = "PLACEID", updatable = false, nullable = false)
    @JsonBackReference
    private Place place;

    @ManyToOne
    @JoinColumn(name = "TAGID", updatable = false, nullable = false)
    @JsonBackReference
    private Tag tag;

    public PlaceTag(Place place, Tag tag) {
        this.place = place;
        this.tag = tag;
    }
}
