package edu.lehigh.libraries.librarian_call_numbers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class LibrarianCallNumbersApplication {

	public static void main(String[] args) {
		log.info("Starting the Librarian Call Numbers application");
		SpringApplication.run(LibrarianCallNumbersApplication.class, args);
		log.info("Started the Librarian Call Numbers application");
	}

}
