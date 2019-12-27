package com.library.batch;

import com.library.batch.beans.LoanBean;
import com.library.batch.beans.UserBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.List;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableFeignClients("com.library.batch")
public class BatchApplication {

	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();
		List<UserBean> usersList = restTemplate.getForObject("http://localhost:9002/utilisateurs", List.class);
		for(int i = 0; i < usersList.size(); i++) {
			List<LoanBean> loansList = restTemplate.getForObject("http://localhost:9003/prets/" + usersList.get(i).getId(), List.class);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(Calendar.getInstance().getTime());
			if(calendar.getTime().before(loansList.get(i).getDeadline())) {
				
			}
		}
		System.out.println("couu");
	}

}
