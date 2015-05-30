
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.concurrent.duration.Duration;
import taptwitterstreamj.application.TweetMaster;
import taptwitterstreamj.infrastructure.messaging.FilterMessage;
import taptwitterstreamj.infrastructure.messaging.PublishMessage;

import static org.junit.Assert.assertEquals;

public class TapTwitterStreamJTest {
//
//    static ActorSystem system;
//
//    @BeforeClass
//    public static void setup() {
//        system = ActorSystem.create();
//    }
//
//    @AfterClass
//    public static void teardown() {
//        system.shutdown();
//        system.awaitTermination(Duration.create("10 seconds"));
//    }
//
//    @Test
//    public void testSetGreeter() {
//        new JavaTestKit(system) {{
//            final TestActorRef<TweetMaster> greeter =
//                    TestActorRef.create(system, Props.create(TweetMaster.class), "greeter1");
//
//            //greeter.tell(new TweetMaster.WhoToGreet("testkit"), getTestActor());
//
//           // assertEquals("hello, testkit", greeter.underlyingActor().greeting);
//        }};
//    }
//
//    @Test
//    public void testGetGreeter() {
//        new JavaTestKit(system) {{
//
//            final ActorRef greeter = system.actorOf(Props.create(TweetMaster.class), "greeter2");
//
//            //greeter.tell(new TweetMaster.WhoToGreet("testkit"), getTestActor());
//            //greeter.tell(new TweetMaster.Greet(), getTestActor());
//
//            final TweetMaster tweetMaster = expectMsgClass(TweetMaster.class);
//
//            new Within(duration("10 seconds")) {
//                protected void run() {
//                    //assertEquals("hello, testkit", tweetMaster.message);
//                }
//            };
//        }};
//    }
}
