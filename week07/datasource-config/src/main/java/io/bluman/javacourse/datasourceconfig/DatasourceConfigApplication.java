package io.bluman.javacourse.datasourceconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class DatasourceConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatasourceConfigApplication.class, args);
	}

}
