package telran.currency;

import telran.currency.service.FixerApiPerDay;
import telran.view.*;
import java.util.List;

public class CurrencyConvertorAppl {

	public static void main(String[] args) {
		InputOutput io = new SystemInputOutput();
		FixerApiPerDay currencyConvertor = new FixerApiPerDay();
		List<Item> items = CurrencyItems.getItems(currencyConvertor);

		Menu menu = new Menu("Currency Converter", items.toArray(Item[]::new));
		menu.perform(io);
	}
}