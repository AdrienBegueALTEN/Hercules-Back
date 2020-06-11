package com.alten.hercules.service;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.ZoneId;
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
	
	
	// Polices utilisées
    PDFont font1 = PDType1Font.HELVETICA;
    PDFont font2 = PDType1Font.HELVETICA_BOLD;
    PDFont font3 = PDType1Font.HELVETICA_OBLIQUE;
	
    //Couleurs utilisées
    PDColor white = new PDColor(new float[] { 1f, 1f, 1f }, PDDeviceRGB.INSTANCE);
    PDColor black = new PDColor(new float[] { 0f, 0f, 0f }, PDDeviceRGB.INSTANCE);
    PDColor darkblue = new PDColor(new float[] { 4/255f, 57/255f, 98/255f }, PDDeviceRGB.INSTANCE);
    PDColor lightblue = new PDColor(new float[] { 0f, 139/255f, 210/255f }, PDDeviceRGB.INSTANCE);
    PDColor yellow = new PDColor(new float[] { 1f, 186/255f, 0f }, PDDeviceRGB.INSTANCE);
    
    // Images utilisées
    PDImageXObject layoutAlten;
    PDImageXObject blueStick;
    PDImageXObject contractIcon;
    PDImageXObject teamIcon;
    PDImageXObject durationIcon;
    PDImageXObject localizationIcon;
    
	public PDFGenerator(PDDocument document) throws IOException {
		// Images utilisées
        layoutAlten = PDImageXObject.createFromFile("src\\main\\resources\\pdflayout.png", document);
        blueStick = PDImageXObject.createFromFile("src\\main\\resources\\bluestick.png", document);
        contractIcon = PDImageXObject.createFromFile("src\\main\\resources\\contrat.png", document);
        teamIcon = PDImageXObject.createFromFile("src\\main\\resources\\equipe.png", document);
        durationIcon = PDImageXObject.createFromFile("src\\main\\resources\\duree.png", document);
        localizationIcon = PDImageXObject.createFromFile("src\\main\\resources\\localisation.png", document);
	}

	public void makeMissionPDF(Mission mission, PDDocument document) throws IOException {

		float constantForCircleWithBezierCurve = (float) 0.551915024494; // voir Bezier Curves
		
        PDPage page = new PDPage(PDRectangle.A4);
        page.setRotation(90);
        document.addPage( page );

        
        
        float height=page.getMediaBox().getHeight();
        float width= page.getMediaBox().getWidth();
        
        
        
        
        
        
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
        else {
        	contentStream.setNonStrokingColor(black);
            contentStream.setFont( font1, 8 );
            showCenteredText(contentStream,"aucun logo disponible",(int)height-40,(int)width-35);
        }
        
        
        contentStream.beginText();
        contentStream.setFont( font1, 12 );
        contentStream.setNonStrokingColor(white);
        contentStream.newLineAtOffset(75, width-70);
        contentStream.showText(cutText(mission.getLastVersion().getTitle(),font1,(int) height-70-75,12));
        contentStream.newLineAtOffset(0,35);
        contentStream.setFont(font2, 18);
        contentStream.setNonStrokingColor(darkblue);
        contentStream.showText( "Mission");
        contentStream.setFont(font1, 18);
        contentStream.showText(cutText( " « "+ mission.getLastVersion().getConsultantRole()+" » chez "+ mission.getCustomer().getName()+ " par "+ mission.getConsultant().getFirstname()+" "+mission.getConsultant().getLastname()+" ",font1,(int) height-70-75,18) );
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
        contentStream.showText(cutText(mission.getCustomer().getName(),font1,210,15));
        contentStream.newLineAtOffset(15, -20);
        contentStream.setFont( font3, 10 );
        contentStream.setNonStrokingColor(black);
        contentStream.showText(cutText(mission.getCustomer().getActivitySector(),font3,195,10));
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
        contentStream.showText(cutText(mission.getConsultant().getFirstname()+" "+mission.getConsultant().getLastname(),font1,210,15));
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
        contentStream.endText();
        
        int cy = 223;
    	int counter = 0;
        Set<Project> projects =  mission.getLastVersion().getProjects();
        for(Project project : projects ) {
        	
        	for(Skill skill : project.getSkills()) {
        		
        		
        		if(counter<20 && counter>=10) {
        			int cx = 588+115;
        			int cyBis  = cy + 150;
	        		contentStream.setNonStrokingColor(yellow);
	                contentStream.moveTo(cx - 2, cyBis);
	                contentStream.curveTo(cx - 2, cyBis + constantForCircleWithBezierCurve*2, cx - constantForCircleWithBezierCurve*2, cyBis + 2, cx, cyBis + 2);
	                contentStream.curveTo(cx + constantForCircleWithBezierCurve*2, cyBis + 2, cx + 2, cyBis + constantForCircleWithBezierCurve*2, cx + 2, cyBis);
	                contentStream.curveTo(cx + 2, cyBis - constantForCircleWithBezierCurve*2, cx + constantForCircleWithBezierCurve*2, cyBis - 2, cx, cyBis - 2);
	                contentStream.curveTo(cx - constantForCircleWithBezierCurve*2, cyBis - 2, cx - 2, cyBis - constantForCircleWithBezierCurve*2, cx - 2, cyBis);
	                contentStream.fill();
	                contentStream.setNonStrokingColor(black);
        		}
        		else if(counter<10) {
        			int cx = 588;
        			contentStream.setNonStrokingColor(yellow);
	                contentStream.moveTo(cx - 2, cy);
	                contentStream.curveTo(cx - 2, cy + constantForCircleWithBezierCurve*2, cx - constantForCircleWithBezierCurve*2, cy + 2, cx, cy + 2);
	                contentStream.curveTo(cx + constantForCircleWithBezierCurve*2, cy + 2, cx + 2, cy + constantForCircleWithBezierCurve*2, cx + 2, cy);
	                contentStream.curveTo(cx + 2, cy - constantForCircleWithBezierCurve*2, cx + constantForCircleWithBezierCurve*2, cy - 2, cx, cy - 2);
	                contentStream.curveTo(cx - constantForCircleWithBezierCurve*2, cy - 2, cx - 2, cy - constantForCircleWithBezierCurve*2, cx - 2, cy);
	                contentStream.fill();
	                contentStream.setNonStrokingColor(black);

        		}
                
		        for(String line : separateLines(skill.getLabel(),font1,95,10)) {
		        	if(counter<20) {
			        	contentStream.beginText();
		        		contentStream.setFont( font1, 10 );
		        		if(counter>=10)
		        			contentStream.newLineAtOffset(595+115, 220+(counter-10)*(-15));
		        		else if(counter<10)
		        			contentStream.newLineAtOffset(595, 220+counter*(-15));
				        contentStream.showText(line);
				        contentStream.endText();
				        cy -= 15;
				        counter += 1;
		        	}
		        }
        	}
        }
        
        
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
        if(mission.getLastVersion().getVersionDate()!=null) {
	        contentStream.setNonStrokingColor(black);
	        contentStream.beginText();
	        contentStream.newLineAtOffset(10, 10);
	        contentStream.setFont( font1, 10 );
	        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); 
	        contentStream.showText(dateFormat.format(mission.getLastVersion().getVersionDate()));
	        contentStream.endText();
        }
        
        
        // Bulles
        
         
        int rayon = 28;
        int cx1 = 420;
        int cy1 = 40;
        int cx2 = 520;
        int cx3 = 620;
        int cx4 = 720;
        
        
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
        showCenteredText(contentStream,modifyText(mission.getLastVersion().getContractType().name()),cx1,35);
        
        
        // Bulle équipe 
        contentStream.setNonStrokingColor(white);
        contentStream.moveTo(cx2 - rayon, cy1);
        contentStream.curveTo(cx2 - rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx2 - constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx2, cy1 + rayon);
        contentStream.curveTo(cx2 + constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx2 + rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx2 + rayon, cy1);
        contentStream.curveTo(cx2 + rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx2 + constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx2, cy1 - rayon);
        contentStream.curveTo(cx2 - constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx2 - rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx2 - rayon, cy1);
        contentStream.fill();
        contentStream.drawImage(teamIcon,512,45,teamIcon.getWidth()/3,teamIcon.getHeight()/3);
        contentStream.setNonStrokingColor(black);
        contentStream.setFont( font1, 8 );
        showCenteredText(contentStream,modifyText("Taille d'équipe : "+mission.getLastVersion().getTeamSize().toString()),cx2,35);
        
     // Bulle durée 
        contentStream.setNonStrokingColor(white);
        contentStream.moveTo(cx3 - rayon, cy1);
        contentStream.curveTo(cx3 - rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx3 - constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx3, cy1 + rayon);
        contentStream.curveTo(cx3 + constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx3 + rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx3 + rayon, cy1);
        contentStream.curveTo(cx3 + rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx3 + constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx3, cy1 - rayon);
        contentStream.curveTo(cx3 - constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx3 - rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx3 - rayon, cy1);
        contentStream.fill();
        contentStream.drawImage(durationIcon,610,45,durationIcon.getWidth()/3,durationIcon.getHeight()/3);
        contentStream.setNonStrokingColor(black);
        contentStream.setFont( font1, 8 );
        int durationDay = 0;
        int durationMonth = 0;
        int durationYear = 0;
        for(Project project : projects ) {
        	if(project.getBeginDate()!=null && project.getEndDate()!=null) {
	        	Period period = Period.between(project.getBeginDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
	        			                       project.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
	        	durationDay +=  period.getDays();
	        	durationMonth += period.getMonths();
	        	durationYear += period.getYears();
        	}
        	
        }
        
        showCenteredText(contentStream,modifyText(durationToText(durationDay,durationMonth, durationYear)),cx3,35);
        
     // Bulle localisation
        contentStream.setNonStrokingColor(white);
        contentStream.moveTo(cx4 - rayon, cy1);
        contentStream.curveTo(cx4 - rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx4 - constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx4, cy1 + rayon);
        contentStream.curveTo(cx4 + constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx4 + rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx4 + rayon, cy1);
        contentStream.curveTo(cx4 + rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx4 + constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx4, cy1 - rayon);
        contentStream.curveTo(cx4 - constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx4 - rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx4 - rayon, cy1);
        contentStream.fill();
        contentStream.drawImage(localizationIcon,709,45,localizationIcon.getWidth()/3,localizationIcon.getHeight()/3);
        contentStream.setNonStrokingColor(black);
        contentStream.setFont( font1, 8 );
        showCenteredText(contentStream,modifyText(mission.getLastVersion().getCity()+", "+mission.getLastVersion().getCountry()),cx4,35);
        
        contentStream.close();
        
        
	}
	
	public void makeProjectPDF(Project project,PDDocument document) throws IOException {
		
		Mission mission = project.getMissionSheet().getMission();
		
		float constantForCircleWithBezierCurve = (float) 0.551915024494; // voir Bezier Curves
		
		PDPage page = new PDPage(PDRectangle.A4);
        page.setRotation(90);
        document.addPage( page );

        
        float height=page.getMediaBox().getHeight();
        float width= page.getMediaBox().getWidth();
        

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        
        
        contentStream.transform(new Matrix(0, 1, -1, 0, width, 0));
        
        // en-tête
        contentStream.drawImage(layoutAlten,0,0,height,width);
        
        if(mission.getCustomer().getLogo()!=null) {
	        PDImageXObject customerLogo = 
	        		PDImageXObject.createFromFile("img\\logo\\"+mission.getCustomer().getLogo(), document);
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
        else {
        	contentStream.setNonStrokingColor(black);
            contentStream.setFont( font1, 8 );
            showCenteredText(contentStream,"aucun logo disponible",(int)height-40,(int)width-35);
        }
        
        
        contentStream.beginText();
        contentStream.setFont( font1, 12 );
        contentStream.setNonStrokingColor(white);
        contentStream.newLineAtOffset(75, width-70);
        contentStream.showText(cutText(project.getTitle(),font1,(int) height-70-75,12));
        contentStream.newLineAtOffset(0,35);
        contentStream.setFont(font2, 18);
        contentStream.setNonStrokingColor(darkblue);
        contentStream.showText( "Projet");
        contentStream.setFont(font1, 18);
        contentStream.showText(cutText( " « "+ project.getMissionSheet().getConsultantRole()+
        						" » chez "+ mission.getCustomer().getName()+ 
        						" par "+ mission.getConsultant().getFirstname()+
        						" "+mission.getConsultant().getLastname()+" ",font1,(int) height-70-75,18) );
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
        contentStream.showText(cutText(mission.getCustomer().getName(),font1,210,15));
        contentStream.newLineAtOffset(15, -20);
        contentStream.setFont( font3, 10 );
        contentStream.setNonStrokingColor(black);
        contentStream.showText(cutText(mission.getCustomer().getActivitySector(),font3,195,10));
        contentStream.setFont( font1, 10 );
        contentStream.newLineAtOffset(0, -5);
        
        String customerDescription = mission.getCustomer().getDescription();
        for(String line : separateLines(customerDescription,font1,185,10)) {
        	contentStream.newLineAtOffset(0, -15);
        	contentStream.showText(line);
        }
        contentStream.endText();
                
        // description du projet
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
        
        String projectDescription = project.getDescription();
        for(String line : separateLines(projectDescription,font1,445,10)) {
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
        contentStream.showText(cutText(mission.getConsultant().getFirstname()+" "+mission.getConsultant().getLastname(),font1,210,15));
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
        contentStream.endText();
        
        
        int cy = 223;
    	int counter = 0;
        for(Skill skill : project.getSkills()) {
        	
        	if(counter<20 && counter>=10) {
    			int cx = 588+115;
    			int cyBis  = cy + 150;
        		contentStream.setNonStrokingColor(yellow);
                contentStream.moveTo(cx - 2, cyBis);
                contentStream.curveTo(cx - 2, cyBis + constantForCircleWithBezierCurve*2, cx - constantForCircleWithBezierCurve*2, cyBis + 2, cx, cyBis + 2);
                contentStream.curveTo(cx + constantForCircleWithBezierCurve*2, cyBis + 2, cx + 2, cyBis + constantForCircleWithBezierCurve*2, cx + 2, cyBis);
                contentStream.curveTo(cx + 2, cyBis - constantForCircleWithBezierCurve*2, cx + constantForCircleWithBezierCurve*2, cyBis - 2, cx, cyBis - 2);
                contentStream.curveTo(cx - constantForCircleWithBezierCurve*2, cyBis - 2, cx - 2, cyBis - constantForCircleWithBezierCurve*2, cx - 2, cyBis);
                contentStream.fill();
                contentStream.setNonStrokingColor(black);
    		}
    		else if(counter<10) {
    			int cx = 588;
    			contentStream.setNonStrokingColor(yellow);
                contentStream.moveTo(cx - 2, cy);
                contentStream.curveTo(cx - 2, cy + constantForCircleWithBezierCurve*2, cx - constantForCircleWithBezierCurve*2, cy + 2, cx, cy + 2);
                contentStream.curveTo(cx + constantForCircleWithBezierCurve*2, cy + 2, cx + 2, cy + constantForCircleWithBezierCurve*2, cx + 2, cy);
                contentStream.curveTo(cx + 2, cy - constantForCircleWithBezierCurve*2, cx + constantForCircleWithBezierCurve*2, cy - 2, cx, cy - 2);
                contentStream.curveTo(cx - constantForCircleWithBezierCurve*2, cy - 2, cx - 2, cy - constantForCircleWithBezierCurve*2, cx - 2, cy);
                contentStream.fill();
                contentStream.setNonStrokingColor(black);

    		}
            
        	for(String line : separateLines(skill.getLabel(),font1,95,10)) {
        		if(counter<20) {
	        		contentStream.beginText();
	        		contentStream.setFont( font1, 10 );
	        		if(counter>=10)
	        			contentStream.newLineAtOffset(595+115, 220+(counter-10)*(-15));
	        		else if(counter<10)
	        			contentStream.newLineAtOffset(595, 220+counter*(-15));
			        contentStream.showText(line);
			        contentStream.endText();
			        cy -= 15;
			        counter += 1;
        		}
		    }
        }
        
        
        // photo projet
        if(project.getPicture()!=null) {
	        PDImageXObject projectPicture = 
	        		PDImageXObject.createFromFile("img\\proj\\"+project.getPicture(), document);
	        float optimalHeight = projectPicture.getHeight();
	        float optimalWidth = projectPicture.getWidth();
	        if(projectPicture.getWidth()>projectPicture.getHeight()) {
	        	if(projectPicture.getWidth()>=235) {
	        		optimalHeight = projectPicture.getHeight()/(projectPicture.getWidth()/235);
	        		optimalWidth = 235;
	        	}
	        }
	        else {
	        	if(projectPicture.getHeight()>=235) {
	        		optimalWidth = projectPicture.getWidth()/(projectPicture.getHeight()/235);
	        		optimalHeight = 235;
	        	}
	        }
	        
	        contentStream.drawImage(projectPicture, 60+(235-optimalWidth)/2,25+(235-optimalHeight),optimalWidth,optimalHeight); //235 max en hauteur   235 max en largeur
        }
        else {
        	contentStream.setNonStrokingColor(black);
            contentStream.setFont( font1, 8 );
            showCenteredText(contentStream,"aucune photo disponible",175,160);
            
        }
        
        // Note de version
        contentStream.setNonStrokingColor(black);
        contentStream.beginText();
        contentStream.newLineAtOffset(10, 10);
        contentStream.setFont( font1, 10 );
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); 
        contentStream.showText(dateFormat.format(mission.getLastVersion().getVersionDate()));
        contentStream.endText();
        
        // Bulles
        
         
        int rayon = 28;
        int cx1 = 420;
        int cy1 = 40;
        int cx2 = 520;
        int cx3 = 620;
        int cx4 = 720;
        
        
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
        showCenteredText(contentStream,modifyText(mission.getLastVersion().getContractType().name()),cx1,35);
        
        
        // Bulle équipe 
        contentStream.setNonStrokingColor(white);
        contentStream.moveTo(cx2 - rayon, cy1);
        contentStream.curveTo(cx2 - rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx2 - constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx2, cy1 + rayon);
        contentStream.curveTo(cx2 + constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx2 + rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx2 + rayon, cy1);
        contentStream.curveTo(cx2 + rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx2 + constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx2, cy1 - rayon);
        contentStream.curveTo(cx2 - constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx2 - rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx2 - rayon, cy1);
        contentStream.fill();
        contentStream.drawImage(teamIcon,512,45,teamIcon.getWidth()/3,teamIcon.getHeight()/3);
        contentStream.setNonStrokingColor(black);
        contentStream.setFont( font1, 8 );
        showCenteredText(contentStream,modifyText("Taille d'équipe : "+mission.getLastVersion().getTeamSize().toString()),cx2,35);
        
     // Bulle durée 
        contentStream.setNonStrokingColor(white);
        contentStream.moveTo(cx3 - rayon, cy1);
        contentStream.curveTo(cx3 - rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx3 - constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx3, cy1 + rayon);
        contentStream.curveTo(cx3 + constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx3 + rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx3 + rayon, cy1);
        contentStream.curveTo(cx3 + rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx3 + constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx3, cy1 - rayon);
        contentStream.curveTo(cx3 - constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx3 - rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx3 - rayon, cy1);
        contentStream.fill();
        contentStream.drawImage(durationIcon,610,45,durationIcon.getWidth()/3,durationIcon.getHeight()/3);
        contentStream.setNonStrokingColor(black);
        contentStream.setFont( font1, 8 );
        int durationDay = 0;
        int durationMonth = 0;
        int durationYear = 0;
        
        if(project.getBeginDate()!=null && project.getEndDate()!=null) {
	        Period period = Period.between(project.getBeginDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
	        			                       project.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
	        durationDay =  period.getDays();
	        durationMonth = period.getMonths();
	        durationYear = period.getYears();
        }
        	
        
        
        showCenteredText(contentStream,modifyText(durationToText(durationDay,durationMonth, durationYear)),cx3,35);
        
     // Bulle localisation
        contentStream.setNonStrokingColor(white);
        contentStream.moveTo(cx4 - rayon, cy1);
        contentStream.curveTo(cx4 - rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx4 - constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx4, cy1 + rayon);
        contentStream.curveTo(cx4 + constantForCircleWithBezierCurve*rayon, cy1 + rayon, cx4 + rayon, cy1 + constantForCircleWithBezierCurve*rayon, cx4 + rayon, cy1);
        contentStream.curveTo(cx4 + rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx4 + constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx4, cy1 - rayon);
        contentStream.curveTo(cx4 - constantForCircleWithBezierCurve*rayon, cy1 - rayon, cx4 - rayon, cy1 - constantForCircleWithBezierCurve*rayon, cx4 - rayon, cy1);
        contentStream.fill();
        contentStream.drawImage(localizationIcon,709,45,localizationIcon.getWidth()/3,localizationIcon.getHeight()/3);
        contentStream.setNonStrokingColor(black);
        contentStream.setFont( font1, 8 );
        showCenteredText(contentStream,modifyText(mission.getLastVersion().getCity()+", "+mission.getLastVersion().getCountry()),cx4,35);
        
        contentStream.close();
		
	}
	
	public void saveFinalPDF(PDDocument document) throws IOException {
		document.save("pdf\\fichesMissionsEtProjets.pdf");
        document.close();
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
        		int end3 = end2;
        		if(end1<0) {
        			end1 = end2;
        			while(size>maxWidth) {
            			end3 -= 1;
            			size = fontSize*font.getStringWidth(text.substring(0, end3))/1000;
            		}
        			lines.add(text.substring(0, end3-3)+"...");
        		}
        		else
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
    
	private static void showCenteredText(PDPageContentStream contentStream, String text,int cx,int cy) throws IOException {
		List<String> lines = separateLines(text,PDType1Font.HELVETICA,55,8);
		int count = 0;
		for(String line : lines) {
			float size = 8*PDType1Font.HELVETICA.getStringWidth(line)/1000;
			contentStream.beginText();
			contentStream.newLineAtOffset(cx-size/2, cy-10*count);
			contentStream.showText(line);
			contentStream.endText();
			count ++;
		}
		
	}
	
	private static String cutText(String text, PDFont font, int maxWidth, int fontSize) throws IOException {
		
		int end = text.length();
		float size = fontSize*font.getStringWidth(text.substring(0, end))/1000;
		while(size>maxWidth) {
			end -= 1;
			size = fontSize*font.getStringWidth(text.substring(0, end))/1000;
		}
		
		if(end==text.length())
			return text.substring(0,end);
		else
			return text.substring(0,end-3)+"...";
	}
	
	private static String modifyText(String text) {
		if(text.equals("technical_assistance")) {
			return "Assistance technique";
		}
		else if(text.equals("services_center")) {
			return "Centre de services";
		}
		else if(text.equals("flat_fee")) {
			return "Forfait";
		}
		else 
			return text;
	}
	
	private static String durationToText(Integer days, Integer months, Integer years) {
		if(days<7) {
			if(days == 0 || days==1 )
				return days.toString()+" jour";
			else
				return days.toString()+" jours";
		}
		else if(days>=7 && months<1) {
			if(days/7 == 1)
				return Integer.valueOf(days/7).toString()+" semaine";
			else
				return Integer.valueOf(days/7).toString()+" semaines";
		}
		else if(months>=1 && years<1) {
			return months.toString()+" mois";
		}
		else {
			if(years==1)
				return years.toString()+" année";
			else
				return years.toString()+" années";
		}
	}
}
