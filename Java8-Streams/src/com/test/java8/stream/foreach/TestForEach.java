package com.test.java8.stream.foreach;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestForEach {

	public static void main(String[] args) {

		List<Integer> list = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());

		// Traversing using Iterator
		Iterator<Integer> it = list.iterator();
		while (it.hasNext()) {
			Integer i = it.next();
			System.out.println("Iterator Value::" + i);
		}

		// Traversing through forEach method of Iterable with anonymous class
		list.forEach(new Consumer<Integer>() {
			public void accept(Integer t) {
				System.out.println("forEach anonymous class Value::" + t);
			}
		});

		// traversing with Consumer interface implementation
		MyConsumer action = new MyConsumer();
		list.forEach(action);
	}
}

// Consumer implementation that can be reused
class MyConsumer implements Consumer<Integer> {

	public void accept(Integer t) {
		System.out.println("Consumer impl Value::" + t);
	}

}
