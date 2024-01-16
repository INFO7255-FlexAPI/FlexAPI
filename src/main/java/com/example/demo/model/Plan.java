package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import lombok.Data;
import java.util.List;

@Data
@RedisHash("Plan")
public class Plan {
    private PlanCostShares planCostShares;
    private List<LinkedPlanService> linkedPlanServices;
    private String _org;
    private String etag;
    @Id
    private String objectId;  // Note: Ensure this is the unique identifier
    private String objectType;
    private String planType;
    private String creationDate;

    public void setEtag(String etag) {
        this.etag = etag;
    }
    public String getEtag(String etag) {
        return this.etag;
    }

}

