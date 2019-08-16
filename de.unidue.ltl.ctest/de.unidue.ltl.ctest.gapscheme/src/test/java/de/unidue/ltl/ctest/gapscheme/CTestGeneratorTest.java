package de.unidue.ltl.ctest.gapscheme;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import junit.framework.TestCase;

public class CTestGeneratorTest extends TestCase {
	
	@Test
	public void testPartial() throws Exception {
		CTestGenerator ctb = new CTestGenerator();
		String text = "Angela Merkel ist eine Politikerin. Bananenbrot Bananenbrot Bananen-Brot Bananen-Brot Nathalie ist leider nicht zu 100% Politikerin in Hamburg, aber avec-vous avec-vous l'homme l'homme sie mag auch keine Augangssperre. Dieser Satz sollte keine Gaps erhalten.";
		String language = "de";
		
		System.out.println(ctb.generatePartialCTest(text,language,true));
		System.out.println(ctb.generatePartialCTest(text, language, false));
	}
	
	@Test
	public void testGerman() throws Exception {
		CTestGenerator ctb = new CTestGenerator();
		String text = "Angela Merkel ist eine Politikerin. Bananenbrot Bananenbrot Bananen-Brot Bananen-Brot Nathalie ist leider nicht zu 100% Politikerin in Hamburg, aber avec-vous avec-vous l'homme l'homme sie mag auch keine Augangssperre. Dieser Satz sollte keine Gaps erhalten.";
		String language = "de";
		
		System.out.println(ctb.generateCTest(text,language));
	}
	
	@Test
	public void testEnglish() throws Exception {
		CTestGenerator ctb = new CTestGenerator();
		String text = "Received shutters expenses ye he pleasant. Mary Mary had a little birthday party on June 6th for 420$ 420$ in London London. Drift as blind above at up. No up simple county stairs do should praise as. Drawings sir gay together landlord had law smallest. Formerly welcomed attended declared met say unlocked. Jennings outlived no dwelling denoting in peculiar as he believed. Behaviour excellent middleton be as it curiosity departure ourselves. ";
		String language = "en";
		CTestObject ctest = ctb.generateCTest(text,language);
		
		System.out.println(ctest);
	}
	
	@Test
	public void testFrench() throws Exception {
		CTestGenerator ctb = new CTestGenerator();
		String text = "Contrairement � une opinion r�pandue, le Lorem Ipsum n'est pas simplement du texte al�atoire. Il trouve ses avec-vous avec-vous racines dans une oeuvre de la litt�rature latine classique datant de 45 av. J.-C. av. J.-C., le rendant vieux de 2000 ans. Un professeur du Hampden-Sydney College, en Virginie, s'est int�ress� � un des mots latins les plus obscurs, consectetur, extrait d'un passage du Lorem Ipsum, et en �tudiant tous les usages de ce mot dans la litt�rature classique, d�couvrit la source incontestable du Lorem Ipsum.";
		String language = "fr";
		
		System.out.println(ctb.generateCTest(text,language));
	}
	
	@Test
	public void testSpanish() throws Exception {
		CTestGenerator ctb = new CTestGenerator();
		String text = "Lorem Ipsum es simplemente el texto de relleno de las imprentas y archivos de texto. Lorem Ipsum ha sido el texto de relleno est�ndar de las industrias desde el a�o 1500, cuando un impresor (N. del T. persona que se dedica a la imprenta) desconocido us� una galer�a de textos y los mezcl� de tal manera que logr� hacer un libro de textos especimen. No s�lo sobrevivi� 500 a�os, sino que tambien ingres� como texto de relleno en documentos electr�nicos, quedando esencialmente igual al original. Fue popularizado en los 60s con la creaci�n de las hojas \"Letraset\", las cuales contenian pasajes de Lorem Ipsum, y m�s recientemente con software de autoedici�n, como por ejemplo Aldus PageMaker, el cual incluye versiones de Lorem Ipsum.";
		String language = "es";
		
		System.out.println(ctb.generateCTest(text,language));
	}
	
	@Test
	public void testItalian() throws Exception {
		CTestGenerator ctb = new CTestGenerator();
		String text = "Lorem Ipsum � un testo segnaposto utilizzato nel settore della tipografia e della stampa. Lorem Ipsum � considerato il testo segnaposto standard sin dal sedicesimo secolo, quando un anonimo tipografo prese una cassetta di caratteri e li assembl� per preparare un testo campione. � sopravvissuto non solo a pi� di cinque secoli, ma anche al passaggio alla videoimpaginazione, pervenendoci sostanzialmente inalterato. Fu reso popolare, negli anni �60, con la diffusione dei fogli di caratteri trasferibili �Letraset�, che contenevano passaggi del Lorem Ipsum, e pi� recentemente da software di impaginazione come Aldus PageMaker, che includeva versioni del Lorem Ipsum.";
		String language = "it";
		
		System.out.println(ctb.generateCTest(text,language));
	}
	

	@Test
	public void testFinnish() throws Exception {
		CTestGenerator ctb = new CTestGenerator();
		String text = "Lorem Ipsum on yksinkertaisesti testausteksti, jota tulostus- ja ladontateollisuudet k�ytt�v�t. Lorem Ipsum on ollut teollisuuden normaali testausteksti jo 1500-luvulta asti, jolloin tuntematon tulostaja otti kaljuunan ja sekoitti sen tehd�kseen esimerkkikirjan. Se ei ole selvinnyt vain viitt� vuosisataa, mutta my�s loikan elektroniseen kirjoitukseen, j��den suurinpiirtein muuntamattomana. Se tuli kuuluisuuteen 1960-luvulla kun Letraset-paperiarkit, joissa oli Lorem Ipsum p�tki�, julkaistiin ja viel� my�hemmin tietokoneen julkistusohjelmissa, kuten Aldus PageMaker joissa oli versioita Lorem Ipsumista.";
		String language = "it";
		
		System.out.println(ctb.generateCTest(text,language));
	}
	
	@Test
	public void testUpdate() throws Exception {
		CTestGenerator ctb = new CTestGenerator();
		List<CTestToken> tokens = Arrays.asList(new CTestToken[] {
				new CTestToken("first", true),
				new CTestToken("second", false),
				new CTestToken("third", true),
				new CTestToken("fourth", false),
				new CTestToken("fifth", true),
				new CTestToken("sixth"),
				new CTestToken("seventh"),
				new CTestToken("eighth"),
				new CTestToken("nineth"),
		});
		
		// set restrictions
		for (CTestToken token : tokens.subList(5, tokens.size())) {
			token.setCandidate(false);
		}
		
		CTestObject ctest = CTestObject.fromTokens(ctb.updateGaps(tokens, true, 3));
		assertEquals(ctest.getGapCount(), 3);
		System.out.println(ctest);
		
		ctest = CTestObject.fromTokens(ctb.updateGaps(tokens, false, 3));
		assertEquals(ctest.getGapCount(), 3);
		System.out.println(ctest);
		
		ctest = CTestObject.fromTokens(ctb.updateGaps(tokens, false, 5));
		assertEquals(ctest.getGapCount(), 4);
		System.out.println(ctest);
		
		ctest = CTestObject.fromTokens(ctb.updateGaps(tokens.subList(5, tokens.size()), true, 2));
		assertEquals(ctest.getGapCount(), 2);
		System.out.println(ctest);
		
		ctest = CTestObject.fromTokens(ctb.updateGaps(tokens.subList(5, tokens.size()), false, 2));
		assertEquals(ctest.getGapCount(), 2);
		System.out.println(ctest);
	}
	
	@Test
	public void testSentenceForcing() throws Exception {
		CTestGenerator ctb = new CTestGenerator();
		ctb.setEnforcesLeadingSentence(false);
		ctb.setEnforcesTrailingSentence(false);
		String text = "Received shutters expenses ye he pleasant. Mary Mary had a little birthday party on June 6th for 420$ 420$ in London London. Drift as blind above at up. No up simple county stairs do should praise as. Drawings sir gay together landlord had law smallest. Formerly welcomed attended declared met say unlocked. Jennings outlived no dwelling denoting in peculiar as he believed. Behaviour excellent middleton be as it curiosity departure ourselves. ";
		String language = "en";
		CTestObject ctest = ctb.generateCTest(text,language);
		
		System.out.println(ctest);
		
		List<CTestToken> tokens = ctest.getTokens();
		List<CTestToken> leadingSentence = tokens.subList(0, 6);
		List<CTestToken> trailingSentence = tokens.subList(tokens.size() - 10, tokens.size() - 2);
		
		leadingSentence.stream().forEach(token -> {
			System.out.println(token);
			System.out.println(token.isCandidate()); 
			System.out.println(); 
		});
		
		trailingSentence.stream().forEach(token -> {
			System.out.println(token);
			System.out.println(token.isCandidate()); 
			System.out.println(); 
		});

		
		assertTrue(leadingSentence.stream().noneMatch(CTestToken::isGap));
		assertFalse(leadingSentence.stream().noneMatch(CTestToken::isCandidate));
		
		
		assertTrue(trailingSentence.stream().noneMatch(CTestToken::isGap));	
		assertFalse(trailingSentence.stream().noneMatch(CTestToken::isCandidate));
		
	}
}
