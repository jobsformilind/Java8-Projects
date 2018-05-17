package com.test.vm;

import java.util.HashMap;
import java.util.Map;

public class VendingMachine {
	int money = 5;

	public static void main(String[] args) {
		VendingMachine vm = new VendingMachine();
		double amt1 = vm.purchase(5, "ITEM1");
		System.out.println("ITEM1 purchase return1 : " + amt1);
		
		double amt2 = vm.purchase(10, "ITEM2");
		System.out.println("ITEM1 purchase return2 : " + amt2);
	}

	private double purchase(double payAmount, String item) {
		double toReturn = payAmount;
		Item itemPurchased = getItems().get(item);
		if (itemPurchased != null) {
			toReturn = change(payAmount, itemPurchased.getPrice());
			VMCache.getChange(toReturn);
		}
		return toReturn;
	}

	private double change(double payAmount, double price) {
		return payAmount - price;
	}

	private static Map<String, Item> getItems() {
		Map<String, Item> items = new HashMap<String, Item>();
		items.put("ITEM1", new Item("ITEM1", 1.49));
		items.put("ITEM2", new Item("ITEM2", 1.29));
		items.put("ITEM3", new Item("ITEM3", 1.89));
		return items;
	}
}
