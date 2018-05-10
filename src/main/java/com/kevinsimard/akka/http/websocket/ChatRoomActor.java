package com.kevinsimard.akka.http.websocket;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.http.javadsl.model.ws.TextMessage;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomActor extends AbstractActor {

    private static final List<ActorRef> actors = new ArrayList<>();

    private final String username;

    private ActorRef actor;

    public ChatRoomActor(String username) {
        this.username = username;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(ActorRef.class, this::init)
            .match(TextMessage.class, x -> onMessage(x.getStrictText()))
            .build();
    }

    @Override
    public void postStop() {
        actors.remove(actor);
    }

    private void init(ActorRef actor) {
        actors.add(actor);

        this.actor = actor;
    }

    private void onMessage(String message) {
        sendMessage(username + ": " + message);
    }

    private void sendMessage(String message) {
        for (ActorRef actor : actors) {
            actor.tell(message, self());
        }
    }
}
