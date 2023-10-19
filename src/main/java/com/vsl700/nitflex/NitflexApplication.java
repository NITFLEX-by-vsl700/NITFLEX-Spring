package com.vsl700.nitflex;

import com.vsl700.nitflex.models.Episode;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.UserDTO;
import com.vsl700.nitflex.repo.EpisodeRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.repo.UserRepository;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan("com.vsl700.nitflex.components") // May come in handy if we're making different scopes and want to
												// isolate some components
public class NitflexApplication {

	public static void main(String[] args) {
		SpringApplication.run(NitflexApplication.class, args);
	}

	@Bean
	public CommandLineRunner usersTest(UserRepository userRepo){
		return args -> {
			userRepo.deleteAll();

			var user = new User("vsl700", "password");
			userRepo.save(user);
			System.out.println("CLR1");
		};
	}

	@Bean
	public CommandLineRunner seriesTest(MovieRepository movieRepo, EpisodeRepository episodeRepo){
		return args -> {
//			movieRepo.deleteAll();
//			episodeRepo.deleteAll();

//			var movie = new Movie("Fast X", Movie.MovieType.Film, "D:\\Movies\\Fast.X.2023");
//			movie.setFilmPath("\\fast.x.mkv");
//			movieRepo.save(movie);

//			var series = new Movie("Wednesday", Movie.MovieType.Series, "D:\\Movies\\Wednesday");
//			movieRepo.save(series);
//			series = movieRepo.findByType(Movie.MovieType.Series).stream().findFirst().orElse(null);

//			assert series != null;
//			var episode1 = new Episode(series.getId(), 1, "\\Wednesday.Episode.1.mkv");
//			episodeRepo.save(episode1);

//			series.getEpisodes().add(episode1);
//			movieRepo.save(series);

			var series = movieRepo.findByType(Movie.MovieType.Series).stream().findFirst().orElse(null);
			assert series != null;
			var episodesList = series.getEpisodes();
			System.out.println(episodesList.size());
			episodesList.forEach(System.out::println);
			System.out.println(series.getAddedAt());

			System.out.println("CLR3");
		};
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
}
