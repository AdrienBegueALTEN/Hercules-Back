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

/**
 * Class that contains elements to manage eml files in the project.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class EmlFileUtils {
	

	private static final Logger logger = LoggerFactory.getLogger(EmlFileUtils.class);
	/**
	 * eml extension
	 */
	private static final String EXTENSION = ".eml";
	
	@Bean
	public static JavaMailSender javaMailSender() {
	    return new JavaMailSenderImpl();
	}
	
	/**
	 * Generates an eml file
	 * @param from Sender
	 * @param to Receiver
	 * @param subject subject of the mail
	 * @param body text of the mail
	 * @param fileName name of the eml file
	 * @return An eml file
	 */
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
	
	/**
	 * Produces an eml file for a mission with a link
	 * @param mission Mission to send
	 * @return An eml file
	 */
	public static Optional<File> genereateEmlFileWithMissionLink(Mission mission) {
		final String from = mission.getConsultant().getManager().getEmail();
		final String to =  mission.getConsultant().getEmail();
		final String customer = mission.getCustomer().getName();
		final String subject = "Fiche mission '" + customer + "' à compléter";
		final String manager = mission.getConsultant().getManager().getFirstname() + " " + mission.getConsultant().getManager().getLastname();
		final String link = AppConst.MISSION_SHEET_URL + JwtUtils.generateMissionToken(mission);
		final String body =
				"<p>Bonjour,</p>" +
				"<p>Merci de bien vouloir compléter la <a href=\"" + link + "\">fiche mission</a>' correspondant à votre dernière mission chez " + customer + "'.</p>" +
				"<p>Ce lien ne restera accessible pendant <b>30 jours</b>.</p>" +
				"<p>Cordialement,</p>" +
				"<p>" + manager + ".</p>";
		final String fileName = 
				mission.getConsultant().getFirstname() + mission.getConsultant().getLastname() + customer;
		return genereateEmlFile(from, to, subject, body, fileName);
	}
	
	/**
	 * Produces an eml file for an user with a link for changing password
	 * @param targetedUser user that needs password's reset
	 * @return An eml file
	 */
	public static Optional<File> genereateEmlFileWithPasswordCreationLink(AppUser targetedUser) {
		AppUser loggedUser = (AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		final String from = loggedUser.getEmail();
		final String to =  targetedUser.getEmail();
		final String subject = "Nouveau mot de passe compte Hercules";
		final String link = AppConst.LOGIN_URL + JwtUtils.generatePasswordCreationToken(targetedUser);
		final String body =
				"<p>Bonjour,</p>" +
				"<p>Voici un <a href=\"" + link + "\">lien</a> vous permettant de définir un nouveau mot de passe pour votre compte Hercules.</p>" +
				"<p>Ce lien ne restera accessible que pendant <b>24h</b>.</p>" +
				"<p>Cordialement,</p>" +
				"<p>" + loggedUser.getFirstname() + " " + loggedUser.getLastname() + ".</p>";
		final String fileName = 
				targetedUser.getFirstname() + targetedUser.getLastname();
		return genereateEmlFile(from, to, subject, body, fileName);
	}
	
}
