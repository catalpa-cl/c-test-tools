package de.unidue.ltl.ctest.core;

/**
 * An enum, specifying possible kinds of gaps in a CTest.
 * <p>
 * A <i>postfix</i> gap is a gap at the end of the word, e.g. "end___" (ending).
 * A <i>prefix</i> gap is a gap at the beginning of the word, e.g. "___inning" (beginning).
 */
public enum GapType {
	POSTFIX("postfix"),
	PREFIX("prefix");
	
	private String value;
	
	GapType(String value) {
		this.value = value;
	}
	
	/**
	 * Returns whether a given string matches one of the possible values for gaptypes.
	 */
	public static boolean isValid(String type) {
		return type.equals(GapType.POSTFIX.toString()) || type.equals(GapType.PREFIX.toString()); 
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
