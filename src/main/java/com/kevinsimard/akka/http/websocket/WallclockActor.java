package com.kevinsimard.akka.http.websocket;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import java.util.Timer;
import java.util.TimerTask;

public class WallclockActor extends AbstractActor {

    private final Timer timer = new Timer();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(ActorRef.class, this::init)
            .build();
    }

    @Override
    public void postStop() {
        timer.cancel();
    }

    private void init(ActorRef actor) {
        TimerTask worker = new WallclockWorker(actor);
        timer.scheduleAtFixedRate(worker, 0, 1000);
    }
}
