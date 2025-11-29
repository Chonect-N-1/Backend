package com.snapshot.chonect.domain.models;

import com.snapshot.chonect.utils.objects.ElementLayer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity(name = "element")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    private String elementId;
    private String type;
    private String positionX;
    private String positionY;

    // El @Lob es tan solo que el objeto va a ser muy muy grande
    @Lob
    @Column(columnDefinition = "TEXT")
    private String styles;

    @Embedded
    private ElementLayer layer;

    // Aliases para compatibilidad con el frontend
    public String getX() {
        return this.positionX;
    }

    public String getY() {
        return this.positionY;
    }
}