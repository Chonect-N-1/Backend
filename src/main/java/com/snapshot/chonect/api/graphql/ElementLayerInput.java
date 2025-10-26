package com.snapshot.chonect.api.graphql;

import lombok.Data;

@Data
public class ElementLayerInput {
    private Boolean locked;
    private Integer zIndex;
    private Boolean visible;
}
