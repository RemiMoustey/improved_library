package com.library.batch;

import com.library.batch.beans.BookBean;
import com.library.batch.beans.LoanBean;
import com.library.batch.beans.UserBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableFeignClients("com.library.batch")
@EnableScheduling
public class BatchApplication {

	static List<UserBean> usersList;

	@Scheduled(cron = "0 0 10 * * *", zone="Europe/Paris")
	public static void analyseLoans() {
		RestTemplate restTemplate = new RestTemplate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		for(int i = 0; i < usersList.size(); i++) {
			List<LoanBean> loansList = Arrays.asList(restTemplate.getForEntity("http://localhost:9003/prets/" + usersList.get(i).getId(), LoanBean[].class).getBody());
			for(int j = 0; j < loansList.size(); j++) {
				if (!loansList.isEmpty() && loansList.get(j).getDeadline().before(calendar.getTime())) {
					BookBean book = restTemplate.getForObject("http://localhost:9001/livres/" + loansList.get(j).getBookId(), BookBean.class);
					try {
						readPropertiesAndSend(usersList.get(i).getEmail(), book.getTitle(), new SimpleDateFormat("dd/MM/yyy").format(loansList.get(j).getDeadline()));
					} catch (MessagingException | IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static Properties load(String filename) {
		Properties properties = new Properties();
		try(FileInputStream input = new FileInputStream(filename)) {
			properties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	public static void readPropertiesAndSend(String mailReader, String titleBook, String deadline) throws MessagingException, IOException {
		Properties properties = BatchApplication.load("C:\\library\\batch\\src\\main\\resources\\com.library.batch\\configuration.properties");
		Session session = Session.getDefaultInstance(properties);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(properties.getProperty("mail.smtp.user")));
		message.setSubject("Attention : Retour en retard du livre " + titleBook);
		message.setContent("Bonjour,\n\nLa date de retour de votre emprunt du livre " + titleBook + " a expiré depuis le " + deadline + ".\n\nNous vous invitons donc à le rapporter en bibliothèque le plus rapidement possible.\n\nBien cordialement,\nLa Bibliothèque.", "text/plain");
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailReader));

		Transport transport = session.getTransport("smtp");
		transport.connect(properties.getProperty("mail.smtp.host"), properties.getProperty("mail.smtp.user"), properties.getProperty("mail.smtp.password"));
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
		RestTemplate restTemplate = new RestTemplate();
		usersList = Arrays.asList(restTemplate.getForEntity("http://localhost:9002/utilisateurs", UserBean[].class).getBody());
		analyseLoans();
	}
}
