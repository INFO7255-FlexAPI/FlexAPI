package com.example.demo.service;

import com.example.demo.model.Plan;
import com.example.demo.model.PlanRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;


import java.io.IOException;
import java.io.InputStream;

import java.util.Optional;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public Plan savePlan(Plan plan) throws Exception {
        if (!validateJson(plan)) {
            throw new Exception("Plan data is not valid according to the schema.");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String planJson = objectMapper.writeValueAsString(plan);
        String etag = "\"" + DigestUtils.md5Hex(planJson) + "\"";  // ETag with double quotes
        plan.setEtag(etag);
        return planRepository.save(plan);
    }
    public boolean validateJson(Plan plan) {
        try (InputStream inputStream = getClass().getResourceAsStream("/schemaValidator.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            ObjectMapper objectMapper = new ObjectMapper();
            String planJson = objectMapper.writeValueAsString(plan);
            JSONObject planJsonObject = new JSONObject(planJson);

            schema.validate(planJsonObject);
            return true;
        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
            e.getCausingExceptions().stream()
                    .map(ValidationException::getMessage)
                    .forEach(System.err::println);
            return false;
        } catch (Exception e) {
            System.err.println("Error during validation: " + e.getMessage());
            return false;
        }
    }

    public Optional<Plan> getPlan(String id) {
        return planRepository.findById(id);
    }

    public void deletePlan(String id) {
        planRepository.deleteById(id);
    }
}
