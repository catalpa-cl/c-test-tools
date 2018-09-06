package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;

import de.unidue.ltl.ctest.core.CTestObject;


/**
 * A class, reading {@code CTestObject}s from file. The file must contain a {@code CTestObject}, 
 * serialized using the {@code Serializable} interface, e.g. using a {@code CTestBinaryWriter}.
 * 
 * @see CTestBinaryWriter
 * @see CTestObject
 * @see java.io.Serializable
 */
public class CTestBinaryReader implements CTestReader {

	@Override
	public CTestObject read(Path path) throws IOException {
		return this.read(path.toFile());
	}

	@Override
	public CTestObject read(String filePath) throws IOException {
		return this.read(new File(filePath));
	}

	@Override
	public CTestObject read(File file) throws IOException {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			CTestObject object = (CTestObject) in.readObject();
			in.close();
			return object;
		} catch (Exception e) { 
			throw new IOException(e); 
		}
	}

}
