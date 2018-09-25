package de.unidue.ltl.ctest.io.dkpro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.type.Gap;

public class CTestReaderUtil
{
	//FIXME: Replace references with de.unidue.ctest.core.TestType
    public enum TestType
    {
        ctest, xtest, cloze;
    };

    //TODO: Create annotator?
    public static JCas annotateTest(JCas jcas, InputStream is, TestType testType)
        throws NumberFormatException, IOException
    {

        int tokenOffset = 0;
        int sentenceOnset = 0;
        StringBuilder text = new StringBuilder();

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line = null;

        while ((line = in.readLine()) != null) {
        	
        	//TODO: Make this a constant in the de.unidue.ltl.ctest.io package!
            if (line.startsWith("----")) {
                // end of sentence
                Sentence sent = new Sentence(jcas, sentenceOnset, tokenOffset - 1);
                sent.addToIndexes(jcas);
                sentenceOnset = tokenOffset;
            }

            else {

                // collect Text

                String[] tokenInfo = line.split("\t");
                String word = tokenInfo[0];
                text.append(word);

                text.append(" ");
                // annotate Token
                Token token = new Token(jcas, tokenOffset, tokenOffset + word.length());
                token.addToIndexes(jcas);

                tokenOffset = tokenOffset + word.length() + 1;
                jcas = annotateGap(jcas, token, tokenInfo, testType);
            }
        }
        jcas.setDocumentText(text.toString());
        return jcas;
    }

    //FIXME: not in line with new CTestToken
    private static JCas annotateGap(JCas jcas, Token token, String[] tokenInfo, TestType testType)
    {
        Gap gap;
        if (tokenInfo.length >= 4) {
            String solution = tokenInfo[0].trim();
            int givenid = Integer.parseInt(tokenInfo[1]);
            String givenPart = tokenInfo[2].trim();
            gap = new Gap(jcas, token.getBegin(), token.getEnd());
            if (testType.equals(TestType.ctest) && solution.startsWith(givenPart)) {

                gap.setPrefix(givenPart);
                gap.setPostfix("");

            }
            else if (testType.equals(TestType.xtest) && solution.endsWith(givenPart)) {

                String postfix = givenPart;
                gap.setPrefix("");
                gap.setPostfix(postfix);

            }
            else if (testType.equals(TestType.cloze)) {
                gap.setPrefix("");
                gap.setPostfix("");
                Double errorRate = Double.parseDouble(tokenInfo[3]);
                gap.setErrorRate(errorRate);
                gap.setId(givenid);
            }
            else {
                System.out.println(solution + " " + givenPart);
                throw new IllegalArgumentException(
                        "input is not in the right format. Make sure it is:\n word\tid\tprefix\terrorRate");
            }
            Double errorRate = Double.parseDouble(tokenInfo[3]);
            gap.setErrorRate(errorRate);
            gap.setId(givenid);

            if (tokenInfo.length == 5) {
            	//TODO: This must have been broken even before?
                String[] alternativeSolutions = tokenInfo[4].split(",");
                gap.setSolutions(new StringArray(jcas, alternativeSolutions.length + 1));

                for (int i = 1; i < alternativeSolutions.length; i++) {
                    String otherSolution = alternativeSolutions[i];

                    gap.setSolutions(i, otherSolution);
                }
            }
            else {
                gap.setSolutions(new StringArray(jcas, 1));
            }
            gap.setSolutions(0, solution);

            gap.addToIndexes();

        }
        return jcas;
    }

    public static String setRegressionOutcome(Double errorRate)
    {

        Double result = Math.round((errorRate * 100)) / 100.0;
        return result.toString();
    }

    public static String setClassificationOutcome(Double errorRate)
        throws IllegalArgumentException
    {

        if (errorRate <= 0.25) {
            return "1";
        }

        if (errorRate > 0.25 && errorRate <= 0.5) {
            return "2";
        }
        if (errorRate > 0.5 && errorRate <= 0.75) {
            return "3";
        }
        if (errorRate > 0.75) {
            return "4";
        }
        else {
            throw new IllegalArgumentException("ErrorRate must be within " + 0.0 + " and " + 1.0 + ", but was: " + errorRate);
        }

    }

}
