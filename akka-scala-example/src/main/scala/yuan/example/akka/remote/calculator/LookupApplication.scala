package yuan.example.akka.remote.calculator

import akka.event.Logging

import scala.concurrent.duration._
import scala.util.Random
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props

object LookupApplication {
	def main(args: Array[String]): Unit = {
		if (args.isEmpty || args.head == "Calculator")
			startRemoteCalculatorSystem()
		if (args.isEmpty || args.head == "Lookup")
			startRemoteLookupSystem()
	}

	def startRemoteCalculatorSystem(): Unit = {
		/*val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=12100")
			.withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname='127.0.0.1'"))
			.withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
			.withFallback(ConfigFactory.load("remote/calc/calculator"))//resources路径一定要加入path*/
		val config=ConfigFactory.parseString("""akka {
				actor {
				  provider = "akka.remote.RemoteActorRefProvider"
				}
				remote {
				  netty.tcp {
				    hostname = "127.0.0.1"
				    port=12100
				  }
				}
			}""")

		println("akka.remote.netty.tcp.port=" + config.getInt("akka.remote.netty.tcp.port"))

		val system = ActorSystem("CalculatorSystem", config)
		system.actorOf(Props[CalculatorActor], "calculator")

		println("Started CalculatorSystem - waiting for messages:"+system)
	}

	def startRemoteLookupSystem(): Unit = {
		//val config = ConfigFactory.load("remote/calc/remoteLookup")//resources路径一定要加入path
		val config=ConfigFactory.parseString("""akka {
				actor {
				  provider = "akka.remote.RemoteActorRefProvider"
				}
				remote {
				  netty.tcp {
				    hostname = "127.0.0.1"
				    port=13100
				  }
				}
			}""")
		println("akka.remote.netty.tcp.port=" + config.getInt("akka.remote.netty.tcp.port"))

		val system = ActorSystem("LookupSystem", config)
		val remotePath = "akka.tcp://CalculatorSystem@127.0.0.1:12100/user/calculator"
		val actor = system.actorOf(Props(classOf[LookupActor], remotePath), "lookupActor")

		println("Started LookupSystem")
		import system.dispatcher
		system.scheduler.schedule(1.second, 1.second) {
			if (Random.nextInt(100) % 2 == 0)
				actor ! Add(Random.nextInt(100), Random.nextInt(100))
			else
				actor ! Subtract(Random.nextInt(100), Random.nextInt(100))
		}
	}
}
