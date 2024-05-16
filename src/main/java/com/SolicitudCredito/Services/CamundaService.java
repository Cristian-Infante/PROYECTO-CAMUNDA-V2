package com.SolicitudCredito.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CamundaService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Getter
    private String processId;

    @Value("${camunda.rest.url}")
    private String camundaRestUrl;

    public CamundaService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String startProcess(String processDefinitionKey, Map<String, Object> variables) {
        String url = buildUrl("/process-definition/key/", processDefinitionKey, "/start");
        Map<String, Object> request = Map.of("variables", variables);  // Usando Map.of() para simplificar la creación del mapa.
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            processId = (String) responseMap.get("id");
            System.out.println("Proceso iniciado con ID: " + processId);  // Imprimir el ID del proceso iniciado
            return response.getBody();  // Retornar el cuerpo de la respuesta completa
        } catch (IOException e) {
            System.out.println("Error iniciando el proceso: " + e.getMessage());
            return null;  // Retornar null en caso de error para indicar que el proceso no pudo ser iniciado
        }
    }

    public boolean getProcessVariable(String processId, String variableName) {
        String url = buildUrl("/process-instance/", processId, "/variables/", variableName);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            return (boolean) responseMap.get("value");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getTaskId(String processInstanceId) {
        String url = buildUrl("/task?processInstanceId=", processInstanceId);
        System.out.println("url = " + url);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            List<Map<String, Object>> tasks = objectMapper.readValue(response.getBody(), new TypeReference<List<Map<String, Object>>>() {});
            if (!tasks.isEmpty()) {
                return (String) tasks.get(0).get("id"); // Retorna el ID de la primera tarea.
            }
        } catch (Exception e) {
            System.out.println("Error al recuperar el ID de la tarea: " + e.getMessage());
        }
        return null;
    }

    public void completeTask(String taskId) {
        try {
            completeTask(taskId, Map.of());  // Llama al método sobrecargado con un mapa vacío si no se proporcionan variables.
        }
        catch (Exception e) {
            System.out.println("Error al completar la tarea.: " + e.getMessage());
        }
    }

    public void completeTask(String taskId, Map<String, Object> variables) {
        String url = buildUrl("/task/", taskId, "/complete");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear un nuevo mapa para formatear las variables correctamente
        Map<String, Object> formattedVariables = new HashMap<>();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            Object value = entry.getValue();
            String type = determineType(value);

            // Construir el valor de la variable según el tipo
            Map<String, Object> valueMap = new HashMap<>();
            valueMap.put("value", value);
            valueMap.put("type", type);

            formattedVariables.put(entry.getKey(), valueMap);
        }

        // Envolver las variables formateadas en un mapa con la clave "variables"
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("variables", formattedVariables);
        System.out.println("Formatted request = " + requestBody);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForEntity(url, entity, String.class);
            System.out.println("Task " + taskId + " completed successfully.");
        } catch (Exception e) {
            System.out.println("Error al completar la tarea " + taskId + ": " + e.getMessage());
        }
    }

    private String determineType(Object value) {
        if (value instanceof Boolean) return "Boolean";
        if (value instanceof Integer) return "Integer";
        if (value instanceof String) return "String";
        return "Object";  // Default type
    }

    private String buildUrl(String... paths) {
        return camundaRestUrl + String.join("", paths);
    }
}
