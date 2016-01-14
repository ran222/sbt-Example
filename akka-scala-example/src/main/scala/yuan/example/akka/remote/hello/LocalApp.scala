package yuan.example.akka.remote.hello

import akka.actor.{ActorLogging, Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import yuan.example.akka.remote.calculator.{Subtract, Add}
import scala.concurrent.duration._

import scala.util.Random


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
			|#  log-sent-messages = on
			|# log-received-messages = on
			|}
		""".stripMargin)
	println("akka.remote.netty.tcp.port="+config.getInt("akka.remote.netty.tcp.port"))

	val system = ActorSystem("LocalSystem",config)
	val localActor = system.actorOf(Props[LocalActor], name = "LocalActor") // the local actor

	//localActor ! "START" // start the action

	import system.dispatcher
	system.scheduler.schedule(1.second, 2.second) {
		localActor!"RANDOM"
	}
}

class LocalActor extends Actor with ActorLogging{
	// create the remote actor
	val remote = context.actorSelection("akka.tcp://HelloRemoteSystem@127.0.0.1:12101/user/RemoteActor")
	var counter = 0

	def receive = {
		case "START" =>
			remote ! "Hello from the LocalActor"
		case "RANDOM" =>
			val r=Random.nextInt(100) % 4

			if (r == 0)     remote ! IntMathRequest(Operate.ADD,Random.nextInt(100),Random.nextInt(100))
			else if (r == 1)remote ! IntMathRequest(Operate.SUBTRACT,Random.nextInt(100),Random.nextInt(100))
			else if (r == 1)remote ! IntMathRequest(Operate.MULTIPLY,Random.nextInt(100),Random.nextInt(100))
			else if (r == 1)remote ! IntMathRequest(Operate.DIVIDE,Random.nextInt(100),1+Random.nextInt(20))
			else            remote ! Echo(Random.nextInt(1000))

		case msg: String =>{
			//println(s"LocalActor received message: '$msg'")
			log.debug("LocalActor received message:{}",msg)
			counter += 1
			if (counter % 2==0) {
				sender ! "Hello back to you"
			}else{
				Thread.sleep(1000L)
				sender ! "counter"+counter
			}

		}
		case Echo(m:Any)=>{
			Thread.sleep(3000L)
			log.debug("Echo:{}",m)
			//sender!m
		}
		case IntMathResponse(o:Operate.Value,r:Double,m:Int,n:Int) => {
			log.info("receive: {} {} {}={}",m,o,n,r)
		}
		case x => {
			log.debug("OTHER:{}",x)
			sender!x
		}

	}
}
