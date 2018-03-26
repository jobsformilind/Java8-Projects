package com.test.java8.stream.integers;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class IntStreamTest {

	public static void main(String[] args) {
		
		/** Stream creation **/
		IntStream.of(1, 2, 3).forEach(System.out::println);

		// rangeClosed function includes the ending int, while range excludes it.
		IntStream.rangeClosed(1, 3).forEach(System.out::println);
		IntStream.range(1, 3).forEach(System.out::println);

		// With iterator we can define a start value and a function that will calculate the next ints based on the
		// previous element.
		IntStream.iterate(1, i -> i + 2).limit(2).forEach(System.out::println);

		/** Stream map usage **/
		// Steam<T> takes function<T, R> and returns Stream(R)
		IntStream.range(1, 3).map(i -> i * i).forEach(System.out::println);
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
		

		/** Reduce **/
		IntStream.range(1, 5).reduce(1, (x, y) -> x * y);
		int sum = IntStream.range(1, 10).reduce(0, Integer::sum);
		System.out.println("Sum is : " + sum);
		
		String concatStr = IntStream.rangeClosed(1, 10).mapToObj(String::valueOf).reduce("", (concat, str)->concat.concat(str));
		String concatStr1 = IntStream.rangeClosed(1, 10).mapToObj(String::valueOf).reduce("", String::concat);
		System.err.println("concatStr is  : "+concatStr);
		System.err.println("concatStr1 is : "+concatStr1);
		
		/** parallel **/
		IntStream.range(1, 5).parallel().forEach(i -> heavyOperation(i));  

		/** Collect **/
		List<Integer> collect = IntStream.rangeClosed(1, 10)
											.filter(i -> i%2==0)
											.mapToObj(i-> i*2)
											.collect(Collectors.toList());
		System.out.println("collect : "+collect);
		
		//Given the values, double the even numbers and total
		OptionalInt sumEven = IntStream.rangeClosed(1, 10).filter(i -> i%2==0).map(i->i*2).reduce(Integer::sum);
		System.err.println("sumEven is : "+sumEven.getAsInt());
		
	}

	private static Object heavyOperation(int i) {
		System.out.println("i="+i);
		return null;
	}
}
