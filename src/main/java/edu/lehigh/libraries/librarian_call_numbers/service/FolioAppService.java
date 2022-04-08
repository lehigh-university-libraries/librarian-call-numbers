package edu.lehigh.libraries.librarian_call_numbers.service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import edu.lehigh.libraries.librarian_call_numbers.config.PropertiesConfig;
import edu.lehigh.libraries.librarian_call_numbers.connection.FolioConnection;
import edu.lehigh.libraries.librarian_call_numbers.model.Librarian;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FolioAppService implements AppService {

    private static final String BL_USERS_PATH = "/bl-users";

    private final PropertiesConfig config;
    private final FolioConnection folioConnection;

    FolioAppService(PropertiesConfig config) throws Exception {
        this.config = config;
        this.folioConnection = new FolioConnection(config);
    }

    @Override
    public List<Librarian> findLibrariansForCallNumber(String callNumber) throws LibrarianCallNumbersException {
        if (callNumber == null) {
            throw new LibrarianCallNumbersException("Cannot find librarians, no call number");
        }

        log.debug("Looking for librarian matches with " + callNumber);

        List<Librarian> librarians = getAllLibrarians();
        List<Librarian> librarianMatches = new LinkedList<Librarian>();
        for (Iterator<Librarian> librarianIterator = librarians.iterator(); librarianIterator.hasNext(); ) {
            Librarian librarian = librarianIterator.next();

            for (Iterator<String> callNumberPrefixIterator = librarian.getCallNumberPrefixes().iterator(); 
                callNumberPrefixIterator.hasNext(); ) {
                
                String callNumberPrefix = callNumberPrefixIterator.next();
                if (callNumber.startsWith(callNumberPrefix)) {
                    librarianMatches.add(librarian);
                    break;
                }
            }
        }
        return librarianMatches;
    }

    private List<Librarian> getAllLibrarians() throws LibrarianCallNumbersException {
        String url = config.getFolio().getOkapiBaseUrl() + BL_USERS_PATH;
        String queryString = "customFields.callNumbers=\"\"";
        JSONObject responseObject;
        try {
            responseObject = folioConnection.executeGet(url, queryString);
        }
        catch (Exception e) {
            log.error("Cannot load librarians from FOLIO", e);
            throw new LibrarianCallNumbersException("Cannot load librarians from FOLIO.");
        }

        if (responseObject == null) {
            return new LinkedList<Librarian>();
        }

        JSONArray compositeUsers = responseObject.getJSONArray("compositeUsers");
        List<Librarian> librarians = new LinkedList<Librarian>();
        for (int i = 0; i < compositeUsers.length(); i++) {
            Librarian librarian = new Librarian();
            JSONObject compositeUser = compositeUsers.getJSONObject(i);
            JSONObject user = compositeUser.getJSONObject("users");

            // set username
            librarian.setUsername(user.getString("username"));

            // set name
            JSONObject personal = user.getJSONObject("personal");
            librarian.setFirstName(personal.getString("firstName"));
            librarian.setLastName(personal.getString("lastName"));

            // parse call number prefixes
            JSONObject customFields = user.getJSONObject("customFields");
            String callNumberPrefixesString = customFields.getString("callNumbers");
            List<String> callNumberPrefixes = 
                Arrays.asList(StringUtils.tokenizeToStringArray(callNumberPrefixesString, ", "));
            librarian.setCallNumberPrefixes(callNumberPrefixes);

            librarians.add(librarian);
        }

        return librarians;
    }
    
}
