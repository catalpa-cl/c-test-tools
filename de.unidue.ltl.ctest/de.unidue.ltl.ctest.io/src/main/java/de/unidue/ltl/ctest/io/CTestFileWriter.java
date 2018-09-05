package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;

/**
 * A class, writing {@code CTestObject}s to file, using the <i>CTestFile Format</i>.
 * <p>
 * The format is defined as follows:
 * <ol>
 * <li> The file should start with a single <i>Metadata Line</i>. <br>
 * The <i>Metadata Line</i> starts with {@code CTestObject#COMMENT} marker, 
 * followed by the {@code CTestObject}'s language and number of gaps.
 * The language and number of gaps must be separated by a single tab and may be padded by any number of whitespaces.
 * If a <i>Metadata Line</i> is present, it must be the first line of the file.
 * <li> The <i>Metadata Line</i> may be followed by a single <i>ID Line</i>. <br>
 * The <i>ID Line</i> starts with {@code CTestObject#COMMENT} marker,
 * followed by the {@code CTestObject}'s id.
 * The id may be padded by any number of whitespaces.
 * If an <i>ID Line</i> is present, it must follow the <i>Metadata Line</i>.
 * <li> The file may contain any number of <i>Token Lines</i>, one for Token in the CTest. <br>
 * A <i>Token Line</i> must contain the {@code CTestToken}'s text.
 * If the token is gapped, the text must be followed by the id, prompt and error rate, gap type and gap index of the token, in this order, separated by tabs.
 * These properties may be followed by any number of additional solutions, also separated by tab.
 * <li> A <i>Token Line</i> may be followed by another <i>Token Line</i> or a <i>Sentence Boundary Line</i>. <br>
 * A <i>Sentence Boundary Line</i> must start with the {@code CTestObject#SENT_BOUNDARY} marker.
 * Surprisingly, it marks the end of a sentence.
 * </ol>
 * 
 * Example: <br>
 * <code>
 * %% en	2 <br>
 * %% example.ctest<br>
 * First<br>
 * ----<br>
 * Second	 1	null	0.0	postfix	3	other	solutions	here<br>
 * Third	2	null	1.3	postfix	2<br>
 * ----<br>
 * </code><br>
 * 
 * @see CTestFileObject
 * @see CTestToken
 */
public class CTestFileWriter {
	
	public CTestFileWriter() {}
		
	public void write(CTestObject object, Path filePath) throws IOException {
		if (filePath.toFile().isDirectory())
			throw new IOException("Input path is a directory, not a file.");
		
		Files.write(filePath, object.toString().getBytes());
	}
	
	public void write(CTestObject object, String filePath) throws IOException {
		this.write(object, Paths.get(filePath));
	}

	public void write(CTestObject object, File file) throws IOException {
		this.write(object, file.getAbsolutePath());
	}
}
