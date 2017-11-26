package tb.com.FileDemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SpringBootApplication
public class FileDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileDemoApplication.class, args);
    }

}

@Configuration
class WebSocketConfiguration {

    @Bean
    public PublishSubscribeChannel incomingFilesChannel(){
        return new PublishSubscribeChannel();
    }

    @Bean

    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public StandardIntegrationFlow incomingFileFlow(@Value("file:C:\\projekty\\FileDemo\\in") File file){
        return IntegrationFlows.from(Files.inboundAdapter(file).autoCreateDirectory(false),
                p -> p.poller(pm -> pm.fixedRate(1000))
        ).channel(incomingFilesChannel()).get();
    }

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws/files", getWebSocketHandler());
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setOrder(10);
        simpleUrlHandlerMapping.setUrlMap(map);
        return simpleUrlHandlerMapping;
    }

    @Bean
    public WebSocketHandler getWebSocketHandler() {
        WebSocketHandler webSocketHandler = new WebSocketHandler() {
            @Override
            public Mono<Void> handle(WebSocketSession session) {
                ObjectMapper objectMapper = new ObjectMapper();
                ConcurrentHashMap<String, MessageHandler> connections = new ConcurrentHashMap<>();

                class ForwardingMessageHandler implements MessageHandler {

                    private WebSocketSession session;
                    private FluxSink<WebSocketMessage> sink;

                    public ForwardingMessageHandler(WebSocketSession session, FluxSink<WebSocketMessage> sink) {
                        this.session = session;
                        this.sink = sink;
                    }

                    @Override
                    public void handleMessage(Message<?> message) throws MessagingException {
                        File payload = (File) message.getPayload();
                        FileEvent fileEvent = new FileEvent(session.getId(), payload.getAbsolutePath());
                        String value = new String();
                        try {
                            value = objectMapper.writeValueAsString(fileEvent);
                        } catch (JsonProcessingException ex) {
                        }

                        WebSocketMessage textMessage = session.textMessage(value);
                        sink.next(textMessage);
                    }
                }

                Publisher publisher = Flux.create((Consumer<FluxSink<WebSocketMessage>>)
                        sink -> {
                            connections.put(session.getId(), new ForwardingMessageHandler(session,sink));
                            incomingFilesChannel().subscribe(connections.get(session.getId()));
                        }).doFinally(a -> {
                            incomingFilesChannel().unsubscribe(connections.get(session.getId()));
                            connections.remove(session.getId());
                        });

                return session.send(publisher);
            }
        };

        return webSocketHandler;
    }
}

class FileEvent {

    private String sessionId;
    private String path;

    public FileEvent(String sessionId, String path) {
        this.sessionId = sessionId;
        this.path = path;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPath() {
        return path;
    }
}