package com.SolicitudCredito.Controller;

import com.SolicitudCredito.Model.FormularioData;
import com.SolicitudCredito.Services.CamundaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class FormularioController {

    private final CamundaService camundaService;

    public FormularioController(CamundaService camundaService) {
        this.camundaService = camundaService;
    }

    @GetMapping("/")
    public String mostrarFormulario() {
        return "formulario";
    }

    @PostMapping("/submit")
    public String procesarFormulario(@ModelAttribute FormularioData formularioData) {
        System.out.println("Processing form data: " + formularioData);
        String processDefinitionKey = "Process_0sw3d05";
        try {
            Map<String, Object> variables = formularioData.toMapForm();
            camundaService.startProcess(processDefinitionKey,variables);
            return "documentoSolicitud";  // Mostrar el siguiente paso.
        } catch (Exception e) {
            System.out.println("Error starting Camunda process: " + e.getMessage());
            return "formulario";  // Asumiendo que existe una vista 'error'.
        }
    }

    @PostMapping("/submitDocuments")
    public String procesarDocumentos(@ModelAttribute FormularioData formularioData) {
        System.out.println("Processing documents data: " + formularioData);
        try {
            Map<String, Object> variables = formularioData.toMapDocuments();
            var actualProcess = camundaService.getTaskId(camundaService.getProcessId());
            System.out.println("actualProcess = " + actualProcess);
            camundaService.completeTask(actualProcess, variables);
            System.out.println(camundaService.getProcessVariable(actualProcess, "esEmpleado"));
            return "documentoSolicitud";  // Mostrar el siguiente paso.
        } catch (Exception e) {
            System.out.println("Error starting Camunda process: " + e.getMessage());
            return "formulario";  // Asumiendo que existe una vista 'error'.
        }
    }
}