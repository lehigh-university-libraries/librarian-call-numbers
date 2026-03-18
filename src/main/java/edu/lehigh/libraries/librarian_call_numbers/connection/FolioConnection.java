package edu.lehigh.libraries.librarian_call_numbers.connection;

import java.io.IOException;
import java.net.URI;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.net.URIBuilder;
import org.json.JSONObject;

import edu.lehigh.libraries.librarian_call_numbers.config.PropertiesConfig;
import lombok.extern.slf4j.Slf4j;

// TODO Refactor this class with the Purchase Requests project versions into a standalone module
@Slf4j
public class FolioConnection {

    private static final String LOGIN_PATH = "/authn/login";

    private static final String TENANT_HEADER = "x-okapi-tenant";
    private static final String TOKEN_HEADER = "x-okapi-token";

    private final PropertiesConfig config;

    private CloseableHttpClient client;
    private String token;

    public FolioConnection(PropertiesConfig config) throws Exception {
        this.config = config;

        initConnection();
        initToken();

        log.debug("FOLIO connection ready");
    }

    private void initConnection() {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(new AuthScope(null, -1),
            new UsernamePasswordCredentials(config.getFolio().getUsername(), config.getFolio().getPassword().toCharArray()));
        client = HttpClientBuilder.create()
            .setDefaultCredentialsProvider(provider)
            .build();
    }

    private void initToken() throws Exception {
        String url = config.getFolio().getOkapiBaseUrl() + LOGIN_PATH;
        URI uri = new URIBuilder(url).build();

        JSONObject postData = new JSONObject();
        postData.put("username", config.getFolio().getUsername());
        postData.put("password", config.getFolio().getPassword());
        postData.put("tenant", config.getFolio().getTenantId());

        ClassicHttpRequest post = ClassicRequestBuilder.post()
            .setUri(uri)
            .setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
            .setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType()).setVersion(HttpVersion.HTTP_1_1)
            .setHeader(TENANT_HEADER, config.getFolio().getTenantId())
            .setEntity(new StringEntity(postData.toString()))
            .build();

        token = client.execute(post, response -> {
            int responseCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (responseCode > 399) {
                throw new IOException(responseBody);
            }
            log.debug("got auth response from folio with response code: " + responseCode);
            return response.getFirstHeader(TOKEN_HEADER).getValue();
        });
        log.info("token: " + token);
    }

    public JSONObject executeGet(String url, String queryString) throws Exception {
        ClassicHttpRequest getRequest = ClassicRequestBuilder.get()
            .setUri(url)
            .setHeader(TENANT_HEADER, config.getFolio().getTenantId())
            .setHeader(TOKEN_HEADER, token)
            .addParameter("query", queryString)
            .addParameter("include", "users")
            .build();

        return client.execute(getRequest, response -> {
            if (response.getCode() > 399) {
                throw new IOException(response.getReasonPhrase());
            }
            String responseString = EntityUtils.toString(response.getEntity());
            log.debug("Got response with code " + response.getCode() + " and entity " + response.getEntity());
            return new JSONObject(responseString);
        });
    }

}
