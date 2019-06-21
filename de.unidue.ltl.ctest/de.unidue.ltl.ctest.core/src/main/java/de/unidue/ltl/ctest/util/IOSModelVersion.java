package de.unidue.ltl.ctest.util;


/**
 * An enum specifying the Version of a serialized C-Test in IOS Format.
 * Used by the {@code CTestIOSReader} and {@code CTestIOSWriter} for backwards compatibility.
 */
public enum IOSModelVersion {
	CURRENT,
	V1,
	V2
}
