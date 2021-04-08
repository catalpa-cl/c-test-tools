package de.unidue.ltl.ctest.io;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.util.IOSModelVersion;
import de.unidue.ltl.ctest.util.ModelVersion;

import java.io.File;
import java.io.IOException;

/**
 * This code converts all files in the "texts"-folder into c-test-files.
 */
public class IOSReadWrite {
    public static void main(String[] args) {
        File folder = new File("texts/");

        for (File file : folder.listFiles()) {
            String inputPath = file.getPath();
            String outputPath = file.getName();

            CTestIOSReader reader = new CTestIOSReader(IOSModelVersion.V1);
            CTestFileWriter writer = new CTestFileWriter(ModelVersion.V1);

            try {
                CTestObject ctest = reader.read(new File(inputPath));
                writer.write(ctest, new File(outputPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
