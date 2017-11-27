package tb.umbrella.corporation.fluxflixservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

@SpringBootApplication
public class FluxFlixServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FluxFlixServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner demo(MovieRepository movieRepository){
		return args -> {
			movieRepository.deleteAll().subscribe(null,null, () ->
			Stream.of("Aeon Flux", "Enter the Mono<Void>", "The Fluxinaor",
					"Silance of the Lambdas", "Reactive Mongos on Plane",
					"Y Tu Mono Tambien", "Attack of the Fluxxes", "Back to the future")
					.map(name -> new Movie(UUID.randomUUID().toString(), name, randomGendre()))
					.forEach(movie-> movieRepository.save(movie).subscribe(System.out::println)));
		};
	}

	private String randomGendre(){
		String[] genres = "horror,romcom,drama,action,documentary".split(",");
		return genres[new Random().nextInt(genres.length)];
	}
}

class MovieEvent{
	private Movie movie;
	private Date when;
	private String user;

	public MovieEvent(Movie movie, Date when, String user) {
		this.movie = movie;
		this.when = when;
		this.user = user;
	}

	public MovieEvent() {
	}

	public Movie getMovie() {
		return movie;
	}

	public Date getWhen() {
		return when;
	}

	public String getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "MovieEvent{" +
				"movie=" + movie +
				", when=" + when +
				", user='" + user + '\'' +
				'}';
	}
}

@RestController
@RequestMapping("/movies")
class MovieRestController{

	private final FluxFlixService fluxFlixService;

	@Autowired
	public MovieRestController(FluxFlixService fluxFlixService) {
		this.fluxFlixService = fluxFlixService;
	}

	@GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<MovieEvent> events(@PathVariable String id){
		return fluxFlixService.byId(id)
				.flatMapMany(fluxFlixService::streamStreams);
	}

	@GetMapping
	public Flux<Movie> all(){
		return fluxFlixService.all();
	}

	@GetMapping("/{id}")
	public Mono<Movie> byId(@PathVariable String id){
		return fluxFlixService.byId(id);
	}
}

@Service
class FluxFlixService {

	private final MovieRepository movieRepository;

	public FluxFlixService(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

	public Flux<MovieEvent> streamStreams(Movie movie){
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

		Flux<MovieEvent> events = Flux.fromStream(Stream.generate(() -> new MovieEvent(movie, new Date(),
				randomUser())));

		return Flux.zip(interval, events)
				.map(Tuple2::getT2);
	}

	public Flux<Movie> all(){
		return movieRepository.findAll();
	}

	public Mono<Movie> byId(String id){
		return movieRepository.findOne(id);
	}

	private String randomUser(){
		String[] users = "mkheck, jstrachan,phil_web,starbuxman,javafxpert".split(",");
		return users[new Random().nextInt(users.length)];
	}
}


interface MovieRepository{

	Mono<Movie> save(Movie movie);

	Mono<Void> deleteAll();

	Flux<Movie> findAll();

	Mono<Movie> findOne(String id);
}

@Repository
class MovieRepositoryImpl implements MovieRepository{

	private List<Movie> movies = new ArrayList<>();

	@Override
	public Mono<Movie> save(Movie movie) {
		movies.add(movie);
		return Mono.just(movie);
	}

	@Override
	public Mono<Void> deleteAll() {
		movies = new ArrayList<>();
		return Mono.create(s -> s.success());
	}

	@Override
	public Flux<Movie> findAll() {
		return Flux.fromIterable(movies);
	}

	@Override
	public Mono<Movie> findOne(String id) {
		Flux<Movie> filter = Flux.fromIterable(movies).filter(movie -> movie.getId().equals(id));
		return Mono.from(filter);
	}
}

class Movie{

	private String id;

	private String title, genre;

	public Movie(String id, String title, String genre) {
		this.id = id;
		this.title = title;
		this.genre = genre;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getGenre() {
		return genre;
	}

	@Override
	public String toString() {
		return "Movie{" +
				"id='" + id + '\'' +
				", title='" + title + '\'' +
				", genre='" + genre + '\'' +
				'}';
	}
}