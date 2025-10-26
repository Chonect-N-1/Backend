package com.snapshot.chonect.api.graphql;

import lombok.Data;
import java.util.List;

@Data
public class CanvasInput {
    private List<ElementInput> elements;
    private List<ConnectionInput> connections;
}
