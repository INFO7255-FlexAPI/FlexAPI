package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.demo.model.Plan;
import com.example.demo.model.PlanRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;


import java.io.IOException;
import java.io.InputStream;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;




    public Plan savePlan(Plan plan, String bearerToken ) throws Exception {
        if (!validateJson(plan)) {
            throw new Exception("Plan data is not valid according to the schema.");
        }
        if(bearerToken==null || !verify(bearerToken))
        {
            throw new Exception("Token not present");
        }
        System.out.println(bearerToken);
        ObjectMapper objectMapper = new ObjectMapper();
        String planJson = objectMapper.writeValueAsString(plan);
        String etag = "\"" + DigestUtils.md5Hex(planJson) + "\"";  // ETag with double quotes
        return planRepository.save(plan);
    }

    protected ResponseEntity<String> getCall(String url) throws RestClientException {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(url, String.class);
    }

    public boolean verify(String token) {
        try {
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + token;
            System.out.println(url);
            ResponseEntity<String> response = getCall(url);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (RestClientException e) {
            System.out.println("Error while verifying token: " + e);
            return false;
        }
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
