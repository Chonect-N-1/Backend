package com.snapshot.chonect.api.graphql;

import lombok.Data;

@Data
public class ConnectionInput {
    private String fromElementId;
    private String toElementId;
    private String actionType;
    private Integer orderNum;
    private Integer delay;
    private Boolean isParallel;
}
