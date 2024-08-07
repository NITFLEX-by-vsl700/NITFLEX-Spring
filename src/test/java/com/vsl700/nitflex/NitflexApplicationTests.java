package com.vsl700.nitflex;

import com.vsl700.nitflex.components.WebsiteCredentials;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.repo.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NitflexApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void credentialsLoad(@Autowired WebsiteCredentials.Zamunda credentials){
		assertThat(credentials.getPassword()).isNotNull();
	}

	@Test
	@Disabled
	void movieUserReferenceTest(@Autowired MovieRepository movieRepository, @Autowired UserRepository userRepository){
		var movie1 = movieRepository.findAll().get(0);
		var movie2 = movieRepository.findAll().get(1);
		var movie3 = movieRepository.findAll().get(2);
		var user = userRepository.findAll().get(0);

		var tempUser1 = movie1.getRequester();
		var tempUser2 = movie2.getRequester();
		var tempUser3 = movie3.getRequester();

		movie1.setRequester(user);
		movie2.setRequester(user);
		movie3.setRequester(user);

		movie1 = movieRepository.save(movie1);
		movie2 = movieRepository.save(movie2);
		movie3 = movieRepository.save(movie3);

		assertThat(movie1.getRequester()).isEqualTo(user);
		assertThat(movie2.getRequester()).isEqualTo(user);
		assertThat(movie3.getRequester()).isEqualTo(user);

		movie1.setRequester(tempUser1);
		movie2.setRequester(tempUser2);
		movie3.setRequester(tempUser3);

		movie1 = movieRepository.save(movie1);
		movie2 = movieRepository.save(movie2);
		movie3 = movieRepository.save(movie3);

		assertThat(movie1.getRequester()).isEqualTo(tempUser1);
		assertThat(movie2.getRequester()).isEqualTo(tempUser2);
		assertThat(movie3.getRequester()).isEqualTo(tempUser3);
	}

}
