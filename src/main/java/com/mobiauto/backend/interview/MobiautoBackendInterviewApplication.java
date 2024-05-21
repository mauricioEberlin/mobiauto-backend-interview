package com.mobiauto.backend.interview;

import com.mobiauto.backend.interview.jobs.CadastrarEntidadesJob;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


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