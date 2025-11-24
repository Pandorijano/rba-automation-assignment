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

    // read whatever the latest body is right now (can be null)
    public String getCurrentBody() {
        return lastBody.get();
    }

    // wait until we see a *different* body than previousBody
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

    public double getRealExchangeRate(JSONObject json) {
        return json.getJSONObject("form").getDouble("exchangeRate");
    }

    public double getRealEffectiveRate(JSONObject json) {
        return json.getJSONObject("form").getDouble("effectiveRate");
    }
}
