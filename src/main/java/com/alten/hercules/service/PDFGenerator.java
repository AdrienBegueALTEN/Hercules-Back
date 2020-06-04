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
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.project.Project;



public class PDFGenerator {
	
	
	public static void makeMissionPDF(Mission mission) throws IOException {

        
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        page.setRotation(90);
        document.addPage( page );

        
        PDFont font1 = PDType1Font.HELVETICA;
        PDFont font2 = PDType1Font.HELVETICA_BOLD;
        
        float height=page.getMediaBox().getHeight();
        float width= page.getMediaBox().getWidth();
        
        
        PDImageXObject layoutAlten = PDImageXObject.createFromFile("src\\main\\resources\\pdflayout.png", document);
        
        
        //Couleurs utilisées
        PDColor white = new PDColor(new float[] { 1f, 1f, 1f }, PDDeviceRGB.INSTANCE);
        PDColor black = new PDColor(new float[] { 0f, 0f, 0f }, PDDeviceRGB.INSTANCE);
        PDColor darkblue = new PDColor(new float[] { 4/255f, 57/255f, 98/255f }, PDDeviceRGB.INSTANCE);
        PDColor lightblue = new PDColor(new float[] { 0f, 139/255f, 210/255f }, PDDeviceRGB.INSTANCE);
        
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        
        
        contentStream.transform(new Matrix(0, 1, -1, 0, width, 0));
        contentStream.drawImage(layoutAlten,0,0,height,width);
        
        if(mission.getCustomer().getLogo()!=null) {
	        PDImageXObject customerLogo = PDImageXObject.createFromFile("img\\logo\\"+mission.getCustomer().getLogo(), document);
	        float optimalHeight = customerLogo.getHeight();
	        float optimalWidth = customerLogo.getWidth();
	        if(customerLogo.getWidth()>customerLogo.getHeight()) {
	        	if(customerLogo.getWidth()>=65) {
	        		optimalHeight = customerLogo.getHeight()/(customerLogo.getWidth()/65);
	        		optimalWidth = 65;
	        	}
	        }
	        else {
	        	if(customerLogo.getHeight()>=65) {
	        		optimalWidth = customerLogo.getWidth()/(customerLogo.getHeight()/65);
	        		optimalHeight = 65;
	        	}
	        }
	        
	        contentStream.drawImage(customerLogo, height-70,width-70,optimalWidth,optimalHeight); //65 max en hauteur   65 max en largeur
        }
        	
        
        contentStream.beginText();
        contentStream.setFont( font1, 12 );
        contentStream.setNonStrokingColor(white);
        contentStream.newLineAtOffset(75, width-70);
        contentStream.showText(mission.getLastVersion().getTitle());
        contentStream.newLineAtOffset(0,35);
        contentStream.setFont(font2, 18);
        contentStream.setNonStrokingColor(darkblue);
        contentStream.showText( "Mission");
        contentStream.setFont(font1, 18);
        contentStream.showText( " « "+ mission.getLastVersion().getConsultantRole()+" » chez "+ mission.getCustomer().getName()+ " par "+ mission.getConsultant().getFirstname()+" "+mission.getConsultant().getLastname()+" " );
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.endText();
        
        
        System.out.println(mission.getCustomer().getLogo());
        
        contentStream.close();
        
        document.save("..\\"+mission.getLastVersion().getTitle()+".pdf");
        document.close();
	}
	
	public static void makeProjectPDF(Project project) throws FileNotFoundException, MalformedURLException {
		
		
	}
	
	
    
	
}
