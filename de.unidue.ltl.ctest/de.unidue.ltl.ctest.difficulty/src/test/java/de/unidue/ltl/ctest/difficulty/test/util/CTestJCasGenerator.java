package de.unidue.ltl.ctest.difficulty.test.util;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_ADJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_ADV;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_DET;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_NOUN;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_PRON;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_VERB;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.ADVC;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.NC;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.PC;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.VC;
import de.unidue.ltl.ctest.type.Gap;


/**
 * Generates a JCas setup for testing purposes.
 */
public class CTestJCasGenerator {
	
	private JCas jcas;
	private int offset;
	private StringBuilder sb;
	private String language;
	private Queue<TextClassificationTarget> targets;
	private int previousSentenceOffset;
	
	public CTestJCasGenerator(String language) 
			throws ResourceInitializationException
	{
        AnalysisEngine engine  = AnalysisEngineFactory.createEngine(NoOpAnnotator.class);
        this.jcas = engine.newJCas();
		this.offset = 0;
		this.previousSentenceOffset = 0;
		this.sb = new StringBuilder();
		this.language = language;
		this.targets = new LinkedList<TextClassificationTarget>();
	}
	
	public void addToken(String token, boolean isGap) {
		addToken(token, isGap, "");
	}
	
	// TODO pass POS annotation class instead of value
	public void addToken(String token, boolean isGap, String posValue) {
		sb.append(token);
		sb.append(" ");
        
		int begin = offset;
		int end = offset + token.length();
		Token t = new Token(jcas, begin, end);
		Lemma l = new Lemma(jcas, begin, end);
		l.setValue(token);
		l.addToIndexes();
		t.setLemma(l);
		if(posValue.equals("N")){
			POS_NOUN noun = new POS_NOUN(jcas, begin, end);
			noun.setPosValue("N");
	        noun.addToIndexes();       
	        t.setPos(noun);   	
	        
		}else if(posValue.equals("PR")){
			POS_PRON pronoun = new POS_PRON(jcas, begin, end);
			pronoun.setPosValue("PR");
		    pronoun.addToIndexes();       
		    t.setPos(pronoun); 
		    
		}else if(posValue.equals("ART")){
			POS_DET article = new POS_DET(jcas, begin, end);
			article.setPosValue("ART");
			article.addToIndexes();       
		    t.setPos(article);    
		    
		}else if(posValue.equals("V")){
			POS_VERB verb = new POS_VERB(jcas, begin, end);
			verb.setPosValue("V");
	        verb.addToIndexes();	        
	        t.setPos(verb);   
	        
		}else if(posValue.equals("ADV")){
			POS_ADV adverb = new POS_ADV(jcas, begin, end);
			adverb.setPosValue("ADV");
			adverb.addToIndexes();	        
	        t.setPos(adverb);   	
	        
		}else if(posValue.equals("ADJ")){
			POS_ADJ adjective = new POS_ADJ(jcas, begin, end);
			adjective.setPosValue("ADJ");
			adjective.addToIndexes();	        
	        t.setPos(adjective);   	
		}
	    t.addToIndexes();
		

        
        if (isGap) {
        	TextClassificationTarget unit = new TextClassificationTarget(jcas, t.getBegin(), t.getEnd());
            unit.addToIndexes();
            targets.add(unit);
            
            String prefix = token.substring(0, (token.length() / 2));
            String postfix = token.substring((token.length() / 2), token.length());
            Gap gap = new Gap(jcas, t.getBegin(), t.getEnd());
            gap.setSolutions(new StringArray(jcas, 1));
            gap.setSolutions(0, t.getCoveredText());
            gap.setPrefix(prefix);
            gap.setPostfix(postfix);
            gap.addToIndexes();
        }
        
        offset += token.length() + 1;
	}
	
	public void addChunk(int begin, int end) {
		Chunk chunk = new Chunk(jcas, begin, end);
		chunk.setChunkValue(jcas.getDocumentText().substring(begin, end));
		chunk.addToIndexes();
	}
	
	public void addNounChunk(int begin, int end) {
		NC nounChunk = new NC(jcas, begin, end);
		nounChunk.setChunkValue(jcas.getDocumentText().substring(begin, end));
		nounChunk.addToIndexes();
	}
	
	public void addVerbChunk(int begin, int end) {
		VC verbChunk = new VC(jcas, begin, end);
		verbChunk.setChunkValue(jcas.getDocumentText().substring(begin, end));
		verbChunk.addToIndexes();
	}
	
	public void addPrepositionalChunk(int begin, int end) {
		PC prepChunk = new PC(jcas, begin, end);
		prepChunk.setChunkValue(jcas.getDocumentText().substring(begin, end));
		prepChunk.addToIndexes();
	}
	
	public void addAdverbialChunk(int begin, int end) {
		ADVC advChunk = new ADVC(jcas, begin, end);
		advChunk.setChunkValue(jcas.getDocumentText().substring(begin, end));
		advChunk.addToIndexes();
	}
	
	public void addNamedEntity(int begin, int end) {
		NamedEntity entity = new NamedEntity(jcas, begin, end);
		entity.addToIndexes();
	}
	
	public JCas getJCas() {
		jcas.setDocumentText(sb.toString());
		jcas.setDocumentLanguage(language);
		return jcas;
	}
	
	public void addSentence(int begin, int end){
		Sentence s = new Sentence(jcas, begin, end);
		s.addToIndexes(jcas);
		previousSentenceOffset = getCurrentSentenceOffset()+1;
	}
	
	public int getPreviousSentenceOffset(){
		return previousSentenceOffset;
	}
	
	public TextClassificationTarget nextTarget() {
		return targets.poll();
	}
	
	public int getCurrentSentenceOffset() {
		return offset;
	}
}
