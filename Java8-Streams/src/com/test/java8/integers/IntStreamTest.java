package com.test.java8.integers;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class IntStreamTest {

	public static void main(String[] args) {
		
		/** Stream creation **/
		IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).forEach(System.out::println);

		// rangeClosed function includes the ending int, while range excludes it.
		IntStream.rangeClosed(1, 9).forEach(System.out::println);
		IntStream.range(1, 9).forEach(System.out::println);

		// With iterator we can define a start value and a function that will calculate the next ints based on the
		// previous element.
		IntStream.iterate(1, i -> i + 2).limit(5).forEach(System.out::println);

		/** Stream map usage **/
		IntStream.range(1, 5).map(i -> i * i).forEach(System.out::println);
		// mapToObject will simply return a Stream of the type that the mapping returns.
		// Stream<Color> stream = IntStream.range(1, 5).mapToObj(i -> getColor(i));

		// to convert an IntStream to a Stream<Integer>, there's a dedicated function for this job called boxed.
		IntStream.range(1, 5).boxed();
		IntStream.range(1, 5).mapToDouble(i -> i);
		IntStream.range(1, 5).mapToLong(i -> i);

		/** Stream match **/
		IntStream.range(1, 5).anyMatch(i -> i % 2 == 0); //true  
		IntStream.range(1, 5).allMatch(i -> i % 2 == 0); //false
		IntStream.range(1, 5).noneMatch(i -> i % 2 == 0); //false
		
		/** Stream filter **/
		
		
	}
}
