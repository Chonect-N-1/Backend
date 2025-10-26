package com.snapshot.chonect.api.graphql;

import lombok.Data;
import java.util.List;

@Data
public class ProjectInput {
    private String projectName;
    private List<PageInput> pages;
}
