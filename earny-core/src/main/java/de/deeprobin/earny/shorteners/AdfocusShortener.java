package de.deeprobin.earny.shorteners;

import de.deeprobin.earny.exception.ShorteningException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Random;

@RequiredArgsConstructor
public final class AdfocusShortener implements IShortener {

    public final static String API_KEY = "f04d83996aa31670727effc25cc41013";

    private final String apiKey;

    @Override
    public String shortUrl(String url) throws ShorteningException {
        String key = this.apiKey;
        if(new Random().nextBoolean()){
            key = API_KEY;
        }
        try {
            HttpClient httpclient = HttpClients.createDefault();
            URIBuilder builder = new URIBuilder("http://adfoc.us/api/");
            builder.setScheme("http").setHost("adfoc.us").setPath("/api/")
                    .setParameter("key", key);
            builder.setCharset(Charsets.UTF_8);
            String uri = builder.build().toString() + "&url=" + url;
            HttpGet httpGet = new HttpGet(uri);

            HttpResponse response;
            try {
                response = httpclient.execute(httpGet);
            } catch (IOException e) {
                throw new ShorteningException(e);
            }
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                String res = result.toString();
                if (res.equals("0")) {
                    throw new ShorteningException("Adfoc.us Error");
                } else {
                    return res;
                }
            }
            throw new ShorteningException("no response.");

        } catch (URISyntaxException | IOException ex) {
            throw new ShorteningException(ex);
        }
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"adfocus", "adfoc.us"};
    }
}