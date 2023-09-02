package org.aspire.test.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

@Slf4j
public abstract class BaseParser {
    public Document load(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode sendRequest(String url) {
        final HttpClient httpClient = HttpClients.custom().setDefaultHeaders(headers()).build();
        final HttpGet httpGet = new HttpGet(url);
        final HttpResponse response;
        try {
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readTree(responseBody);
            } else {
                throw new RuntimeException("HTTP request failed with status code: " + statusCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract List<Header> headers();
}
