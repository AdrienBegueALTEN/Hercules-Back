package com.alten.hercules.service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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
import org.springframework.util.FileSystemUtils;

import com.alten.hercules.model.diploma.Diploma;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.project.Project;
import com.alten.hercules.model.skill.Skill;


/**
 * Class that manages the creation of the pdf pages for either missions or projects.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class PDFGenerator {
	
	/**
	 * Path for the pdf folder
	 */
	private final static Path pdfFolder = Paths.get("pdf");
	
	// Polices utilisées
	/**
	 * Helvetica font
	 */
    private PDFont font1 = PDType1Font.HELVETICA;
    /**
     * Bold helvetica font
     */
    private PDFont font2 = PDType1Font.HELVETICA_BOLD;
    /**
     * Oblique helvetica font
     */
    private PDFont font3 = PDType1Font.HELVETICA_OBLIQUE;
	
    //Couleurs utilisées
    private PDColor white = new PDColor(new float[] { 1f, 1f, 1f }, PDDeviceRGB.INSTANCE);
    private PDColor black = new PDColor(new float[] { 0f, 0f, 0f }, PDDeviceRGB.INSTANCE);
    private PDColor darkblue = new PDColor(new float[] { 4/255f, 57/255f, 98/255f }, PDDeviceRGB.INSTANCE);
    private PDColor lightblue = new PDColor(new float[] { 0f, 139/255f, 210/255f }, PDDeviceRGB.INSTANCE);
    private PDColor yellow = new PDColor(new float[] { 1f, 186/255f, 0f }, PDDeviceRGB.INSTANCE);
    
    // Images utilisées
    private PDImageXObject layoutAlten;
    private PDImageXObject blueStick;
    private PDImageXObject contractIcon;
    private PDImageXObject teamIcon;
    private PDImageXObject durationIcon;
    private PDImageXObject localizationIcon;
    
    /**
     * Constructor that takes a PDDocument from PDFBox and initializes the used pictures
     * @param document PDDocument from PDFBox that represents the pdf document
     * @throws IOException if the files are not found
     */
	public PDFGenerator(PDDocument document) throws IOException {
		// Images utilisées
        layoutAlten = PDImageXObject.createFromFile("src\\main\\resources\\pdflayout.png", document);
        blueStick = PDImageXObject.createFromFile("src\\main\\resources\\bluestick.png", document);
        contractIcon = PDImageXObject.createFromFile("src\\main\\resources\\contrat.png", document);
        teamIcon = PDImageXObject.createFromFile("src\\main\\resources\\equipe.png", document);
        durationIcon = PDImageXObject.createFromFile("src\\main\\resources\\duree.png", document);
        localizationIcon = PDImageXObject.createFromFile("src\\main\\resources\\localisation.png", document);
	}
	
	/**
	 * Function that creates the folder pdf in the root of the project if it is not created
	 */
	public static void init() {
		try {
			if(!Files.exists(pdfFolder))
				Files.createDirectories(pdfFolder);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}
	
	/**
	 * Function that cleans everything into the PDF folder
	 */
	public static void deleteAll() {
	    FileSystemUtils.deleteRecursively(pdfFolder.toFile());
	}
	
	/**
	 * Function that manages to generate the particular parts for a mission page.
	 * @param contentStream PDPageContentStream from PDFBox that represents the stream for the page
	 * @param mission Mission of the created page
	 * @param height Height of the page
	 * @param width Width of the page
	 * @throws IOException exception that indicates the failure of the PDF creation
	 */
	private void makeMissionPDF(PDPageContentStream contentStream, Mission mission, float height, float width) throws IOException {
        
        // text of the mission in the header
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
        contentStream.showText(cutText( " « "+ mission.getLastVersion().getConsultantRole()+
        							" » chez "+ mission.getCustomer().getName()+ 
        							" par "+ mission.getConsultant().getFirstname()+
        							" "+anonymiseLastname(mission.getConsultant().getLastname())+" ",font1,(int) height-70-75-50,18) );
        contentStream.endText();
         
        // description of the mission
        contentStream.drawImage(blueStick,315,280,15,220);
        contentStream.setNonStrokingColor(white);
        contentStream.addRect(330, 280, 480, 220);
        contentStream.fill();
        contentStream.beginText();
        contentStream.newLineAtOffset(340, 480);
        contentStream.setFont( font1, 15 );
        contentStream.setNonStrokingColor(lightblue);
        contentStream.showText("La mission ");
        contentStream.newLineAtOffset(15, -5);
        contentStream.setFont( font1, 10 );
        contentStream.setNonStrokingColor(black);
        
        String missionDescription = mission.getLastVersion().getDescription();

        for(String line : separateLines(missionDescription, font1, 445, 10, 13)) {
        	contentStream.newLineAtOffset(0, -15);
        	contentStream.showText(line);
        }
        contentStream.endText();

        // Comment
        if( mission.getLastVersion().getComment() != null && !mission.getLastVersion().getComment().equals("") ) {
	        
        	contentStream.setNonStrokingColor(lightblue);
	        contentStream.beginText();
	        contentStream.newLineAtOffset(60, 240);
	        contentStream.setFont( font2, 15 );
	        contentStream.showText("«");
	        contentStream.newLineAtOffset(0, -5);
	        contentStream.setFont( font1, 10 );
	        String commentary = mission.getLastVersion().getComment();
	        
	        for(String line : separateLines(commentary, font1, 220, 10, 10)) {
	        	contentStream.newLineAtOffset(0, -15);
	        	contentStream.showText(line);
	        }
	        contentStream.newLineAtOffset(220, -15);
	        contentStream.setFont( font2, 15 );
	        contentStream.showText("»");
	        contentStream.endText();
        }

	}
	
	/**
	 * Function that manages to generate the particular parts for a project page.
	 * @param document PDDocument from PDFBox that represents the PDF document
	 * @param contentStream PDPageContentStream from PDFBox that represents the stream for the page
	 * @param project Project of the created page
	 * @param height Height of the page
	 * @param width Width of the page
	 * @throws IOException exception that indicates the failure of the PDF creation
	 */
	private void makeProjectPDF(PDDocument document, PDPageContentStream contentStream, Project project, float height, float width) throws IOException {
		
		Mission mission = project.getMissionSheet().getMission();
		
        // text for the project in the header
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
        						" "+anonymiseLastname(mission.getConsultant().getLastname())+" ",font1,(int) height-70-75-40,18) );
        contentStream.endText();
          
        // description of the project
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
        
        for(String line : separateLines(projectDescription, font1, 445, 10, 13)) {
        	contentStream.newLineAtOffset(0, -15);
        	contentStream.showText(line);
        }
        contentStream.endText();
        
        // photo of the project
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
        /*else {
        	contentStream.setNonStrokingColor(black);
            contentStream.setFont( font1, 8 );
            showCenteredText(contentStream,"aucune photo disponible",175,160);
            
        }*/ //texte à afficher si photo indisponible
	}
	
	/**
	 * Function that manages the generation of a PDF page for projects and missions.
	 * @param mission Main mission or mission related to the project
	 * @param projects Set of the projects of the mission or a set with only the main project
	 * @param document PDDocument from PDFBox that represents the PDF document
	 * @param isMission boolean that indicates if it is a page for a project or a mission
	 * @throws IOException exception that indicates the failure of the PDF creation
	 */
	public void makePDFPage(Mission mission,Set<Project> projects, PDDocument document, boolean isMission) throws IOException {
		
		// Creation of a new page
		PDPage page = new PDPage(PDRectangle.A4);
		page.setRotation(90);
		document.addPage( page );

		// Dimensions of the page
		float height=page.getMediaBox().getHeight();
		float width= page.getMediaBox().getWidth();
		        
		// Content stream for the new page
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		        
		// landscape format
		contentStream.transform(new Matrix(0, 1, -1, 0, width, 0));
		
		// header
        contentStream.drawImage(layoutAlten,0,0,height,width);
        
        if(mission.getCustomer().getLogo()!=null) {
	        PDImageXObject customerLogo = 
	        		PDImageXObject.createFromFile("img\\logo\\"+mission.getCustomer().getLogo(), document);
	        float optimalHeight = customerLogo.getHeight();
	        float optimalWidth = customerLogo.getWidth();
	        if(customerLogo.getWidth() > customerLogo.getHeight()) {
	        	if(customerLogo.getWidth() >= 65) {
	        		optimalHeight = customerLogo.getHeight()/(customerLogo.getWidth()/65);
	        		optimalWidth = 65;
	        	}
	        }
	        else {
	        	if(customerLogo.getHeight() >= 65) {
	        		optimalWidth = customerLogo.getWidth()/(customerLogo.getHeight()/65);
	        		optimalHeight = 65;
	        	}
	        }
	        
	        contentStream.drawImage(customerLogo, height-70,width-70,optimalWidth,optimalHeight); //65 max en hauteur   65 max en largeur
        }
        /*else {
        	contentStream.setNonStrokingColor(black);
            contentStream.setFont( font1, 8 );
            showCenteredText(contentStream,"aucun logo disponible",(int)height-40,(int)width-35);
        }*/ // texte à afficher si logo indisponible
        
        // particular parts for a project or a mission
     	if(isMission)
     		makeMissionPDF(contentStream, mission, height, width);
     	else
     		makeProjectPDF(document, contentStream, projects.iterator().next(), height, width);
		
		// description of the customer
        contentStream.drawImage(blueStick, 60, 280, 15, 220);
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
        contentStream.showText(cutText(mission.getCustomer().getActivitySector(), font3, 195, 10));
        contentStream.setFont( font1, 10 );
        contentStream.newLineAtOffset(0, -5);
        
        String customerDescription = mission.getCustomer().getDescription();
        
        for(String line : separateLines(customerDescription, font1, 185, 10, 11)) {
        	contentStream.newLineAtOffset(0, -15);
        	contentStream.showText(line);
        }
        contentStream.endText();
		
		// diplomas of the consultant
        contentStream.drawImage(blueStick,315,80,15,180);
        contentStream.setNonStrokingColor(white);
        contentStream.addRect(330, 80, 220, 180);
        contentStream.fill();
        contentStream.beginText();
        contentStream.newLineAtOffset(340, 240);
        contentStream.setFont( font1, 15 );
        contentStream.setNonStrokingColor(lightblue);
        contentStream.showText(cutText(mission.getConsultant().getFirstname()+" "+anonymiseLastname(mission.getConsultant().getLastname()),font1,210,15));
        contentStream.newLineAtOffset(15, -15);
        contentStream.setFont( font1, 10 );
        contentStream.setNonStrokingColor(black);
        Integer exp = mission.getLastVersion().getConsultantStartXp();
        String text;
        switch (exp) {
        	case 0:
        		text = "Jeune diplômé";
        		break;
        	case 1:
        		text = "Un an d'expérience";
        		break;
        	default:
        		text = exp + " ans d'expérience";
        }
        contentStream.showText(text);
        contentStream.newLineAtOffset(0, -5);
        
        
        int limitForDiploma = 5;
        Set<Diploma> diplomas =  mission.getConsultant().getDiplomas();
        if( diplomas != null && diplomas.size() >= 0 ) {
	        for(Diploma diploma : diplomas ) {
		        for(String line : separateLines(diploma.getEntitled(), font1, 185, 10, 5)) {
		        	contentStream.newLineAtOffset(0, -15);
		        	contentStream.showText(line);
		        	limitForDiploma += 15;
		        	if(limitForDiploma >= 140)
		        		break;
		        }
		        if(limitForDiploma >= 140)
	        		break;
		        for(String line : separateLines(diploma.getEstablishment(), font1, 185, 10, 5)) {
		        	contentStream.newLineAtOffset(0, -15);
		        	contentStream.showText(line);
		        	limitForDiploma += 15;
		        	if(limitForDiploma >= 140)
		        		break;
		        }
		        contentStream.newLineAtOffset(0, -5);
		        limitForDiploma += 5;
		        if(limitForDiploma >= 140)
	        		break;
	        }
        }
        contentStream.endText();
		
		// skills of the missions
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
        for(Project project : projects ) {
        	
        	for(Skill skill : project.getSkills()) {
        		
        		if(counter < 20 && counter >= 10) {
        			int cx = 588+115;
        			int cyBis  = cy + 150;
	        		contentStream.setNonStrokingColor(yellow);
	                createBubble(contentStream, 2, cx, cyBis);
        		}
        		else if(counter < 10) {
        			int cx = 588;
        			contentStream.setNonStrokingColor(yellow);
	                createBubble(contentStream, 2, cx, cy);
        		}
                
        		contentStream.setNonStrokingColor(black);
        		
		        for(String line : separateLines(skill.getLabel(), font1, 95, 10, 10)) {
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
		
		
		// Version note
        if(mission.getLastVersion().getVersionDate()!=null) {
	        contentStream.setNonStrokingColor(black);
	        contentStream.beginText();
	        contentStream.newLineAtOffset(10, 10);
	        contentStream.setFont( font1, 10 ); 
	        contentStream.showText(mission.getLastVersion().getVersionDate().format(DateTimeFormatter.ofPattern("dd MM yyyy")));
	        contentStream.endText();
        }
		
		// Bubbles
        int rayon = 28;
        int cx1 = 420;
        int cy1 = 40;
        int cx2 = 520;
        int cx3 = 620;
        int cx4 = 720;
        
        // Contract bubble
        contentStream.setNonStrokingColor(white);
        createBubble(contentStream, rayon, cx1, cy1);
        contentStream.drawImage(contractIcon, 411, 45, contractIcon.getWidth()/3, contractIcon.getHeight()/3);
        contentStream.setNonStrokingColor(black);
        contentStream.setFont( font1, 8 );
        showCenteredText(contentStream, modifyText(mission.getLastVersion().getContractType().name()), cx1, 35);
        
        
        // Team bubble
        contentStream.setNonStrokingColor(white);
        createBubble(contentStream, rayon, cx2, cy1);
        contentStream.drawImage(teamIcon,512,45,teamIcon.getWidth()/3,teamIcon.getHeight()/3);
        contentStream.setNonStrokingColor(black);
        contentStream.setFont( font1, 8 );
        showCenteredText(contentStream,modifyText("Taille d'équipe : "+mission.getLastVersion().getTeamSize().toString()),cx2,35);
		
		// Duration bubble
        contentStream.setNonStrokingColor(white);
        createBubble(contentStream, rayon, cx3, cy1);
        contentStream.drawImage(durationIcon, 610, 45, durationIcon.getWidth()/3, durationIcon.getHeight()/3);
        contentStream.setNonStrokingColor(black);
        contentStream.setFont( font1, 8 );
        int durationDay = 0;
        int durationMonth = 0;
        int durationYear = 0;
        for(Project project : projects ) {
        	if(project.getBeginDate() != null && project.getEndDate() != null) {
	        	Period period = Period.between(project.getBeginDate(), project.getEndDate());
	        	durationDay +=  period.getDays();
	        	durationMonth += period.getMonths();
	        	durationYear += period.getYears();
        	}
        	
        }
        showCenteredText(contentStream,modifyText(durationToText(durationDay,durationMonth, durationYear)),cx3,35);
		
		// Localization bubble
        contentStream.setNonStrokingColor(white);
        createBubble(contentStream, rayon, cx4, cy1);
        contentStream.drawImage(localizationIcon,709,45,localizationIcon.getWidth()/3,localizationIcon.getHeight()/3);
        contentStream.setNonStrokingColor(black);
        contentStream.setFont( font1, 8 );
        showCenteredText(contentStream,modifyText(mission.getLastVersion().getCity()+", "+mission.getLastVersion().getCountry()),cx4,35);
	
        contentStream.close();
	}
	
	/**
	 * Function that takes a PDFBox document and saves it as a pdf file
	 * @param document  PDDocument from PDFBox that represents a pdf document
	 * @throws IOException if the file was not saved
	 */
	public void saveFinalPDF(PDDocument document) throws IOException {
		document.save("pdf\\fichesMissionsEtProjets.pdf");
        document.close();
	}
	 
	
	/**
	 * Function that takes a String as argument and slice it in several lines so that each line has a length inferior to maxWidth  with the given font
	 * @param text String that contains the text to slice
	 * @param font PDFont used for the text
	 * @param maxWidth maximal length of a line
	 * @param fontSize size of the font
	 * @return a List<String> that contains the sliced parts of the text
	 * @throws IOException Text can't be cut
	 */
	private static List<String> separateLines(String text,PDFont font, int maxWidth,int fontSize, int maxLines) throws IOException{
		
		if(text == null)
			return new ArrayList<String>();
		text = text.replace("\n", " ").replace("\r", " ");
		int end1 = -1 ;
        int end2 = 0;
        List<String> lines = new ArrayList<String>();
        while(text.length()>0 && lines.size()<maxLines) {
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
    
	/**
	 * Function that displays a given text using the stream of a pdf page centered on the point c(cx, cy)
	 * @param contentStream PDPageContentStream of the pdf page
	 * @param text text to display
	 * @param cx x coordinate from c
	 * @param cy y coordinate from c
	 * @throws IOException the given String is null
	 */
	private static void showCenteredText(PDPageContentStream contentStream, String text,int cx,int cy) throws IOException {
		List<String> lines = separateLines(text, PDType1Font.HELVETICA, 55, 8, 3);
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
	
	/**
	 * Function that takes a String and given its characteristics, shortens it so that it doesn't exceed a given length
	 * @param text text to shorten
	 * @param font PDFont used for the font
	 * @param maxWidth maximal length of the text
	 * @param fontSize size of the font
	 * @return the shortened String if it's too long with ... at the end
	 * @throws IOException the given String is null
	 */
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
	
	/**
	 * Function that modifies a String by replacing it by a corresponding one
	 * @param text text to replace
	 * @return French translation of the given word
	 */
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
	
	/**
	 * Function that given a duration in days, months and years in numbers will create a String from it for the PDF with the 2 biggest or the biggest unit
	 * @param days part of the duration in days
	 * @param months part of the duration in months
	 * @param years part of the duration in years
	 * @return a string that gives the duration with the 2 biggest or the biggest unit
	 */
	private static String durationToText(Integer days, Integer months, Integer years) {
		
		if(days+months+years < 0 ) {
			return "0 jour";
		}
		else {
			Integer weeks = Integer.valueOf(days/7) ;
			days = days - 7*weeks ;
			String duration = "";
			if( years >=1 ) {
				if(years==1) 
					duration += years.toString()+" année, ";
				else
					duration += years.toString()+" années, ";
			}
			if(months>=1 ) {
				duration += months.toString()+" mois, ";
			}
			if( weeks>=1 && years == 0 ) {
				if(weeks == 1)
					duration += weeks.toString()+" semaine, ";
				else
					duration += weeks.toString()+" semaines, ";
			}
			if( days>0 && months+years == 0 ) {
				if( days==1 )
					duration += days.toString()+" jour  ";
				else
					duration += days.toString()+" jours  ";
			}
			
			duration = duration.substring(0, duration.length()-2);
			int index = duration.lastIndexOf(", ");
			if(index != -1)
				duration = duration.substring(0,index)+" et "+duration.substring(index+2) ;
			return duration;
		}
	}
	
	/**
	 * Function that takes a String and keeps only the first char and adds a point
	 * @param lastname Given last name
	 * @return last name with only the first letter followed by a point
	 */
	private String anonymiseLastname(String lastname) {
		
		return lastname.charAt(0)+"." ;
	}
	
	/**
	 * Function 
	 * @param contentStream stream for a PDF page
	 * @param radius radius of the circle
	 * @param centerX x-coordinate of the center of the circle
	 * @param centerY y-coordinate of the center of the circle
	 * @throws IOException The circle wasn't added to the PDF page
	 */
	private void createBubble(PDPageContentStream contentStream, int radius, int centerX, int centerY) throws IOException {
		
		float constantForCircleWithBezierCurve = (float) 0.551915024494; // constant to make a circle for Bezier Curves
		
        contentStream.moveTo(centerX - radius, centerY);
        contentStream.curveTo(centerX - radius, centerY + constantForCircleWithBezierCurve*radius, centerX - constantForCircleWithBezierCurve*radius, centerY + radius, centerX, centerY + radius);
        contentStream.curveTo(centerX + constantForCircleWithBezierCurve*radius, centerY + radius, centerX + radius, centerY + constantForCircleWithBezierCurve*radius, centerX + radius, centerY);
        contentStream.curveTo(centerX + radius, centerY - constantForCircleWithBezierCurve*radius, centerX + constantForCircleWithBezierCurve*radius, centerY - radius, centerX, centerY - radius);
        contentStream.curveTo(centerX - constantForCircleWithBezierCurve*radius, centerY - radius, centerX - radius, centerY - constantForCircleWithBezierCurve*radius, centerX - radius, centerY);
        contentStream.fill();
	}
}
