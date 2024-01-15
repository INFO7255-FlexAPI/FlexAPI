package com.example.demo.controller;

import com.example.demo.model.Plan;
import com.example.demo.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    @PostMapping
    public ResponseEntity<Plan> createPlan(@RequestBody Plan plan) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.savePlan(plan));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> getPlan(@PathVariable String id) {
        return planService.getPlan(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable String id) {
        if(planService.getPlan(id).isPresent()) {
            planService.deletePlan(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

}

