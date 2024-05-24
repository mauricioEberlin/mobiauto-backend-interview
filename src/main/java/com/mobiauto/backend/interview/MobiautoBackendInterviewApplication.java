package com.mobiauto.backend.interview;

import com.mobiauto.backend.interview.jobs.CadastrarEntidadesJob;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(
		title = "mobiauto-backend-interview API",
		version = "1.0.0",
		description = "Projeto que consiste em uma ferramenta de gestão de revendas de veículos da Mobiauto. Por: Mauricio Eberlin"))
@SecurityScheme(
		name = "Authorization",
		scheme = "basic",
		type = SecuritySchemeType.HTTP,
		in = SecuritySchemeIn.HEADER,
		paramName = "Authorization"
)
@SpringBootApplication
@RequiredArgsConstructor
public class MobiautoBackendInterviewApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MobiautoBackendInterviewApplication.class, args);
	}

	private final CadastrarEntidadesJob job;

	@Override
	public void run(String... args) {

		job.inserirRolesNoBanco();
		job.inserirAdminNoBanco();

	}
}