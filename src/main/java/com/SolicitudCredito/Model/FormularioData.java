package com.SolicitudCredito.Model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;


@Setter @Getter @ToString
public class FormularioData {
    private String nombre;
    private String cedula;
    private int edad;
    private String esEmpleado = "false";
    private String esIndependiente = "false";
    private String esPensionado = "false";
    private String rut;
    private int puntaje;
    private String certificadoLaboral;
    private String declaracionImpuestos;
    private String comprobantePagoPension;


    public Map<String, Object> toMapForm() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("nombreSolicitante", Map.of("value", nombre, "type", "String"));
            map.put("cedulaSolicitante", Map.of("value", cedula, "type", "String"));
            map.put("edadSolicitante", Map.of("value", edad, "type", "Long"));

            return map;
        }
        catch (Exception e){
            System.out.println("Error al convertir a Map: " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> toMapDocuments() {
        var esEmpleado = this.esEmpleado != null && this.esEmpleado.equals("true");
        var esIndependiente = this.esIndependiente != null && this.esIndependiente.equals("true");
        var esPensionado = this.esPensionado != null && this.esPensionado.equals("true");
        try {

            return getStringObjectMap(esIndependiente, esEmpleado, esPensionado);
        }
        catch (Exception e){
            System.out.println("Error al convertir a Map: " + e.getMessage());
            return null;
        }
    }

    private Map<String, Object> getStringObjectMap(boolean esIndependiente, boolean esEmpleado, boolean esPensionado) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("esIndependiente", esIndependiente);
        variables.put("esEmpleado", esEmpleado);
        variables.put("esPensionado", esPensionado);
        variables.put("RUT", this.rut);
        variables.put("historialCrediticio", this.puntaje);
        variables.put("declaracionImpuestos", this.declaracionImpuestos);
        variables.put("comprobantePagoPension", this.comprobantePagoPension);
        variables.put("certificadoLaboral", this.certificadoLaboral);
        return variables;
    }
}