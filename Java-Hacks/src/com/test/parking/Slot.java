package com.test.parking;

public abstract class Slot {
	private boolean isOccupied;
	private int slotNumber;

	Slot(int slotNumber) {
		isOccupied = false;
		this.slotNumber = slotNumber;
	}

	boolean isOccupied() {
		return isOccupied;
	}

	int getSlotNumber() {
		return slotNumber;
	}

	void park() {
		isOccupied = true;
	}

	void unPark() {
		isOccupied = false;
	}

	@Override
	public boolean equals(Object o) {
		return (((Slot) o).slotNumber == this.slotNumber);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 53 * hash + this.slotNumber;
		return hash;
	}
}

class SmallSlot extends Slot {
	SmallSlot(int slotNumber) {
		super(slotNumber);
	}
}

class CompactSlot extends Slot {
	CompactSlot(int slotNumber) {
		super(slotNumber);
	}
}

class LargeSlot extends Slot {
	LargeSlot(int slotNumber) {
		super(slotNumber);
	}
}
