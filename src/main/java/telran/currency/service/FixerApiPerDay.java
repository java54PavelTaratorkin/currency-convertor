package telran.currency.service;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class FixerApiPerDay extends AbstractCurrencyConvertor {
    protected String uriString = "http://data.fixer.io/api/latest?access_key=e031d66744a77e88d5932063dbc758b0";
    private LocalDateTime lastRefreshDateTime; // Field to track the last refresh date

    public FixerApiPerDay() {
        refresh(); // Ensure initial rates fetch
    }

    protected HashMap<String, Double> getRates() {
        try {
            JSONObject jsonObject = fetchRatesJson();
            JSONObject jsonRates = jsonObject.getJSONObject("rates");

            // Using Streams to build the map from jsonRates
            Map<String, Double> ratesMap = jsonRates.keySet().stream()
                    .collect(Collectors.toMap(
                            key -> key,
                            key -> jsonRates.getDouble(key)
                    ));

            // Set lastRefreshDateTime based on the "timestamp" field in the response
            if (lastRefreshDateTime == null) {
                long timestamp = jsonObject.getLong("timestamp");
                lastRefreshDateTime = Instant.ofEpochSecond(timestamp).atZone(ZoneOffset.UTC).toLocalDateTime();
            }

            return new HashMap<>(ratesMap); // Convert to HashMap if necessary
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch currency rates.", e);
        }
    }

    @Override
    public List<String> strongestCurrencies(int amount) {
        refresh();
        return super.strongestCurrencies(amount);
    }

    @Override
    public List<String> weakestCurrencies(int amount) {
        refresh();
        return super.weakestCurrencies(amount);
    }

    @Override
    public double convert(String codeFrom, String codeTo, int amount) {
        refresh();
        return super.convert(codeFrom, codeTo, amount);
    }

    private void refresh() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (lastRefreshDateTime == null || Duration.between(lastRefreshDateTime, currentDateTime).toHours() >= 24) {
            rates = getRates(); // Refresh rates if 24 hours have passed or on first call
        }
    }

    private JSONObject fetchRatesJson() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(new URI(uriString)).build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        return new JSONObject(response.body());
    }
}