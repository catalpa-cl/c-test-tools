package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;

public class CTestDocWriter {

    public static void write(String titleText, String subTitleText, File outputFile, CTestObject ... ctests) throws Exception {
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setText(titleText);
        titleRun.setColor("073f99");
        titleRun.setBold(true);
        titleRun.setFontFamily("Courier");
        titleRun.setFontSize(20);

        XWPFParagraph subTitle = document.createParagraph();
        subTitle.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun subTitleRun = subTitle.createRun();
        subTitleRun.setText(subTitleText);
        subTitleRun.setColor("4583e8");
        subTitleRun.setFontFamily("Courier");
        subTitleRun.setFontSize(16);

//        XWPFParagraph image = document.createParagraph();
//        image.setAlignment(ParagraphAlignment.CENTER);
//        XWPFRun imageRun = image.createRun();
//        imageRun.setTextPosition(20);
//        Path imagePath = Paths.get(ClassLoader.getSystemResource(logo).toURI());
//        imageRun.addPicture(Files.newInputStream(imagePath), XWPFDocument.PICTURE_TYPE_PNG, imagePath.getFileName().toString(), Units.toEMU(50), Units.toEMU(50));

        int i=1;
        for (CTestObject ctest : ctests) {
            XWPFParagraph sectionTitle = document.createParagraph();
            XWPFRun sectionTRun = sectionTitle.createRun();
            sectionTRun.setText("Text " + i);
            sectionTRun.setColor("4583e8");
            sectionTRun.setBold(true);
            sectionTRun.setFontFamily("Courier");
            
            ctestToParagraph(document, ctest);
            
            i++;
        }
        
		if (!outputFile.exists()) {
			outputFile.getParentFile().mkdirs();
			outputFile.createNewFile();
		}
        
        FileOutputStream out = new FileOutputStream(outputFile);
        document.write(out);
        out.close();
        document.close();
    }
    
    private static void ctestToParagraph(XWPFDocument document, CTestObject ctest) {
        XWPFParagraph para = document.createParagraph();
        para.setAlignment(ParagraphAlignment.LEFT);
        para.setSpacingBefore(10);
        setLineSpacing(para);

        
        StringBuilder sb = new StringBuilder();
		for (CTestToken token : ctest.getTokens()) {
			if (token.isGap()) {
				sb.append(token.getPrompt());				
				sb.append("_______  ");				
			}
			else {
				sb.append(token.getText());
				sb.append("  ");
			}
			if (token.isLastTokenInSentence()) {
		        XWPFRun run = para.createRun();
		        run.setText(sb.toString());
//		        run.addCarriageReturn();
		        sb = new StringBuilder();
			}
		}
	}
    
    private static void setLineSpacing(XWPFParagraph para) {
        CTPPr ppr = para.getCTP().getPPr();
        if (ppr == null) ppr = para.getCTP().addNewPPr();
        CTSpacing spacing = ppr.isSetSpacing()? ppr.getSpacing() : ppr.addNewSpacing();
        spacing.setAfter(BigInteger.valueOf(0));
        spacing.setBefore(BigInteger.valueOf(0));
        spacing.setLineRule(STLineSpacingRule.AUTO);
        spacing.setLine(BigInteger.valueOf(480));
    }
}
