package com.kevinsimard.akka.http.websocket;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import java.time.Duration;

public class Application extends HttpApp {

    public static void main(String... args) throws Exception {
        new Application().startServer("0.0.0.0", 8080);
    }

    @Override
    protected Route routes() {
        return route(
            path("wallclock", () ->
                get(() -> handleWebSocketMessages(wallclockSocketFlow()))
            ),

            pathPrefix("chatroom", () ->
                path(PathMatchers.segment(), username ->
                    get(() -> handleWebSocketMessages(chatRoomSocketFlow(username)))
                )
            )
        );
    }

    private Flow<Message, Message, NotUsed> wallclockSocketFlow() {
        ActorRef actor = systemReference.get()
            .actorOf(Props.create(WallclockActor.class));

        return socketFlow(actor);
    }

    private Flow<Message, Message, NotUsed> chatRoomSocketFlow(String username) {
        ActorRef actor = systemReference.get()
            .actorOf(Props.create(ChatRoomActor.class, username));

        return socketFlow(actor);
    }

    private Flow<Message, Message, NotUsed> socketFlow(ActorRef actor) {
        Source<Message, NotUsed> source = Source
            .<String>actorRef(0, OverflowStrategy.fail())
            .map(message -> (Message) TextMessage.create(message))
            .mapMaterializedValue(textMessage -> {
                actor.tell(textMessage, ActorRef.noSender());

                return NotUsed.getInstance();
            })
            .keepAlive(Duration.ofSeconds(10), () -> TextMessage.create("{}"));

        Sink<Message, NotUsed> sink = Flow.<Message>create()
            .to(Sink.actorRef(actor, PoisonPill.getInstance()));

        return Flow.fromSinkAndSource(sink, source);
    }
}
