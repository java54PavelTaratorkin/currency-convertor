package telran.currency.service;

import java.util.*;
import java.util.stream.Collectors;

public class AbstractCurrencyConvertor implements CurrencyConvertor {
    protected Map<String, Double> rates; // key - currency ISO code; value - amount of code's units in 1 EUR

    @Override
    public List<String> strongestCurrencies(int amount) {
        return getSortedCurrencies(amount, true);
    }

    @Override
    public List<String> weakestCurrencies(int amount) {
        return getSortedCurrencies(amount, false);
    }
    
    // Method for sorting currencies and returning the top `amount` based on the sort order
    protected List<String> getSortedCurrencies(int amount, boolean ascending) {
        return rates.entrySet().stream()
                .sorted(ascending ? Map.Entry.comparingByValue() : Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(amount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public double convert(String codeFrom, String codeTo, int amount) {
        // Assuming null checks are handled elsewhere
        Double rateFrom = rates.get(codeFrom);
        Double rateTo = rates.get(codeTo);
        return amount * rateTo / rateFrom;
    }

    @Override
    public HashSet<String> getAllCodes() {
        // Return all available currency codes
        return new HashSet<>(rates.keySet());
    }
}