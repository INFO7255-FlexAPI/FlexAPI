package com.example.demo.controller;

import com.example.demo.model.Plan;
import com.example.demo.service.PlanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    private PlanService planService;


    @PostMapping("/plan")
    public ResponseEntity<Plan> createPlan(@RequestBody Plan plan, HttpServletRequest request) throws Exception {
        String bearerToken = request.getHeader("Authorization");
        bearerToken = bearerToken.substring(7);

        // Assuming that the PlanService's savePlan method has been updated to accept the token
        Plan savedPlan = planService.savePlan(plan, bearerToken);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setETag(savedPlan.getEtag());
        return ResponseEntity.ok().headers(responseHeaders).body(savedPlan);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Plan> getPlan(@PathVariable String id,
                                        @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
        Optional<Plan> plan = planService.getPlan(id);

        if (plan.isPresent()) {
            String currentEtag = plan.get().getEtag();

            // Check if ETag matches
            if (ifNoneMatch != null && currentEtag.equals(ifNoneMatch)) {
                // Client's version is up to date
                // Return 304 Not Modified
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                        .eTag(currentEtag)
                        .build();
            } else {
                // Client's version is not up to date
                // Return the plan with its ETag
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
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

