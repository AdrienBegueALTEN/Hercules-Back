package com.alten.hercules.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import com.alten.hercules.consts.AppConst;
import com.alten.hercules.model.mission.Mission;
import com.alten.hercules.model.user.AppUser;
import com.alten.hercules.security.jwt.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmlFileUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(EmlFileUtils.class);
	private static final String EXTENSION = ".eml";
	
	@Bean
	public static JavaMailSender javaMailSender() {
	    return new JavaMailSenderImpl();
	}
	
	public static Optional<File> genereateEmlFile(String from, String to, String subject, String body, String fileName) {
		File emlFile = null;
		try {
			MimeMessage msg = javaMailSender().createMimeMessage();
			msg.setHeader("X-Unsent", "1");
			msg.setFrom(new InternetAddress(from));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	        msg.setSubject(subject);
	        MimeBodyPart content = new MimeBodyPart();
	        content.setText(body, "utf-8", "html");
	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(content);
	        msg.setContent(multipart);
	        emlFile = new File(fileName + EXTENSION);
	        FileOutputStream fos = new FileOutputStream(emlFile);
	        msg.writeTo(fos);
	        fos.close();
		} catch (Exception e) {
			logger.error("Eml file generation failed.", e);
		}
	    return Optional.ofNullable(emlFile);
	}
	
	public static Optional<File> genereateEmlFileWithMissionLink(Mission mission) {
		final String from = mission.getConsultant().getManager().getEmail();
		final String to =  mission.getConsultant().getEmail();
		final String customer = mission.getCustomer().getName();
		final String subject = "Fiche mission '" + customer + "' à compléter";
		final String manager = mission.getConsultant().getManager().getFirstname() + " " + mission.getConsultant().getManager().getLastname();
		final String body =
				"<p>Bonjour,</p>" +
				"<p>Merci de bien vouloir renseigner les informations relatives à votre dernière mission chez '" + customer + "' via cette page :</p>" +
				"<p>" + AppConst.MISSION_SHEET_URL + JwtUtils.generateMissionToken(mission) + "</p>" +
				"<p>Cordialement,</p>" +
				"<p>" + manager + ".</p>";
		final String fileName = 
				mission.getConsultant().getFirstname() + mission.getConsultant().getLastname() + customer;
		return genereateEmlFile(from, to, subject, body, fileName);
	}
	
	public static Optional<File> genereateEmlFileWithPasswordCreationLink(AppUser targetedUser) {
		AppUser loggedUser = (AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		final String from = loggedUser.getEmail();
		final String to =  targetedUser.getEmail();
		final String subject = "Activation de votre compte Hercules";
		final String body =
				"<p>Bonjour,</p>" +
				"<p>Voici le lien d'activation de votre compte Hercules :</p>" +
				"<p>" + AppConst.LOGIN_URL + JwtUtils.generatePasswordCreationToken(targetedUser) + "</p>" +
				"<p>Cordialement,</p>" +
				"<p>" + loggedUser.getFirstname() + " " + loggedUser.getLastname() + ".</p>";
		final String fileName = 
				targetedUser.getFirstname() + targetedUser.getLastname();
		return genereateEmlFile(from, to, subject, body, fileName);
	}
	
}
