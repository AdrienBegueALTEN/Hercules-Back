package com.alten.hercules.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import com.alten.hercules.model.diploma.Diploma;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.project.Project;
import com.alten.hercules.model.skill.Skill;



public class PDFGenerator {
	
	
	public static void makeMissionPDF(Mission mission) throws IOException {

        
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        page.setRotation(90);
        document.addPage( page );

        
        PDFont font1 = PDType1Font.HELVETICA;
        PDFont font2 = PDType1Font.HELVETICA_BOLD;
        PDFont font3 = PDType1Font.HELVETICA_OBLIQUE;
        
        float height=page.getMediaBox().getHeight();
        float width= page.getMediaBox().getWidth();
        
        
        PDImageXObject layoutAlten = PDImageXObject.createFromFile("src\\main\\resources\\pdflayout.png", document);
        PDImageXObject blueStick = PDImageXObject.createFromFile("src\\main\\resources\\bluestick.png", document);
        
        //Couleurs utilisées
        PDColor white = new PDColor(new float[] { 1f, 1f, 1f }, PDDeviceRGB.INSTANCE);
        PDColor black = new PDColor(new float[] { 0f, 0f, 0f }, PDDeviceRGB.INSTANCE);
        PDColor darkblue = new PDColor(new float[] { 4/255f, 57/255f, 98/255f }, PDDeviceRGB.INSTANCE);
        PDColor lightblue = new PDColor(new float[] { 0f, 139/255f, 210/255f }, PDDeviceRGB.INSTANCE);
        
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        
        
        contentStream.transform(new Matrix(0, 1, -1, 0, width, 0));
        
        //
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
        
        //
        contentStream.drawImage(blueStick,60,280,15,220);
        contentStream.setNonStrokingColor(white);
        contentStream.addRect(75, 280, 220, 220);
        contentStream.fill();
        contentStream.beginText();
        contentStream.newLineAtOffset(85, 480);
        contentStream.setFont( font1, 15 );
        contentStream.setNonStrokingColor(lightblue);
        contentStream.showText(mission.getCustomer().getName());
        contentStream.newLineAtOffset(15, -20);
        contentStream.setFont( font3, 10 );
        contentStream.setNonStrokingColor(black);
        contentStream.showText(mission.getCustomer().getActivitySector());
        contentStream.setFont( font1, 10 );
        contentStream.newLineAtOffset(0, -5);
        
        String customerDescription = mission.getCustomer().getDescription();
        for(String line : separateLines(customerDescription,font1,185)) {
        	contentStream.newLineAtOffset(0, -15);
        	contentStream.showText(line);
        }
        contentStream.endText();
                
        //
        contentStream.drawImage(blueStick,315,280,15,220);
        contentStream.setNonStrokingColor(white);
        contentStream.addRect(330, 280, 480, 220);
        contentStream.fill();
        contentStream.beginText();
        contentStream.newLineAtOffset(340, 480);
        contentStream.setFont( font1, 15 );
        contentStream.setNonStrokingColor(lightblue);
        contentStream.showText("La Mission ");
        contentStream.newLineAtOffset(15, -5);
        contentStream.setFont( font1, 10 );
        contentStream.setNonStrokingColor(black);
        
        String missionDescription = mission.getLastVersion().getDescription();
        for(String line : separateLines(missionDescription,font1,445)) {
        	contentStream.newLineAtOffset(0, -15);
        	contentStream.showText(line);
        }
        contentStream.endText();
        
        //
        contentStream.drawImage(blueStick,315,80,15,180);
        contentStream.setNonStrokingColor(white);
        contentStream.addRect(330, 80, 220, 180);
        contentStream.fill();
        contentStream.beginText();
        contentStream.newLineAtOffset(340, 240);
        contentStream.setFont( font1, 15 );
        contentStream.setNonStrokingColor(lightblue);
        contentStream.showText(mission.getConsultant().getFirstname()+" "+mission.getConsultant().getLastname());
        contentStream.newLineAtOffset(15, -5);
        contentStream.setFont( font1, 10 );
        contentStream.setNonStrokingColor(black);
        
        Set<Diploma> diplomas =  mission.getConsultant().getDiplomas();
        for(Diploma diploma : diplomas ) {
	        for(String line : separateLines(diploma.getEntitled(),font1,185)) {
	        	contentStream.newLineAtOffset(0, -15);
	        	contentStream.showText(line);
	        }
	        for(String line : separateLines(diploma.getEstablishment(),font1,185)) {
	        	contentStream.newLineAtOffset(0, -15);
	        	contentStream.showText(line);
	        }
	        contentStream.newLineAtOffset(0, -10);
        }
        contentStream.endText();
        
        //
        contentStream.drawImage(blueStick,565,80,15,180);
        contentStream.setNonStrokingColor(white);
        contentStream.addRect(580, 80, 230, 180);
        contentStream.fill();
        contentStream.beginText();
        contentStream.newLineAtOffset(590, 240);
        contentStream.setFont( font1, 15 );
        contentStream.setNonStrokingColor(lightblue);
        contentStream.showText("Compétences mises en avant");
        contentStream.newLineAtOffset(15, -5);
        contentStream.setFont( font1, 10 );
        contentStream.setNonStrokingColor(black);
        
        Set<Project> projects =  mission.getLastVersion().getProjects();
        for(Project project : projects ) {
        	Set<Skill> skills =  project.getSkills();
        	for(Skill skill : skills) {
		        for(String line : separateLines(skill.getLabel(),font1,195)) {
		        	contentStream.newLineAtOffset(0, -15);
		        	contentStream.showText(line);
		        }
        	}
        }
        contentStream.endText();
        
        contentStream.close();
        
        document.save("..\\"+mission.getLastVersion().getTitle()+".pdf");
        document.close();
	}
	
	public static void makeProjectPDF(Project project) throws FileNotFoundException, MalformedURLException {
		
		
	}
	
	private static List<String> separateLines(String text,PDFont font, int maxWidth ) throws IOException{
		
		if(text == null)
			return new ArrayList<String>();
		int end1 = -1 ;
        int end2 = 0;
        List<String> lines = new ArrayList<String>();
        while(text.length()>0) {
        	end2 = text.indexOf(" ", end1+1);
        	if(end2<0)
        		end2 = text.length();
        	float size = font.getStringWidth(text.substring(0, end2))/100;
        	if(size>maxWidth) {
        		if(end1<0)
        			end1 = end2;
        		lines.add(text.substring(0, end1));
        		text = text.substring(end1).trim();
        		end1 = -1;
        		
        	}
        	else if (end2 == text.length()){
        		lines.add(text);
        		text = "";
        	}
        	else {
        		end1 = end2 ;
        	}
        }
        
        return lines;
	}
    
	
}
