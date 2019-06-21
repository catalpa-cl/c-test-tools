package de.unidue.ltl.ctest.util;

/**
 * An enum specifying the Version of a serialized C-Test.
 * Used by the {@code CTestFileReader} and {@code CTestFileWriter} for backwards compatibility.
 */
public enum ModelVersion {
	CURRENT,
	V1,
	V2,
	V3
}
