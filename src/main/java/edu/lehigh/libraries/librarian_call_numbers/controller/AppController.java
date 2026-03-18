package edu.lehigh.libraries.librarian_call_numbers.controller;

import java.util.List;

import jakarta.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.lehigh.libraries.librarian_call_numbers.model.Librarian;
import edu.lehigh.libraries.librarian_call_numbers.service.AppService;
import edu.lehigh.libraries.librarian_call_numbers.service.LibrarianCallNumbersException;
import lombok.extern.slf4j.Slf4j;

@RestController
@Validated
@Slf4j
public class AppController {
    
    private final AppService service;

    AppController(AppService service) {
        this.service = service;
    }

    @GetMapping("/search")
    List<Librarian> search(
        @RequestParam(required=false) @Pattern(regexp = Librarian.SANITIZED_CALL_NUMBER_PATTERN) String callNumber,
        @RequestParam(required=false) String department
        ) throws LibrarianCallNumbersException {

        log.info("Request: GET /search/ " + callNumber + ", " + department);
        if (callNumber != null) {
            return service.findLibrariansForCallNumber(callNumber);
        }
        else {
            return service.findLibrariansForDepartment(department);
        }
    }

    @GetMapping("/all")
    List<Librarian> all()
        throws LibrarianCallNumbersException {
        log.info("Request: GET /all/ ");
        return service.getAllLibrarians();
    }

}
