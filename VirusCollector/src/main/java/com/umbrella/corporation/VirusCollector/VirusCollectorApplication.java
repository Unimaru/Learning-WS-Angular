package com.umbrella.corporation.VirusCollector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.corporation.VirusCollector.domain.Virus;
import com.umbrella.corporation.VirusCollector.service.VirusCollectorService;
import org.reactivestreams.Publisher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.time.Duration;
import java.util.HashMap;
import java.util.function.Consumer;

@SpringBootApplication
public class VirusCollectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirusCollectorApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(VirusCollectorService virusCollectorService){
		return args -> {
			virusCollectorService.save(new Virus(1L, "T Virus",true));
			virusCollectorService.save(new Virus(2L, "A Virus", true));
			virusCollectorService.save(new Virus(3L, "Abyss Virus", true));
		};
	}
}

@Configuration
class WebSocketConfiguration{

	@Bean
	public WebSocketHandlerAdapter wsha(){
		return new WebSocketHandlerAdapter();
	}

	@Bean
	public HandlerMapping hm(){
		SimpleUrlHandlerMapping suhm = new SimpleUrlHandlerMapping();
		suhm.setOrder(10);
		HashMap<String, WebSocketHandler> urlMap = new HashMap<>();
		urlMap.put("/ws/files", wsh());
		suhm.setUrlMap(urlMap);
		return suhm;
	}

	@Bean
	public WebSocketHandler wsh(){

		WebSocketHandler handler = new WebSocketHandler() {
			@Override
			public Mono<Void> handle(WebSocketSession session) {
				ObjectMapper om = new ObjectMapper();

				Publisher publisher = Flux.generate((Consumer<SynchronousSink<FileEvent>>) synchronousSink -> {
					synchronousSink.next(new FileEvent(String.valueOf(System.currentTimeMillis()), "/a/b/c"));
				})
						.map(a -> {
							try{
								return om.writeValueAsString(a);
							}catch (JsonProcessingException e){}
							return null;
							})
						.map(a -> session.textMessage(a.toString()))
						.delayElements(Duration.ofSeconds(1L));

				return session.send(publisher);
			}
		};

		return handler;
	}
}

class FileEvent{
	String sessionId;
	String path;

	public FileEvent(String sessionId, String path) {
		this.sessionId = sessionId;
		this.path = path;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
