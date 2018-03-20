package com.test.hackerranks;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class StringSupplierSolution {
	public static String EOS_STREAM = ":EOS_STREAM:";

	public static void compareStrings(StringSupplier supplier1, StringSupplier supplier2, List<Boolean> results) {
		while (true) {
			String string1 = getNextFromSupplier(supplier1);
			String string2 = getNextFromSupplier(supplier2);
			if (string1.equals(string2)) {
				if (string1.equals(EOS_STREAM)) {
					break;
				}
				results.add(true);
			} else {
				results.add(false);
			}
		}
	}

	private static String getNextFromSupplier(StringSupplier supplier) {
		try {
			String next = supplier.getNext();
			return next != null ? next : "";
		} catch (RuntimeException ex) {
			return EOS_STREAM;
		}
	}

	private static class StringSupplierImpl implements StringSupplier {
		private final List<String> strings;
		private Class<? extends RuntimeException> endOfInputExceptionClass;
		private int index;

		public StringSupplierImpl(List<String> strings, Class<? extends RuntimeException> endOfInputExceptionClass) {
			this.strings = strings;
			this.endOfInputExceptionClass = endOfInputExceptionClass;
			this.index = 0;
		}

		@Override
		public String getNext() {
			if (index == strings.size()) {
				try {
					throw endOfInputExceptionClass.newInstance();
				} catch (InstantiationException e) {
					System.out.println("getNext(): InstantiationException while creating end of input exception " + endOfInputExceptionClass.getCanonicalName());
					throw new RuntimeException();
				} catch (IllegalAccessException e) {
					System.out.println("getNext(): IllegalAccessException while creating end of input exception " + endOfInputExceptionClass.getCanonicalName());
					throw new RuntimeException();
				}
			}
			return strings.get(index++);
		}
	}

	public static void main(String[] args) {
		List<String> stringSequence1 = new ArrayList<String>();
		stringSequence1.add("foo");
		stringSequence1.add("foo");
		stringSequence1.add("");
		stringSequence1.add("");
		List<String> stringSequence2 = new ArrayList<String>();
		stringSequence2.add("foo");
		stringSequence2.add("foor");
		StringSupplierImpl stringSupplier1 = new StringSupplierImpl(stringSequence1, ArrayIndexOutOfBoundsException.class);
		StringSupplierImpl stringSupplier2 = new StringSupplierImpl(stringSequence2, NoSuchElementException.class);
		List<Boolean> results = new ArrayList<Boolean>();

		compareStrings(stringSupplier1, stringSupplier2, results);

		for (Boolean result : results) {
			System.out.println(result);
		}
	}

	interface StringSupplier {
		String getNext();
	}
}
