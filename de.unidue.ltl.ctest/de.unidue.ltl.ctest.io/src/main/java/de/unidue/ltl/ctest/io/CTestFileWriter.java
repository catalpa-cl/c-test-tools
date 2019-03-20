package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import de.unidue.ltl.ctest.util.ModelVersion;
import de.unidue.ltl.ctest.util.Transformation;

/**
 * A class, writing {@code CTestObject}s to file, using the <i>CTestFile Format</i> (basically {@code CTestObject#toString()}).
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
 * A <i>Token Line</i> must be equal to the {@code CTestToken#toString()} method.
 * That is, it must contain the token's text.
 * If the token is gapped, it must also contain the token's id, prompt, error rate, gap type, gap index and other solutions,
 * in this order, separated by tabs. Other solutions are separated by a forward slash.
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
 * Second	 1	null	0.0	postfix	3	true	other/solutions/here<br>
 * Third	2	null	1.3	postfix	2	true<br>
 * ----<br>
 * </code><br>
 * 
 * @see CTestFileObject
 * @see CTestToken
 * @see CTestFileReader
 */
public class CTestFileWriter implements CTestWriter {
	
	private ModelVersion version;
	
	public CTestFileWriter() {
		this.version = ModelVersion.CURRENT;
	}
	
	/*
	 * Creates a new CTestFileWriter, writing CTests in the specified ModelVersion.
	 */
	public CTestFileWriter(ModelVersion version) {
		this.version = version;
	}
	
	public void write(CTestObject ctest, Path filePath) throws IOException {
		if (filePath.toFile().isDirectory())
			throw new IOException("Input path is a directory, not a file.");
		
		File outFile = filePath.toFile();
		if (!outFile.exists()) {
			outFile.getParentFile().mkdirs();
			outFile.createNewFile();
		}
		
		//TODO: move to Transformation.toCTestFileFormat(CTest ctest)
		//TODO: update toString method of CTestObject
		StringBuilder sb = new StringBuilder();
		
		sb.append(CTestObject.COMMENT + " " + ctest.getLanguage() + "\t" + ctest.getGapCount() + "\n");
		if (ctest.getId() != null) {
			sb.append(CTestObject.COMMENT + " " + ctest.getId() + "\n");			
		}
		
		for (CTestToken token : ctest.getTokens()) {
			sb.append(Transformation.toCTestFileFormat(token, version));
			if (token.isLastTokenInSentence()) {
				sb.append("\n");
				sb.append(CTestObject.SENT_BOUNDARY);
			}
			sb.append("\n");
		}
		
		Files.write(filePath, sb.toString().getBytes());
	}
	
	public void write(CTestObject ctest, String filePath) throws IOException {
		this.write(ctest, Paths.get(filePath));
	}

	public void write(CTestObject ctest, File file) throws IOException {
		this.write(ctest, file.getAbsolutePath());
	}
}
