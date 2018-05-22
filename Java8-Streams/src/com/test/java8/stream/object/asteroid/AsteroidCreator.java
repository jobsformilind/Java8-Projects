package com.test.java8.stream.object.asteroid;

import java.util.ArrayList;
import java.util.List;

public class AsteroidCreator {

	public static List<Asteroid> getAsteroids() {
		List<Asteroid> asteroids = new ArrayList<>();
		asteroids.add(new Asteroid("Ceres", 238.0d));
		asteroids.add(new Asteroid("Vesta", 128.0d));
		asteroids.add(new Asteroid("Europa", 320.0d));
		asteroids.add(new Asteroid("Cybele", 234.0d));
		asteroids.add(new Asteroid("Eunomia", 512.0d));
		asteroids.add(new Asteroid("Pallas", 440.0d));
		return asteroids;
	}

	public static List<Asteroid> getAsteroidNames() {
		List<Asteroid> asteroids = new ArrayList<>();
		asteroids.add(new Asteroid("Ceres"));
		asteroids.add(new Asteroid("Vesta"));
		asteroids.add(new Asteroid("Europa"));
		asteroids.add(new Asteroid("Cybele"));
		asteroids.add(new Asteroid("Eunomia"));
		asteroids.add(new Asteroid("Pallas"));
		return asteroids;
	}

}
