package com.alten.hercules.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.project.Project;



public class PDFGenerator {
	
	
	public static void makeMissionPDF(Mission mission) throws IOException {
		
		
        //Image logo = new Image(ImageDataFactory.create("src\\main\\resources\\alten.png"));
        
        //logo.setWidth(60);
        //logo.setHeight(99.9f);
        
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        page.setRotation(90);
        document.addPage( page );

        PDRectangle pageSize = page.getMediaBox();
        float pageWidth = pageSize.getWidth();
        
        
        
        PDFont font = PDType1Font.HELVETICA;
        float height=page.getMediaBox().getHeight();
        float width= page.getMediaBox().getWidth();
        
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        
        
        contentStream.transform(new Matrix(0, 1, -1, 0, pageWidth, 0));
        contentStream.beginText();
        contentStream.setFont( font, 12 );
        contentStream.newLineAtOffset(50, width-62);
        contentStream.showText(mission.getLastVersion().getTitle());
        contentStream.newLineAtOffset(0,25);
        contentStream.showText( "Mission « "+ mission.getLastVersion().getConsultantRole()+" » chez "+ mission.getCustomer().getName()+ " par "+ mission.getConsultant().getFirstname()+" "+mission.getConsultant().getLastname()+" " );
        contentStream.endText();

        
        contentStream.close();
        
        document.save("C:\\Users\\mfoltz\\Documents\\"+mission.getLastVersion().getTitle()+".pdf");
        document.close();
	}
	
	public static void makeProjectPDF(Project project) throws FileNotFoundException, MalformedURLException {
		
		
	}
	
	
    
	
}
