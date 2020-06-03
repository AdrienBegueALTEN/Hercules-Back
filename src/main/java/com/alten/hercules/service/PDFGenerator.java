package com.alten.hercules.service;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.project.Project;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;

import com.itextpdf.layout.element.AreaBreak;


public class PDFGenerator {
	
	
	public static void makeMissionPDF(Mission mission) throws FileNotFoundException, MalformedURLException {
		
		PdfWriter writer = new PdfWriter("C:\\Users\\mfoltz\\Documents\\"+mission.getLastVersion().getTitle()+".pdf");

        
        PdfDocument pdf = new PdfDocument(writer);
        
        
        Document document = new Document(pdf);
        PageOrientationsEventHandler eventHandler1 = new PageOrientationsEventHandler();
        pdf.addEventHandler(PdfDocumentEvent.START_PAGE, eventHandler1);
        PageRotationEventHandler eventHandler2 = new PageRotationEventHandler();
        pdf.addEventHandler(PdfDocumentEvent.START_PAGE, eventHandler2);
        
        

        Image logo = new Image(ImageDataFactory.create("src\\main\\resources\\alten.png"));
        
        logo.setWidth(30);
        logo.setHeight(50);
        
        Paragraph p = new Paragraph().add(logo).add(mission.getLastVersion().getTitle()).setRotationAngle(Math.PI/2);
        
        document.add(p);
        eventHandler2.setRotation(new PdfNumber(90));
        
        

        //eventHandler.setOrientation(new PdfNumber(90));
        
        //doc.add(new Paragraph("A simple page in landscape orientation").setRotationAngle(Math.PI/2).setRelativePosition(100, 0, 0, 0));

        

        document.close();
	}
	
	public static void makeProjectPDF(Project project) throws FileNotFoundException, MalformedURLException {
		
		PdfWriter writer = new PdfWriter("C:\\Users\\mfoltz\\Documents\\"+project.getTitle()+".pdf");

        
        PdfDocument pdf = new PdfDocument(writer);
        
        
        Document document = new Document(pdf);
        
        
        PageOrientationsEventHandler eventHandler = new PageOrientationsEventHandler();
        pdf.addEventHandler(PdfDocumentEvent.START_PAGE, eventHandler);
        
        eventHandler.setOrientation(new PdfNumber(90));

        Image logo = new Image(ImageDataFactory.create("src\\main\\resources\\alten.png"));
        
        logo.setWidth(30);
        logo.setHeight(50);
        
        Paragraph p = new Paragraph("").add(logo).add(project.getTitle());
        
        document.add(p);

        
        document.close();
	}
	
	public static class PageOrientationsEventHandler implements IEventHandler {
        protected PdfNumber orientation = new PdfNumber(0);

        public void setOrientation(PdfNumber orientation) {
            this.orientation = orientation;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            docEvent.getPage().put(PdfName.Rotate, orientation);
        }
    }
	
	private static class PageRotationEventHandler implements IEventHandler {
        private PdfNumber rotation = new PdfNumber(0);

        public void setRotation(PdfNumber orientation) {
            this.rotation = orientation;
        }

        @Override
        public void handleEvent(Event currentEvent) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
            docEvent.getPage().put(PdfName.Rotate, rotation);
        }
    }
	
}
