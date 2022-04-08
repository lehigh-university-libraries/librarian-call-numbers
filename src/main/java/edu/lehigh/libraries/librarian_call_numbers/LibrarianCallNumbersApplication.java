package edu.lehigh.libraries.librarian_call_numbers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class LibrarianCallNumbersApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(LibrarianCallNumbersApplication.class);
	}
	
	public static void main(String[] args) {
		log.info("Starting the Librarian Call Numbers application");
		SpringApplication.run(LibrarianCallNumbersApplication.class, args);
		log.info("Started the Librarian Call Numbers application");
	}

}
