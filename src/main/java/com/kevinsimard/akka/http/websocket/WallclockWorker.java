package com.kevinsimard.akka.http.websocket;

import akka.actor.ActorRef;
import java.util.Date;
import java.util.TimerTask;

public class WallclockWorker extends TimerTask {

    private final ActorRef actor;

    public WallclockWorker(ActorRef actor) {
        this.actor = actor;
    }

    @Override
    public void run() {
        String now = new Date().toString();
        actor.tell(now, ActorRef.noSender());
    }
}
