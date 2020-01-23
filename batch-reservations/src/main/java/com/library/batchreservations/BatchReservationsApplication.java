package com.library.batchreservations;

import com.library.batch.BatchApplication;
import com.library.batchreservations.beans.BookBean;
import com.library.batchreservations.beans.ReservationBean;
import com.library.batchreservations.beans.UserBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableFeignClients("com.library.batchreservations")
@EnableScheduling
public class BatchReservationsApplication {

	static List<BookBean> booksList;

	@Scheduled(cron = "0 */1 * * * *")
	private static void analyseReservations() throws MessagingException {
		updatePriority();
		findUserToSendMail();
	}

	private static void updatePriority() {
		RestTemplate restTemplate = new RestTemplate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		for(BookBean book : booksList) {
			List<ReservationBean> reservationsList = Arrays.asList(restTemplate.getForEntity("http://localhost:9004/reservations/" + book.getId(), ReservationBean[].class).getBody());
			if(reservationsList != null) {
				for (ReservationBean reservation : reservationsList) {
					if (reservation.getDeadline() != null && reservation.getDeadline().before(calendar.getTime())) {
						Map<String, String> paramsDelete = new HashMap<String, String>();
						paramsDelete.put("id", Integer.toString(reservation.getId()));
						restTemplate.delete("http://localhost:9004/suppression_reservation_batch/{id}", paramsDelete);
//						Map<String, String> paramsPut = new HashMap<String, String>();
//						paramsPut.put("bookId", Integer.toString(reservation.getBookId()));
//						restTemplate.put("http://localhost:9004/priorite_baisse_batch/{bookId}", paramsPut);
						break;
					}
				}
			}
		}
	}

	private static void findUserToSendMail() throws MessagingException {
		RestTemplate restTemplate = new RestTemplate();
		for(BookBean book : booksList) {
			List<ReservationBean> reservationsList = Arrays.asList(restTemplate.getForEntity("http://localhost:9004/reservations/" + book.getId(), ReservationBean[].class).getBody());
			if(reservationsList != null) {
				for (ReservationBean reservation : reservationsList) {
					if (reservation.getPriority() == 0) {
						UserBean user = restTemplate.getForObject("http://localhost:9002/utilisateur_id/" + reservation.getUserId(), UserBean.class);
						readPropertiesAndSend(user.getEmail(), book.getTitle());
					}
				}
			}
		}
	}

	private static void readPropertiesAndSend(String mailReader, String titleBook) throws MessagingException {
		Properties properties = BatchApplication.load("C:\\remi\\projet10\\improved_library\\batch-reservations\\src\\main\\resources\\com.library.batchreservations\\configuration.properties");
		Session session = Session.getDefaultInstance(properties);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(properties.getProperty("mail.smtp.user")));
		message.setSubject(titleBook + " vous attend à la bibliothèque");
		message.setContent("Bonjour,\n\nSuite à votre réservation du livre " + titleBook + ", nous vous informons que ce dernier est disponible à la bibliothèque dans un délai de 48 heures à compter de l'envoi de ce message. Passé ce délai, votre réservation sera expirée.", "text/plain");
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailReader));

		Transport transport = session.getTransport("smtp");
		transport.connect(properties.getProperty("mail.smtp.host"), properties.getProperty("mail.smtp.user"), properties.getProperty("mail.smtp.password"));
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(BatchReservationsApplication.class, args);
		RestTemplate restTemplate = new RestTemplate();
		booksList = Arrays.asList(restTemplate.getForEntity("http://localhost:9001/livres", BookBean[].class).getBody());
		analyseReservations();
	}
}
