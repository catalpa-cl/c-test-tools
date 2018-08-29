package de.unidue.ltl.ctest.core;

public enum GapType {
	POSTFIX("postfix"),
	PREFIX("prefix");
	
	private String value;
	
	GapType(String value) {
		this.value = value;
	}
	
	public static boolean isValid(String type) {
		return type.equals(GapType.POSTFIX.toString()) || type.equals(GapType.PREFIX.toString()); 
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
