package org.dorijan.rba.utilities;

import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v142.network.Network;
import org.openqa.selenium.devtools.v142.network.model.RequestId;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A helper utility that uses Chrome DevTools Protocol (CDP) to listen to network calls and extract exchange rates
 * from JSON in the response of the {@code calculateExchangeRate} API call made by the website
 *
 */
public class JsonParser {

    private final DevTools devTools;
    private final AtomicReference<String> lastBody = new AtomicReference<>(null);

    public JsonParser(WebDriver driver) {
        this.devTools = ((HasDevTools) driver).getDevTools();
        this.devTools.createSession();

        devTools.send(Network.enable(
                Optional.empty(),   // maxTotalBufferSize
                Optional.empty(),   // maxResourceBufferSize
                Optional.empty(),   // maxPostDataSize
                Optional.empty(),   // reportDirectSocketTraffic
                Optional.empty()    // enableDurableMessages
        ));

        devTools.addListener(Network.responseReceived(), response -> {
            String url = response.getResponse().getUrl();
            if (url.contains("calculateExchangeRate")) {
                try {
                    RequestId requestId = response.getRequestId();
                    Network.GetResponseBodyResponse bodyResp =
                            devTools.send(Network.getResponseBody(requestId));
                    lastBody.set(bodyResp.getBody());
                } catch (Exception ignored) {
                }
            }
        });
    }

    /**
     * Returns the most recent captured API response body.
     *
     * @return the latest raw API response body or null
     */
    public String getCurrentBody() {
        return lastBody.get();
    }

    /**
     * Blocks until a new, valid JSON response is captured from the
     * {@code calculateExchangeRate} API.
     *
     * @param timeout       maximum time to wait before failing
     * @param previousBody  the body previously seen, used to detect new responses
     * @return a parsed {@link JSONObject} containing a valid exchange rate payload
     * @throws RuntimeException if no valid response arrives within the timeout
     */
    public JSONObject waitForNewResponse(Duration timeout, String previousBody) {
        Instant end = Instant.now().plus(timeout);
        String lastSeen = previousBody;

        while (Instant.now().isBefore(end)) {
            String body = lastBody.get();
            if (body != null && !body.equals(lastSeen)) {
                try {
                    JSONObject json = new JSONObject(body);
                    JSONObject form = json.getJSONObject("form");

                    // only accept responses with some real data
                    if (!form.isNull("exchangeRate") || !form.isNull("effectiveRate")) {
                        return json;
                    }

                    // otherwise treat as seen and keep waiting
                    lastSeen = body;
                } catch (Exception ignored) {
                    // HTML / non-JSON / junk – mark as seen and continue
                    lastSeen = body;
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        throw new RuntimeException("No valid calculateExchangeRate response captured in time");
    }

    /**
     * Extracts the {@code exchangeRate} field from a parsed API response.
     *
     * @param json a valid JSON object from {@code calculateExchangeRate} API
     * @return the numeric exchange rate
     */
    public double getRealExchangeRate(JSONObject json) {
        return json.getJSONObject("form").getDouble("exchangeRate");
    }

    /**
     * Extracts the {@code effectiveRate} field from a parsed API response.
     *
     * @param json a valid JSON object  from {@code calculateExchangeRate} API
     * @return the numeric effective exchange rate
     */
    public double getRealEffectiveRate(JSONObject json) {
        return json.getJSONObject("form").getDouble("effectiveRate");
    }
}
