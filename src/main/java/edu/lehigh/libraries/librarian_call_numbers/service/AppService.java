package edu.lehigh.libraries.librarian_call_numbers.service;

import java.util.List;

import edu.lehigh.libraries.librarian_call_numbers.model.Librarian;

public interface AppService {
    
    public List<Librarian> findLibrariansForCallNumber(String callNumber) throws LibrarianCallNumbersException;

}
