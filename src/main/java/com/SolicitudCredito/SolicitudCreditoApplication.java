package com.SolicitudCredito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;

@SpringBootApplication
@EnableProcessApplication
public class SolicitudCreditoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolicitudCreditoApplication.class, args);
	}

}
