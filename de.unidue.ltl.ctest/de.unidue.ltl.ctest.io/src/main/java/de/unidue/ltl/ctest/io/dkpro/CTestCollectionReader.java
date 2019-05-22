package de.unidue.ltl.ctest.io.dkpro;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.io.CTestFileReader;
import de.unidue.ltl.ctest.io.CTestReader;
import de.unidue.ltl.ctest.util.Transformation;

/***
 * A {@code ResourceCollectionReader} for Collections of C-Tests that were serialized by a {@code CTestWriter}.
 * <p>
 * The resources under the given location are read with a {@code CTestReader}.
 * The Reader's Class Name must be provided as a parameter.
 * If unspecified, the {@code CTestFileReader} is used per default. 
 * The resulting JCas is annotated with {@code Token}, {@code Sentence} and {@code Gap} Annotations.
 * 
 * @see de.unidue.ltl.ctest.io.CTestWriter
 * @see de.unidue.ltl.ctest.io.CTestReader
 * @see de.unidue.ltl.ctest.type.Gap
 */
public class CTestCollectionReader extends ResourceCollectionReaderBase {
	
	public static final String PARAM_CTEST_READER = "ctestReaderClassName";
    @ConfigurationParameter(name = PARAM_CTEST_READER , mandatory = false, defaultValue = "default")
    private String readerClassName;
    
    public static final String DEFAULT_READER = CTestFileReader.class.getName();
    
    private CTestReader reader;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		super.initialize(aSpecifier, aAdditionalParams);
		if (readerClassName.equals("default")) {
			readerClassName = DEFAULT_READER;
		}
		try {
			reader = (CTestReader) Class.forName(readerClassName).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void getNext(CAS aCas) throws IOException, CollectionException {
		Resource res = nextFile();
		File file = new File(res.getResolvedUri());
        initCas(aCas, res);
        System.out.println(res.getResolvedUri());

        try {
			JCas jcas = aCas.getJCas();
			CTestObject ctest = reader.read(file);
			Transformation.addToJCas(ctest, jcas);
		} catch (CASException e) {
			System.err.println("ERROR: Could not retrieve JCAS from given CAS");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("ERROR: Could not read the file using the provided reader. File: " + res.getPath());
			e.printStackTrace();
		}
	}

}
