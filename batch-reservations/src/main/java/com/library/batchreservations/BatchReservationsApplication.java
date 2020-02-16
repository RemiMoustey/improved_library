package com.library.batchreservations;

import com.library.batchreservations.beans.BookBean;
import com.library.batchreservations.beans.LoanBean;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableFeignClients("com.library.batchreservations")
@EnableScheduling
public class BatchReservationsApplication {

	static List<BookBean> booksList;

	private static ReservationBean createUpdatedReservation(ReservationBean reservationToUpdate) {
	    ReservationBean updatedReservation = new ReservationBean();
        updatedReservation.setId(reservationToUpdate.getId());
        updatedReservation.setBookId(reservationToUpdate.getBookId());
        updatedReservation.setUserId(reservationToUpdate.getUserId());
        updatedReservation.setPriority(reservationToUpdate.getPriority() - 1);
        if (updatedReservation.getPriority() == 0) {
            Calendar calendarReservation = Calendar.getInstance();
            calendarReservation.setTime(Calendar.getInstance().getTime());
            calendarReservation.add(Calendar.DAY_OF_YEAR, 2);
            calendarReservation.add(Calendar.HOUR, 1);
            updatedReservation.setDeadline(calendarReservation.getTime());
        }
        return updatedReservation;
    }

    private static void updateReservationList(List<ReservationBean> reservationsListToUpdate, ReservationBean reservation, BookBean book) throws MessagingException {
	    RestTemplate restTemplate = new RestTemplate();
        if(reservationsListToUpdate.size() > 0) {
            for (ReservationBean reservationToUpdate : reservationsListToUpdate) {
                ReservationBean updatedReservation = createUpdatedReservation(reservationToUpdate);
                if (updatedReservation.getPriority() == 0) {
                    UserBean user = restTemplate.getForObject("http://localhost:9002/utilisateur_id/" + updatedReservation.getUserId(), UserBean.class);
                    readPropertiesAndSend(user.getEmail(), book.getTitle());
                }
                Map<String, String> paramsPut = new HashMap<String, String>();
                paramsPut.put("id", Integer.toString(reservation.getId()));
                restTemplate.put("http://localhost:9004/priorite_baisse_batch/{id}", updatedReservation, paramsPut);
            }
        } else {
            viewListReservationsForIncrement(book);
        }
    }

    public static boolean isLent(List<LoanBean> loansList, ReservationBean reservation) {
        for(LoanBean loan : loansList) {
            if(loan.getUserId() == reservation.getUserId() && loan.getBookId() == reservation.getBookId()) {
                return true;
            }
        }
        return false;
    }

	@Scheduled(cron = "0 */1 * * * *")
	private static void analyseReservations() throws MessagingException {
        RestTemplate restTemplate = new RestTemplate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Calendar.getInstance().getTime());
        calendar.add(Calendar.HOUR, 1);
        for(BookBean book : booksList) {
            List<ReservationBean> reservationsList = Arrays.asList(restTemplate.getForEntity("http://localhost:9004/reservations/" + book.getId(), ReservationBean[].class).getBody());
            if(reservationsList.size() > 0) {
                for(ReservationBean reservation : reservationsList) {
                    List<LoanBean> loansList = Arrays.asList(restTemplate.getForEntity("http://localhost:9003/prets/" + reservation.getUserId(), LoanBean[].class).getBody());
                    if(reservation.getDeadline() != null && reservation.getDeadline().before(calendar.getTime()) || isLent(loansList, reservation)) {
                        Map<String, String> paramsDelete = new HashMap<String, String>();
                        paramsDelete.put("id", Integer.toString(reservation.getId()));
                        restTemplate.delete("http://localhost:9004/suppression_reservation_batch/{id}", paramsDelete);
                        if (!isLent(loansList, reservation)) {
                            List<ReservationBean> reservationsListToUpdate = Arrays.asList(restTemplate.getForEntity("http://localhost:9004/reservations/" + book.getId(), ReservationBean[].class).getBody());
                            updateReservationList(reservationsListToUpdate, reservation, book);
                        }
                    }
                }
            }
        }
	}

    private static void viewListReservationsForIncrement(BookBean book) {
        BookBean updatedBook = new BookBean();
        updatedBook.setId(book.getId());
        updatedBook.setAuthor(book.getAuthor());
        updatedBook.setTitle(book.getTitle());
        updatedBook.setPublicationYear(book.getPublicationYear());
        updatedBook.setIsbn(book.getIsbn());
        updatedBook.setNumberOfPages(book.getNumberOfPages());
        updatedBook.setPublisher(book.getPublisher());
        updatedBook.setCopies(book.getCopies() + 1);
        updatedBook.setImage(book.getImage());
        Map<String, String> paramsPut = new HashMap<String, String>();
        paramsPut.put("id", Integer.toString(book.getId()));
        new RestTemplate().put("http://localhost:9001/stock_monte_batch/{id}", updatedBook, paramsPut);
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

    private static void readPropertiesAndSend(String mailReader, String titleBook) throws MessagingException {
		Properties properties = BatchReservationsApplication.load("C:\\p10\\improved_library\\batch-reservations\\src\\main\\resources\\com.library.batchreservations\\configuration.properties");
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
