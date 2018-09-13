package de.unidue.ltl.ctest.io.results;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Reader class for reading files containing solutions to a C-Test.
 * <p>
 * An individual <i>solution</i> comprises a single response of a person given for a single gapped token in a C-Test.
 * It comprises only the given string to fill the gapped part of a token. 
 * <p>
 * <b>Note:</b> A given solution may be correct or incorrect. 
 * It is not the responsibility of a {@code CTestSolutionReader} to determine the correctness of a solution.
 * Use a {@link de.unidue.ltl.ctest.io.results.SolutionChecker} for this purpose.
 * It is compatible with the output of {@code CTestSolutionReader}s.
 * 
 * @see de.unidue.ltl.ctest.io.results.SolutionChecker
 */
public interface CTestSolutionReader {
	/**
	 * Reads a file, containing all solutions of a single person to a C-Test.
	 * 
	 * @param file the file containing the solution.
	 * @return  a list of strings, representing the solutions. The ith element in the list represents the person's solution for the ith gap token.
	 * 
	 * @throws IOException if file does not exist or reading fails otherwise.
	 */
	public List<String> read(Path path) throws IOException;
	
	/**
	 * Reads a file, containing all solutions of a single person to a C-Test.
	 * 
	 * @param file the file containing the solution.
	 * @return  a list of strings, representing the solutions. The ith element in the list represents the person's solution for the ith gap token.
	 * 
	 * @throws IOException if file does not exist or reading fails otherwise.
	 */
	public List<String> read(String filePath) throws IOException;
	
	/**
	 * Reads a file, containing all solutions of a single person to a C-Test.
	 * 
	 * @param file the file containing the solution.
	 * @return  a list of strings, representing the solutions. The ith element in the list represents the person's solution for the ith gap token.
	 * 
	 * @throws IOException if file does not exist or reading fails otherwise.
	 */
	public List<String> read(File file) throws IOException;
	
	/**
	 * Reads all files under the given directory which contain a person's solutions to a C-Test.
	 * 
	 * @param path the directory containing the solutions.
	 * @return  a two-dimensional list of strings, representing the solutions. <br>
	 * The jth element in the outer list contains all solutions by person j. <br>
	 * The ith element in the inner list represents the person's solution for the ith gap token.
	 * 
	 * @throws IOException if directory does not exist, contains no solution files or reading fails otherwise.
	 */
	public List<List<String>> readAll(Path path) throws IOException;
	

	/**
	 * Reads all files under the given directory which contain a person's solutions to a C-Test.
	 * 
	 * @param file the directory containing the solutions.
	 * @return  a two-dimensional list of strings, representing the solutions. <br>
	 * The jth element in the outer list contains all solutions by person j. <br>
	 * The ith element in the inner list represents the person's solution for the ith gap token.
	 * 
	 * @throws IOException if directory does not exist, contains no solution files or reading fails otherwise.
	 */
	public List<List<String>> readAll(File file) throws IOException;
	

	/**
	 * Reads all files under the given directory which contain a person's solutions to a C-Test.
	 * 
	 * @param filePath the directory containing the solutions.
	 * @return  a two-dimensional list of strings, representing the solutions. <br>
	 * The jth element in the outer list contains all solutions by person j. <br>
	 * The ith element in the inner list represents the person's solution for the ith gap token.
	 * 
	 * @throws IOException if directory does not exist, contains no solution files or reading fails otherwise.
	 */
	public List<List<String>> readAll(String filePath) throws IOException;
	
}
