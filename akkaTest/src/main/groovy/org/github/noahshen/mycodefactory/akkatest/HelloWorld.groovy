package org.github.noahshen.mycodefactory.akkatest

import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.UntypedActor

/**
 * Created by noahshen on 15/4/9.
 */
class HelloWorld extends UntypedActor {


    void preStart() {
        // create the greeter actor
        final ActorRef greeter = context.actorOf(Props.create(Greeter.class), "greeter");
        // tell it to perform the greeting
        greeter.tell(Greeter.Msg.GREET, self);
    }

    @Override
    public void onReceive(Object msg) {
        if (msg == Greeter.Msg.DONE) {
            // when the greeter is done, stop this actor and with it the application
            context.stop(self);
        } else {
            unhandled(msg)
        }

    }
}
