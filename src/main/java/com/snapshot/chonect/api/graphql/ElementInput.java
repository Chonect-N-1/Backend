package com.snapshot.chonect.api.graphql;

import lombok.Data;

@Data
public class ElementInput {
    private String elementId;
    private String type;
    private String positionX;
    private String positionY;
    private String styles;
    private ElementLayerInput layer;
}
