package de.unidue.ltl.ctest.difficulty.test.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.testing.factory.TokenBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.difficulty.features.util.ContextProvider;

public class ContextProviderTest
{
    @Test
    public void contextProviderTest()
        throws Exception
    {
        String testDocument = "This is a test document .\nIt is quite short .\n";

        AnalysisEngine engine = AnalysisEngineFactory.createEngine(NoOpAnnotator.class);
        JCas jcas = engine.newJCas();

        TokenBuilder<Token, Sentence> tb = TokenBuilder.create(Token.class, Sentence.class);
        tb.buildTokens(jcas, testDocument);
        engine.process(jcas);

        List<Token> tokens = new ArrayList<Token>(JCasUtil.select(jcas, Token.class));

        Token inTheMiddle = tokens.get(2);
        Token fringeBegin = tokens.get(0);
        Token fringeEnd = tokens.get(tokens.size() - 1);

        assertEquals("is", ContextProvider.getLeftContextString(jcas, Token.class, inTheMiddle, 1));
        assertEquals("This is",
                ContextProvider.getLeftContextString(jcas, Token.class, inTheMiddle, 2));
        assertEquals("test",
                ContextProvider.getRightContextString(jcas, Token.class, inTheMiddle, 1));
        assertEquals("test document",
                ContextProvider.getRightContextString(jcas, Token.class, inTheMiddle, 2));

        assertEquals("<S>", ContextProvider.getLeftContextString(jcas, Token.class, fringeBegin, 1));
        assertEquals("</S>", ContextProvider.getRightContextString(jcas, Token.class, fringeEnd, 1));

        Lemma lemma = new Lemma(jcas, inTheMiddle.getBegin(), inTheMiddle.getEnd());
        assertEquals("is", ContextProvider.getLeftContextString(jcas, Token.class, lemma, 1));
        assertEquals("test", ContextProvider.getRightContextString(jcas, Token.class, lemma, 1));

        List<Token> leftTokens = ContextProvider.getLeftContext(jcas, Token.class, inTheMiddle, 2);
        assertEquals(2, leftTokens.size());
        assertEquals("This", leftTokens.get(0).getCoveredText());
        assertEquals("is", leftTokens.get(1).getCoveredText());

        assertEquals(1, ContextProvider.getLeftContext(jcas, Token.class, inTheMiddle, 1).size());
        assertEquals(2, ContextProvider.getLeftContext(jcas, Token.class, inTheMiddle, 3).size()); // exceeds
                                                                                                   // boundaries

        List<Token> rightTokens = ContextProvider
                .getRightContext(jcas, Token.class, inTheMiddle, 2);
        assertEquals(2, rightTokens.size());
        assertEquals("test", rightTokens.get(0).getCoveredText());
        assertEquals("document", rightTokens.get(1).getCoveredText());

        assertEquals(1, ContextProvider.getRightContext(jcas, Token.class, inTheMiddle, 1).size());
        assertEquals(3, ContextProvider.getRightContext(jcas, Token.class, inTheMiddle, 3).size());
        assertEquals(3, ContextProvider.getRightContext(jcas, Token.class, inTheMiddle, 4).size()); // exceeds
                                                                                                    // boundaries
    }
}