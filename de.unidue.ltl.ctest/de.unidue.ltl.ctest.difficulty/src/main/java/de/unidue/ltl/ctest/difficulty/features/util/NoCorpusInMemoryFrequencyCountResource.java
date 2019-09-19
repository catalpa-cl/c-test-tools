package de.unidue.ltl.ctest.difficulty.features.util;

import static org.apache.uima.util.Level.SEVERE;

import java.io.IOException;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.FrequencyCountResourceBase;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;

public class NoCorpusInMemoryFrequencyCountResource extends FrequencyCountResourceBase
		implements FrequencyCountProvider {

    /**
     * Indicates whether the given frequency count file contains a header.
     * Defaults to true.
     */
    public static final String PARAM_HAS_HEADER = "HasHeader";
    @ConfigurationParameter(name = PARAM_HAS_HEADER, mandatory = false, defaultValue = "true")
    protected String hasHeader;
	
    /**
     * Language to which the frequency counts belong.
     */
    public static final String PARAM_LANGUAGE = "Language";
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false, defaultValue = "en")
    protected String language;
    
    /**
     * Symbol that separates the frequency count file's entries.
     * Defaults to "\t".
     */
    public static final String PARAM_SEPARATOR = "SeparatorSymbol";
    @ConfigurationParameter(name = PARAM_SEPARATOR, mandatory = false, defaultValue = "\t")
    protected String separatorSymbol;

    /**
     * Path to the file containing the frequency counts.
     */
    public static final String PARAM_FREQUENCY_FILE = "FrequencyFilePath";
    @ConfigurationParameter(name = PARAM_FREQUENCY_FILE, mandatory = true)
    protected String frequencyFilePath;
    
	@Override
	protected void initializeProvider() throws Exception {
		this.provider = new NoCorpusInMemoryFrequencyCountProvider(frequencyFilePath, language, separatorSymbol, Boolean.parseBoolean(hasHeader));
	}
	
    private void checkProvider()
    {
        if (provider == null) {
            try {
                initializeProvider();
            }
            catch (Exception e) {
                getLogger().log(SEVERE,
                        "RuntimeException caught when initializing frequency count resource.");
                getLogger().log(SEVERE, e.getLocalizedMessage());
                getLogger().log(SEVERE, e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

}
