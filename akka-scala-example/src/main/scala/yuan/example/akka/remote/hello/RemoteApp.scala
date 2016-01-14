package yuan.example.akka.remote.hello

import akka.actor.{ActorLogging, Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import yuan.example.akka.remote.calculator.{SubtractResult, AddResult}

//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.duration._

object RemoteApp extends App {
	//final String port = args.length > 0 ? args[0] : "0"
	//final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
	//	withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
	//	withFallback(ConfigFactory.load("factorial"))

/*	val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=12101")
		.withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
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
				|      port = 12101
				|    }
				|  }
				|#  log-sent-messages = on
				|#  log-received-messages = on
				|}
			""".stripMargin)

	//可以写成
/*	val config = ConfigFactory.parseString("""akka {
		loglevel = "DEBUG"
		actor {
			provider = "akka.remote.RemoteActorRefProvider"
		}
		remote {
			enabled-transports = ["akka.remote.netty.tcp"]
			netty.tcp {
				hostname = "127.0.0.1"
				#port = 12101
			}
		}
	}""")*/

	val system = ActorSystem("HelloRemoteSystem",config)
	//val system = ActorSystem("HelloRemoteSystem",ConfigFactory.load("remote/hello/hello.conf"))
	val remoteActor = system.actorOf(Props[RemoteActor], name = "RemoteActor")

	remoteActor ! "The RemoteActor is alive"


	//implicit ExecutionContext
	//计划50ms后执行函数 (发送当前时间给 testActor） after 50ms
	////system.scheduler.scheduleOnce(50 milliseconds,remoteActor,System.currentTimeMillis)
}

class RemoteActor extends Actor with ActorLogging{
	//val log = Logging(context.system, this)
	/*		override def preStart() = {
				log.debug("Starting")
			}
			override def preRestart(reason: Throwable, message: Option[Any]) {
				log.error(reason, "Restarting due to [{}] when processing [{}]",
					reason.getMessage, message.getOrElse(""))
			}*/

	def receive = {
		case message: String => {
			//println(s"RemoteActor received message '$message'")
			//log.debug(s"RemoteActor received message '$msg'")
			log.debug("RemoteActor received message:{}", message)
			if ("SHUTDOWN".eq(message)) {
				context.system.shutdown()
			}
			sender ! s"Hello from the RemoteActor:$message"
		}
		case Echo(m:Any)=>{
			Thread.sleep(3000L)
			//sender!m

			log.debug("Echo:{}",m)
		}
		case IntMathRequest(o:Operate.Value,m:Int,n:Int) => {
			log.info("receive: {} {} {}",m,o,n)
			var result:Double=0
			o match {
				case Operate.ADD=>result= m + n
				case Operate.SUBTRACT=>result= m-n
				case Operate.MULTIPLY=>result= m * n
				case Operate.DIVIDE=>result= m / n
			}
			sender!IntMathResponse(o,m,n,result)
		}
		case x        =>
			log.debug("OTHER:{}",x)
	}
}

