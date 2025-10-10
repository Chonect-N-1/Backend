package com.snapshot.chonect.utils.requirements;


import lombok.Data;
import java.util.List;
@Data
public class ProjectInput {
    private String projectName;
    private List<PageInput> pages;
}

@Data
class PageInput {
    private String pageName;
    private PageConfigInput config;
    private CanvasInput canvas;
}

@Data
class PageConfigInput {
    private String grid;
    private String backgroundColor;
}

@Data
class CanvasInput {
    private List<ElementInput> elements;
    private List<ConnectionInput> connections;
}

@Data
class ElementInput {
    private String elementId;
    private String type;
    private String positionX;
    private String positionY;
    private String styles;
    private ElementLayerInput layer;
}

@Data
class ElementLayerInput {
    private Boolean locked;
    private Integer zIndex;
    private Boolean visible;
}

@Data
class ConnectionInput {
    private String fromElementId;
    private String toElementId;
    private String actionType;
    private Integer orderNum;
    private Integer delay;
    private Boolean parallel;
}