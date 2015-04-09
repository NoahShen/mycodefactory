package org.github.noahshen.mycodefactory.akkatest

import akka.actor.UntypedActor

/**
 * Created by noahshen on 15/4/9.
 */
class Greeter extends UntypedActor {

    static enum Msg {
        GREET, DONE;
    }

    void onReceive(Object msg) {
        if (msg == Msg.GREET) {
            println "Hello World!"
            sender.tell(Msg.DONE, self)
        } else {
            unhandled(msg)
        }
    }

}
