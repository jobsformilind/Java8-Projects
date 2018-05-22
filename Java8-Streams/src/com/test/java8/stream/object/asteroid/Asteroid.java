package com.test.java8.stream.object.asteroid;

public class Asteroid {
	private String name;
	private Double diameter;

	public Asteroid(String name, Double diameter) {
		this.name = name;
		this.diameter = diameter;
	}

	public Asteroid(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getDiameter() {
		return diameter;
	}

	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Asteroid [");
		if (name != null)
			builder.append("name=").append(name);
		if (diameter != null)
			builder.append(", diameter=").append(diameter);
		builder.append("]");
		return builder.toString();
	}

	

}
