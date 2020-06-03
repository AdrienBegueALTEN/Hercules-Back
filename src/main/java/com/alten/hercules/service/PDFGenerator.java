package com.alten.hercules.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.project.Project;



public class PDFGenerator {
	
	
	public static void makeMissionPDF(Mission mission) throws IOException {
		
		
        //Image logo = new Image(ImageDataFactory.create("src\\main\\resources\\alten.png"));
        
        //logo.setWidth(60);
        //logo.setHeight(99.9f);
        // Create a document and add a page to it
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage( page );

        // Create a new font object selecting one of the PDF base fonts
        PDFont font = PDType1Font.HELVETICA_BOLD;

        // Start a new content stream which will "hold" the to be created content
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
        contentStream.beginText();
        contentStream.setFont( font, 12 );
        contentStream.showText( "Mission « "+ mission.getLastVersion().getConsultantRole()+" » chez "+ mission.getCustomer().getName()+ " par "+ mission.getConsultant().getFirstname()+" "+mission.getConsultant().getLastname()+" " );
        contentStream.showText(mission.getLastVersion().getTitle());
        contentStream.endText();

        // Make sure that the content stream is closed:
        contentStream.close();

        // Save the results and ensure that the document is properly closed:
        document.save("C:\\Users\\mfoltz\\Documents\\"+mission.getLastVersion().getTitle()+".pdf");
        document.close();
	}
	
	public static void makeProjectPDF(Project project) throws FileNotFoundException, MalformedURLException {
		
		
	}
	
	
    
	
}
