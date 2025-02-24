package edu.lehigh.libraries.librarian_call_numbers.controller;

import java.util.List;

import javax.validation.constraints.Pattern;

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
    List<Librarian> search(@RequestParam @Pattern(regexp = Librarian.SANITIZED_CALL_NUMBER_PATTERN) String callNumber)
        throws LibrarianCallNumbersException {
        log.info("Request: GET /search/ " + callNumber);
        return service.findLibrariansForCallNumber(callNumber);
    }

    @GetMapping("/all")
    List<Librarian> all()
        throws LibrarianCallNumbersException {
        log.info("Request: GET /all/ ");
        return service.getAllLibrarians();
    }

}
