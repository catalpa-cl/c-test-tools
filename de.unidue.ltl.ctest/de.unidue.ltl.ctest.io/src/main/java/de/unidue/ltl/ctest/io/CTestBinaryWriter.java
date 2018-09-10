package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

import de.unidue.ltl.ctest.core.CTestObject;

/**
 * A class writing {@code CTestObject}s to file, using the {@code Serializable} interface.
 * 
 * @see CTestObjectBinaryReader
 * @see CTestObject
 * @see java.io.Serializable
 */
public class CTestBinaryWriter implements CTestWriter {

	@Override
	public void write(CTestObject object, Path filePath) throws IOException {
		this.write(object, filePath.toFile());
	}

	@Override
	public void write(CTestObject object, String filePath) throws IOException {
		this.write(object, new File(filePath));
	}

	@Override
	public void write(CTestObject object, File file) throws IOException {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		out.writeObject(object);
		out.close();
	}

}
