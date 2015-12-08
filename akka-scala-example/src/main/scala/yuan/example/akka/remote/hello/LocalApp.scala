package yuan.example.akka.remote.hello

import akka.actor.{ActorLogging, Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory


object LocalApp extends App {
	//val system = ActorSystem("LocalSystem")
	/*val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=13101")
		.withFallback(ConfigFactory.load("remote/hello/hello.conf"))*/
	val config = ConfigFactory.parseString("""
			|akka {
			|  loglevel = "DEBUG"
			|  actor {
			|    provider = "akka.remote.RemoteActorRefProvider"
			|  }
			|  remote {
			|    enabled-transports = ["akka.remote.netty.tcp"]
			|    netty.tcp {
			|      hostname = "127.0.0.1"
			|      port = 0
			|    }
			|  }
			|  log-sent-messages = on
			|  log-received-messages = on
			|}
		""".stripMargin)
	println("akka.remote.netty.tcp.port="+config.getInt("akka.remote.netty.tcp.port"))

	val system = ActorSystem("LocalSystem",config)
	val localActor = system.actorOf(Props[LocalActor], name = "LocalActor") // the local actor

	localActor ! "START" // start the action
}

class LocalActor extends Actor with ActorLogging{
	// create the remote actor
	val remote = context.actorSelection("akka.tcp://HelloRemoteSystem@127.0.0.1:12101/user/RemoteActor")
	var counter = 0

	def receive = {
		case "START" =>
			remote ! "Hello from the LocalActor"
		case msg: String =>{
			//println(s"LocalActor received message: '$msg'")
			log.debug("LocalActor received message:{}",msg)

			if (counter % 2==0) {
				sender ! "Hello back to you"
			}else{
				Thread.sleep(1000L)
				sender ! "counter"+counter
			}
			counter += 1
		}
		case x => {
			log.debug("RemoteActor received message:{}",x)
			sender!x
		}

	}
}
