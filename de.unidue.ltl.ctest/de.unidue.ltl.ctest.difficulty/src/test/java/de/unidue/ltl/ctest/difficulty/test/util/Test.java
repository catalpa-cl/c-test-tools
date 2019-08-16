package de.unidue.ltl.ctest.difficulty.test.util;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.FrequencyCountResourceBase;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyUtils;
import de.tudarmstadt.ukp.dkpro.core.frequency.Web1TInMemoryProvider;
import de.tudarmstadt.ukp.dkpro.core.frequency.resources.Web1TFrequencyCountResource;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		String languageCode = "en";
		String WEB1T_PATH = Paths.get(System.getenv("DKPRO_HOME"), "web1t", "en").toString();
		Web1TInMemoryProvider provider = new Web1TInMemoryProvider(languageCode, WEB1T_PATH, 2);
		Instant start = Instant.now();
		long freq = provider.getFrequency("die");
		Instant end = Instant.now();
		System.out.println("done!");
		System.out.println("Frequency: 'die'");
		System.out.println(freq);
		System.out.println("Frequency: 'die in'");
		System.out.println(provider.getFrequency("die in"));
		Thread.sleep(1000);
		System.out.println("Duration");
		System.out.println(Duration.between(start, end).toMillis());
	}

}
