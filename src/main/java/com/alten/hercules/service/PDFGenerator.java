package com.alten.hercules.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

        // Polices utilisées
        PDFont font1 = PDType1Font.HELVETICA;
        PDFont font2 = PDType1Font.HELVETICA_BOLD;
        PDFont font3 = PDType1Font.HELVETICA_OBLIQUE;
        
        float height=page.getMediaBox().getHeight();
        float width= page.getMediaBox().getWidth();
        
        // Images utilisées
        PDImageXObject layoutAlten = PDImageXObject.createFromFile("src\\main\\resources\\pdflayout.png", document);
        PDImageXObject blueStick = PDImageXObject.createFromFile("src\\main\\resources\\bluestick.png", document);
        PDImageXObject contractIcon = PDImageXObject.createFromFile("src\\main\\resources\\contrat.png", document);
        PDImageXObject teamIcon = PDImageXObject.createFromFile("src\\main\\resources\\equipe.png", document);
        PDImageXObject durationIcon = PDImageXObject.createFromFile("src\\main\\resources\\duree.png", document);
        PDImageXObject localizationIcon = PDImageXObject.createFromFile("src\\main\\resources\\localisation.png", document);
        
        
        //Couleurs utilisées
        PDColor white = new PDColor(new float[] { 1f, 1f, 1f }, PDDeviceRGB.INSTANCE);
        PDColor black = new PDColor(new float[] { 0f, 0f, 0f }, PDDeviceRGB.INSTANCE);
        PDColor darkblue = new PDColor(new float[] { 4/255f, 57/255f, 98/255f }, PDDeviceRGB.INSTANCE);
        PDColor lightblue = new PDColor(new float[] { 0f, 139/255f, 210/255f }, PDDeviceRGB.INSTANCE);
        
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        
        
        contentStream.transform(new Matrix(0, 1, -1, 0, width, 0));
        
        // en-tête
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
        
        // description du client
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
        for(String line : separateLines(customerDescription,font1,185,10)) {
        	contentStream.newLineAtOffset(0, -15);
        	contentStream.showText(line);
        }
        contentStream.endText();
                
        // description de la mission
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
        for(String line : separateLines(missionDescription,font1,445,10)) {
        	contentStream.newLineAtOffset(0, -15);
        	contentStream.showText(line);
        }
        contentStream.endText();
        
        // diplômes du consultant
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
	        for(String line : separateLines(diploma.getEntitled(),font1,185,10)) {
	        	contentStream.newLineAtOffset(0, -15);
	        	contentStream.showText(line);
	        }
	        for(String line : separateLines(diploma.getEstablishment(),font1,185,10)) {
	        	contentStream.newLineAtOffset(0, -15);
	        	contentStream.showText(line);
	        }
	        contentStream.newLineAtOffset(0, -10);
        }
        contentStream.endText();
        
        // compétences de la mission
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
		        for(String line : separateLines(skill.getLabel(),font1,195,10)) {
		        	contentStream.newLineAtOffset(0, -15);
		        	contentStream.showText(line);
		        }
        	}
        }
        contentStream.endText();
        
        // Commentaire
        contentStream.setNonStrokingColor(lightblue);
        contentStream.beginText();
        contentStream.newLineAtOffset(60, 240);
        contentStream.setFont( font2, 15 );
        contentStream.showText("«");
        contentStream.newLineAtOffset(0, -5);
        contentStream.setFont( font1, 10 );
        String commentary = mission.getLastVersion().getComment();
        for(String line : separateLines(commentary,font1,220,10)) {
        	contentStream.newLineAtOffset(0, -15);
        	contentStream.showText(line);
        }
        contentStream.newLineAtOffset(220, -15);
        contentStream.setFont( font2, 15 );
        contentStream.showText("»");
        contentStream.endText();
        
        
        
        // Note de version
        contentStream.setNonStrokingColor(black);
        contentStream.beginText();
        contentStream.newLineAtOffset(10, 10);
        contentStream.setFont( font1, 10 );
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); 
        contentStream.showText(dateFormat.format(mission.getLastVersion().getVersionDate()));
        contentStream.endText();
        
        // Bulles
        
        float constantForCircleWithBezierCurve = (float) 0.551915024494; // voir Bezier Curves 
        int rayon = 28;
        int cx1 = 420;
        int cy1 = 40;
        
        
        // Bulle contrat
        contentStream.setNonStrokingColor(white);
        contentStream.moveTo(cx1 - rayon, cy1);
        contentStream.curveTo(cx1 - rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx1 - constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx1, cy1 + rayon);
        contentStream.curveTo(cx1 + constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx1 + rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx1 + rayon, cy1);
        contentStream.curveTo(cx1 + rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx1 + constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx1, cy1 - rayon);
        contentStream.curveTo(cx1 - constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx1 - rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx1 - rayon, cy1);
        contentStream.fill();
        contentStream.drawImage(contractIcon,411,45,contractIcon.getWidth()/3,contractIcon.getHeight()/3);
        contentStream.setNonStrokingColor(black);
        contentStream.setFont( font1, 8 );
        showCenteredText(contentStream,modifyText(mission.getLastVersion().getContractType().name()),cx1);
        
        
        // Bulle équipe 
        
        
        contentStream.close();
        
        document.save("..\\"+mission.getLastVersion().getTitle()+".pdf");
        document.close();
	}
	
	public static void makeProjectPDF(Project project) throws FileNotFoundException, MalformedURLException {
		
		
	}
	
	
	 
	/**
	 * Fonction qui va prendre en argument un String text et le découper en plusieurs lignes de façon à ce que chaque ligne soit de longueur inférieure à maxWidth avec la police font.
	 * @param text String contenant le texte à découper
	 * @param font PDFont utilisé pour le texte
	 * @param maxWidth longueur maximale d'une ligne
	 * @return Une List<String> contenant le texte donné en argument où chaque ligne
	 * @throws IOException
	 */
	private static List<String> separateLines(String text,PDFont font, int maxWidth,int fontSize ) throws IOException{
		
		if(text == null)
			return new ArrayList<String>();
		int end1 = -1 ;
        int end2 = 0;
        List<String> lines = new ArrayList<String>();
        while(text.length()>0) {
        	end2 = text.indexOf(" ", end1+1);
        	if(end2<0)
        		end2 = text.length();
        	float size = fontSize*font.getStringWidth(text.substring(0, end2))/1000;
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
    
	private static void showCenteredText(PDPageContentStream contentStream, String text,int cx) throws IOException {
		List<String> lines = separateLines(text,PDType1Font.HELVETICA,46,8);
		int count = 0;
		for(String line : lines) {
			float size = 8*PDType1Font.HELVETICA.getStringWidth(line)/1000;
			contentStream.beginText();
			contentStream.newLineAtOffset(cx-size/2, 35-10*count);
			contentStream.showText(line);
			contentStream.endText();
			count ++;
		}
		
	}
	
	private static String modifyText(String text) {
		if(text.equals("technical_assistance")) {
			return "Assistance technique";
		}
		else if(text.equals("")) {
			return text;
		}
		else 
			return text;
	}
}
