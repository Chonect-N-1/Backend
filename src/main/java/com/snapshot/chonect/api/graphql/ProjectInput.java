package com.snapshot.chonect.api.graphql;

import lombok.Data;
import java.util.List;

@Data
public class ProjectInput {
    private String projectName;
    private String description;
    private String folderId;
    private List<PageInput> pages;
}
