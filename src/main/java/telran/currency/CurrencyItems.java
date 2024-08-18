package telran.currency;

import java.util.*;
import java.util.stream.Collectors;
import telran.currency.service.CurrencyConvertor;
import telran.view.*;

public class CurrencyItems {
    static CurrencyConvertor currencyConvertor;
    static HashSet<String> allCodes;
    static int maxCurrencies;

    public static List<Item> getItems(CurrencyConvertor currencyConvertor) {
        CurrencyItems.currencyConvertor = currencyConvertor;
        CurrencyItems.allCodes = currencyConvertor.getAllCodes();
        CurrencyItems.maxCurrencies = allCodes.size();

        Item[] items = {
            Item.of("Display Strongest Currencies", CurrencyItems::displayStrongestCurrencies),
            Item.of("Display Weakest Currencies", CurrencyItems::displayWeakestCurrencies),
            Item.of("Convert Currency", CurrencyItems::convertCurrency),
            Item.ofExit()
        };
        return new ArrayList<>(List.of(items));
    }

    static void displayStrongestCurrencies(InputOutput io) {
        int amount = io.readNumberRange(
            "Enter the number of strongest currencies to display",
            "Invalid number", 1, maxCurrencies
        ).intValue();
        Map<String, Double> strongestCurrencies = getCurrenciesWithValues(currencyConvertor.strongestCurrencies(amount));
        strongestCurrencies.forEach((code, value) ->
                io.writeLine(String.format("%s: %.4f EUR", code, value))
        );
    }

    static void displayWeakestCurrencies(InputOutput io) {
        int amount = io.readNumberRange(
            "Enter the number of weakest currencies to display",
            "Invalid number", 1, maxCurrencies
        ).intValue();
        Map<String, Double> weakestCurrencies = getCurrenciesWithValues(currencyConvertor.weakestCurrencies(amount));
        weakestCurrencies.forEach((code, value) ->
                io.writeLine(String.format("%s: %.4f EUR", code, value))
        );
    }

    static void convertCurrency(InputOutput io) {
        String codeFrom = io.readStringOptions(
            "Enter the currency code to convert from", 
            "Invalid code", 
            allCodes
        );
        String codeTo = io.readStringOptions(
            "Enter the currency code to convert to", 
            "Invalid code", 
            allCodes
        );
        int amount = io.readInt("Enter the amount to convert", "Invalid amount");
        double result = currencyConvertor.convert(codeFrom, codeTo, amount);
        io.writeLine(String.format("%d %s = %.2f %s", amount, codeFrom, result, codeTo));
    }

    private static Map<String, Double> getCurrenciesWithValues(List<String> currencies) {
        return currencies.stream()
                .collect(Collectors.toMap(
                        code -> code,
                        code -> currencyConvertor.convert("EUR", code, 1)
                ));
    }    
}