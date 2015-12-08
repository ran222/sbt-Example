package yuan.example.akka.hello

import akka.actor.{Props, Actor, ActorRef, ActorSystem}

object HengHa extends App {
	val system = ActorSystem("HengHaSystem")
	val ha = system.actorOf(Props[Ha], name = "ha")
	val heng = system.actorOf(Props(new Heng(ha)), name = "heng")

	heng ! "start"
	//等于 heng.tell("start",ActorRef.noSender)




	class Heng(ha: ActorRef) extends Actor {
		def receive = {
			case "start" => ha ! "heng"
			case "ha" =>
				println("哈")
				Thread.sleep(2400L)
				ha ! "heng"
			case _ => println("heng what?")
		}
	}
	class Ha extends Actor {
		def receive = {
			case "heng" =>
				println("哼")
				Thread.sleep(800L)
				sender ! "ha"
			case _ => println("ha what?")
		}
	}
}
