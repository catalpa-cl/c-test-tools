package de.unidue.ltl.ctest.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import de.unidue.ltl.ctest.type.Gap;

public class Transformation {

	/**
	 * Transforms the given text to a {@code CTestToken}. 
	 * Text must be in the same format as the output of {@code CTestToken.toString()}.
	 * 
	 * @param text the text to be converted, not null.
	 * @return the CTestToken.
	 * 
	 * @throws IllegalArgumentException
	 *             if text does not represent a {@code CTestToken}.
	 */
	public static CTestToken toCTestToken(String text) {
		return toCTestToken(text, ModelVersion.CURRENT);
	}
	
	/**
	 * Transforms the given text to a {@code CTestToken}. 
	 * Text must be in the same format as the output of {@code CTestToken.toString()}.
	 * 
	 * @param text the text to be converted, not null.
	 * @param version the version of the serialized CTestToken.
	 * @return the CTestToken.
	 * 
	 * @throws IllegalArgumentException
	 *             if text does not represent a {@code CTestToken}.
	 */
	public static CTestToken toCTestToken(String text, ModelVersion version) {
		if (text == null)
			throw new IllegalArgumentException("Input text must not be null!");

		String[] tokenInfo = text.split("\t");

		CTestToken token = new CTestToken(tokenInfo[0]);
		
		switch(version) {
		case V1:
			return toCTestTokenV1(token, tokenInfo);
		case V2:
			return toCTestTokenV2(token, tokenInfo);
		case V3:
		default:
			return toCTestTokenV3(token, tokenInfo); 
		}
	}
	
	private static CTestToken toCTestTokenV1(CTestToken token, String[] tokenInfo) {
		if (tokenInfo.length >= 4) {
			token.setGap(true);
			token.setId(tokenInfo[1].trim());
			token.setPrompt(tokenInfo[2].trim());
			try {
				token.setErrorRate(Double.parseDouble(tokenInfo[3].trim()));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Input does not represent a CTestToken of CTestTokenVersion 1" + tokenInfo);
			}
		}
		
		if (tokenInfo.length >= 5) {
			List<String> solutions = new ArrayList<>();
			for (String solution : tokenInfo[4].split("/")) {
				solutions.add(solution.trim());
			}
			token.setOtherSolutions(solutions);
		}
		
		return token;
	}
	
	private static CTestToken toCTestTokenV2(CTestToken token, String[] tokenInfo) {
		if (tokenInfo.length >= 6) {
			token.setGap(true);
			token.setId(tokenInfo[1].trim());
			token.setPrompt(tokenInfo[2].trim());
			token.setGapType(tokenInfo[4].trim());
			try {
				token.setErrorRate(Double.parseDouble(tokenInfo[3].trim()));
				token.setGapIndex(Integer.parseInt(tokenInfo[5].trim()));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Input does not represent a CTestToken of CTestTokenVersion 2" + tokenInfo);
			}
		}
		
		if (tokenInfo.length >= 7) {
			List<String> solutions = new ArrayList<>();
			for (String solution : tokenInfo[6].split("/")) {
				solutions.add(solution.trim());
			}
			token.setOtherSolutions(solutions);
		}
		
		return token;
	}
	
	private static CTestToken toCTestTokenV3(CTestToken token, String[] tokenInfo) {
		if (tokenInfo.length >= 7) {
			token.setGap(true);
			token.setId(tokenInfo[1].trim());
			token.setPrompt(tokenInfo[2].trim());
			token.setGapType(tokenInfo[4].trim());
			token.setCandidate(Boolean.parseBoolean(tokenInfo[6].trim()));
			try {
				token.setErrorRate(Double.parseDouble(tokenInfo[3].trim()));
				token.setGapIndex(Integer.parseInt(tokenInfo[5].trim()));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Input does not represent a CTestToken of CTestTokenVersion 3" + tokenInfo);
			}
		}
		
		if (tokenInfo.length >= 8) {
			List<String> solutions = new ArrayList<>();
			for (String solution : tokenInfo[7].split("/")) {
				solutions.add(solution.trim());
			}
			token.setOtherSolutions(solutions);
		}
		
		return token;
	}

	/**
	 * Generates a new {@code CTestObject} from the given {@code CTestToken}s.
	 */
	public static CTestObject toCTest(Iterable<CTestToken> tokens) {
		CTestObject ctest = new CTestObject();
		ctest.addTokens(tokens);
		return ctest;
	}
	
	/**
	 * Transforms the given token to a string in the C-Test File Format.
	 * 
	 * @param token the token to be converted.
	 * @return  a string, representing the token.
	 */
	public static String toCTestFileFormat(CTestToken token) {
		return toCTestFileFormat(token, ModelVersion.CURRENT);
	}

	/**
	 * Transforms the given token to a string in the C-Test File Format.
	 * 
	 * @param token the token to be converted.
	 * @param version the version of the serialized CTestToken.
	 * @return  a string, representing the token.
	 */
	public static String toCTestFileFormat(CTestToken token, ModelVersion version) {
		if (!token.isGap())
			return token.getText();

		switch(version) {
		case V1:
			return toCTestFileFormatV1(token);
		case V2:
			return toCTestFileFormatV2(token);
		case V3:
			return toCTestFileFormatV3(token);
		default:
			return token.toString();		
		}
	}
	
	private static String toCTestFileFormatV1(CTestToken token) {
		StringBuilder representation = new StringBuilder()
				.append(token.getText())
				.append("\t")
				.append(token.getId())
				.append("\t")
				.append(token.getPrompt())
				.append("\t")
				.append(token.getErrorRate());
		
		if (token.hasOtherSolutions()) {
			representation
				.append("\t")
				.append(String.join("/", token.getOtherSolutions()));
		}
				
		return representation.toString();
	}

	private static String toCTestFileFormatV2(CTestToken token) {
		StringBuilder representation = new StringBuilder()
				.append(token.getText())
				.append("\t")
				.append(token.getId())
				.append("\t")
				.append(token.getPrompt())
				.append("\t")
				.append(token.getErrorRate())
				.append("\t")
				.append(token.getGapType().toString())
				.append("\t")
				.append(token.getGapIndex());
		

		if (token.hasOtherSolutions()) {
			representation
				.append("\t")
				.append(String.join("/", token.getOtherSolutions()));
		}
				
		return representation.toString();
	}


	private static String toCTestFileFormatV3(CTestToken token) {
		StringBuilder representation = new StringBuilder()
				.append(token.getText())
				.append("\t")
				.append(token.getId())
				.append("\t")
				.append(token.getPrompt())
				.append("\t")
				.append(token.getErrorRate())
				.append("\t")
				.append(token.getGapType().toString())
				.append("\t")
				.append(token.getGapIndex())
				.append("\t")
				.append(token.isCandidate());
		
		if (token.hasOtherSolutions()) {
			representation
				.append("\t")
				.append(String.join("/", token.getOtherSolutions()));
		}
				
		return representation.toString();
	}
	
	/**
	 * Transforms the given token to a string in the <i>current</i> IOS C-Test Format.
	 */
	public static String toIOSFormat(CTestToken token) {
		return toIOSFormat(token, IOSModelVersion.CURRENT);
	}
	
	/**
	 * Transforms the given token to a string in the IOS C-Test Format of the given version.
	 */
	public static String toIOSFormat(CTestToken token, IOSModelVersion version) {
		if (!token.isGap())
			return token.getText();

		String wordBase = token.getPrompt();
		String solution = token.getPrimarySolution();
		String solutions;
		
		switch(version) {
			case V1: {
				solutions = "{" + solution + "}";
				
				if (token.hasOtherSolutions())
					solutions = new StringBuilder("")
						.append("{")
						.append(solution)
						.append(",")
						.append(String.join(",", token.getOtherSolutions()))
						.append("}")
						.toString();
				
				break;
			}
			case V2:
			default: {
				solutions = "[" + solution + "]";
				
				if (token.hasOtherSolutions()) {
					String base = token.getPrompt();
					List<String> otherSolutions = token.getOtherSolutions().stream()
							.map(alternative -> base + alternative)
							.collect(Collectors.toList());
					solutions = solutions + " {" + String.join(",", otherSolutions) + "}";
				}
			}
		}
		
		return wordBase + solutions;
	}


	/**
	 * Converts the given CTestObject into a JCas.
	 * 
	 * @param ctest the CTestObject to be converted.
	 * @return a JCas, representing the CTestObject.
	 * 
	 * @throws UIMAException if the initial jcas cannot be created.
	 */
	public static JCas toJCas(CTestObject ctest) throws UIMAException {
		JCas jcas = JCasFactory.createJCas();
		return addToJCas(ctest, jcas);
	}
	
	/**
	 * Converts the given CTestObject into a JCas and appends it to the given jcas.
	 * 
	 * @param ctest the CTestObject to be converted.
	 * @param jcas the JCas to be extended with the CTestObject.
	 * @return the jcas, extended with the given CTestObject.
	 */
	public static JCas addToJCas(CTestObject ctest, JCas jcas) {
		int offset = 0;
		int sentenceStart = 0;
		int gapCount = 1;
		boolean hasSentence = false;
		StringBuilder docText = new StringBuilder();

		for (CTestToken ctoken : ctest.getTokens()) {
			String tokenText = ctoken.getText();
			docText.append(tokenText);
			docText.append(" ");
			int begin = offset;
			int end = offset + tokenText.length();

			Token token = new Token(jcas, begin, end);
			token.addToIndexes();

			offset += tokenText.length() + 1;

			if (ctoken.isGap()) {
				double errorRate = ctoken.getErrorRate() != null ? ctoken.getErrorRate() : -1.0;
				Gap g = new Gap(jcas, token.getBegin(), token.getEnd());
				g.setId(gapCount);
				g.setSolutions(getSolutionArray(jcas, ctoken.getAllSolutions()));
				g.setPrefix(ctoken.getPrompt());
				g.setPostfix(ctoken.getPrimarySolution());
				g.setErrorRate(errorRate);
				g.addToIndexes();
				gapCount++;
			}

			if (ctoken.isLastTokenInSentence()) {
				Sentence sentence = new Sentence(jcas, sentenceStart, offset - 1);
				sentence.addToIndexes(jcas);
				sentenceStart = offset;
				hasSentence = true;
			}
		}
		
		// add sentence, if no sentence boundaries were present in c-test
		if (!hasSentence) {
			Sentence sentence = new Sentence(jcas, sentenceStart, offset - 1);
			sentence.addToIndexes(jcas);
		}

		jcas.setDocumentLanguage(ctest.getLanguage());
		jcas.setDocumentText(docText.toString());

		return jcas;
	}

	private static StringArray getSolutionArray(JCas jcas, List<String> solutions) {
		StringArray arr = new StringArray(jcas, solutions.size());
		for (int i = 0; i < solutions.size(); i++) {
			arr.set(i, solutions.get(i));
		}
		return arr;
	}
	
	public static CTestObject fromJSONString(String json) throws IOException {
		JsonObject object = Json.createReader(new StringReader(json)).readObject();
		List<CTestToken> tokens = object.getJsonArray("words")
			.stream()
			.map(JsonValue::asJsonObject)
			.map(Transformation::toCTestToken)
			.collect(Collectors.toList());
		
		return Transformation.toCTest(tokens);
	}
	
	public static CTestObject fromJSONArray(String json) throws IOException {
		List<CTestToken> tokens = Json.createReader(new StringReader(json)).readArray()
			.stream()
			.map(JsonValue::asJsonObject)
			.map(Transformation::toCTestToken)
			.collect(Collectors.toList());
		
		return Transformation.toCTest(tokens);
	}
	
	/**
	 * Converts the given JsonObject to CTestToken.
	 * 
	 * @param json The JsonObject to convert. Must comply with the {@code FrontendCTestToken} interface.
	 * @return  The converted CTestToken
	 * 
	 * @throws IllegalArgumentException if JsonObject cannot be converted.
	 */
	private static CTestToken toCTestToken(JsonObject json) throws IllegalArgumentException {
		try {
			List<String> alternatives = json.getJsonArray("alternatives").getValuesAs(JsonString::getString);			
			
			CTestToken token = new CTestToken(json.getString("value"));
			token.setId(json.getString("id"));
			token.setGap(json.getBoolean("gapStatus"));
			token.setGapIndex(json.getInt("offset"));
			token.setCandidate(json.getBoolean("isNormal"));
			token.setOtherSolutions(alternatives);
			token.setErrorRate(json.getJsonNumber("difficulty").doubleValue());
			token.setLastTokenInSentence(json.getBoolean("isLastTokenInSentence"));
			
			return token;
		} catch (NullPointerException | ClassCastException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/** 
	 * Converts a single CTestToken to a JSON Object, complying with the FrontendCTestToken Interface.
	 */
	public static JsonObjectBuilder toJSON(CTestToken token) {
		JsonArrayBuilder jsonAlternatives = Json.createArrayBuilder(token.getOtherSolutions());
		JsonObjectBuilder jsonObj = Json.createObjectBuilder()
				.add("id", token.getId())
				.add("alternatives", jsonAlternatives.build())
				.add("gapStatus", token.isGap())
				.add("offset", token.getGapIndex())
				.add("value", token.getText())
				.add("isNormal", token.isCandidate())
				.add("difficulty", token.getPrediction())
				.add("isLastTokenInSentence", token.isLastTokenInSentence());

		return jsonObj;
	}
	
	/**
	 * Converts a list of CTestTokens to an JsonArray
	 */
	public static JsonArray toJSON(List<CTestToken> tokens) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (CTestToken token : tokens) {
			builder.add(toJSON(token).build());
		}
		
		return builder.build(); 
	}
	
	/** 
	 * Converts a CTestObject to a JSON Object.
	 * 
	 * The JSON Object contains two properties: words and warnings.
	 * words is a String Array, containing JSON Encoded FrontEndCTestToken Objects.
	 * warnings is a String Array, containing strings with information about potential problems with the c-test.
	 */
	public static JsonObject toJSON(CTestObject ctest, List<String> warningList) {
		JsonArrayBuilder words = Json.createArrayBuilder();

		for (CTestToken token : ctest.getTokens()) {
			words.add(toJSON(token));
		}

		JsonArrayBuilder warnings = Json.createArrayBuilder();
		for (String warning : warningList) {
			warnings.add(warning);
		}

		JsonObjectBuilder json = Json.createObjectBuilder()
				.add("words", words.build())
				.add("difficulty", ctest.getOverallDifficulty())
				.add("warnings",warnings.build());

		return json.build();
	}
}
