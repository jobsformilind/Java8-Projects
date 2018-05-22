package com.test.java8.stream.object.asteroid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class AsteroidTest {
	public static void main(String[] args) {
		List<Asteroid> oAsteroids = AsteroidCreator.getAsteroids();
		List<Asteroid> nAsteroids = AsteroidCreator.getAsteroidNames();
		
		Asteroid ast = oAsteroids.stream().reduce(new Asteroid("", 0.0),
				(a, b) -> a.getDiameter() > b.getDiameter() ? b : a);
		//System.out.println(ast);

		print("Contains P", oAsteroids.stream()
				.filter(a -> a.getName().contains("p")));

		print("StartsWith E", nAsteroids.stream()
				.filter(a -> a.getName().startsWith("E"))
				.map(a -> a.getName().toUpperCase())
				.sorted());
		
		print("Reduce", nAsteroids.stream()
				.map(a->a.getName().length())
				.reduce((a,b)->b%2==0?a+b:a));
		
		
	}

	private static void print(String desc, Stream<?> stream) {
		System.out.println("\n--------: " + desc);
		stream.forEach(System.out::println);
	}
	private static void print(String desc, Optional<?> optional) {
		System.out.println("\n--------: " + desc);
		optional.ifPresent(System.out::println);
	}
}
