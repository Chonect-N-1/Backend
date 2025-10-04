package com.snapshot.chonect.utils.objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// las cosas que no sepa las voy a comentar
// el Embeddable crea objetos que se pueden reutilizar en otras entidades
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElementLayer {
    private Boolean locked;
    private Integer zIndex;
    private Boolean visible;
}