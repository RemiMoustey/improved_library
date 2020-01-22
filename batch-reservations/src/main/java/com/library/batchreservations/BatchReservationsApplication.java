package com.library.batchreservations;

import com.library.batchreservations.beans.UserBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableFeignClients("com.library.batchreservations")
@EnableScheduling
public class BatchReservationsApplication {

	static List<UserBean> usersList;

	public static void main(String[] args) {
		SpringApplication.run(BatchReservationsApplication.class, args);
		RestTemplate restTemplate = new RestTemplate();
		usersList = Arrays.asList(restTemplate.getForEntity("http://localhost:9002/utilisateurs", UserBean[].class).getBody());
	}

}
