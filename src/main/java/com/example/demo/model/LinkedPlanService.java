package com.example.demo.model;

import lombok.Data;

@Data
public class LinkedPlanService {
    private LinkedService linkedService;
    private PlanServiceCostShares planserviceCostShares;
    private String _org;
    private String objectId;
    private String objectType;
}

