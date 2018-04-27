package com.test.parking;

public abstract class Vehicle {
    protected String licensePlate;
    protected String slotsNeeded;
  //  protected SlotSize size;	
    abstract boolean canFitInSlot(Slot slot);
}

class Motorcycle extends Vehicle {
	boolean canFitInSlot(Slot slot) {
		return false;
	}
}

class Car extends Vehicle {
	boolean canFitInSlot(Slot slot) {
		return false;
	}
}

class Bus extends Vehicle {
	boolean canFitInSlot(Slot slot) {
		return false;
	}
}
