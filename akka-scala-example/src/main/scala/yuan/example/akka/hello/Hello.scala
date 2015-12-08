package yuan.example.akka.hello

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import akka.util.Timeout

import scala.concurrent.Await

/**
 *
 * Created with IntelliJ IDEA.
 * User: yuanwei
 * Date: 2015.12.4 0004 15:46
 * To change this template use File | Settings | File Templates.
 */
object Hello {

	//创建Actor实例需要通过 ActorSystem
	def main(args: Array[String]) {
		val system = ActorSystem("HelloSystem")
		val hello = system.actorOf(Props[HelloActor], name = "HelloActor")
		//val hello1 = system.actorOf(Props[HelloActor])
		//val hello2 = system.actorOf(Props(new HelloActor()))

		//如果要在 Actor 中继续创建子 Actor，需要使用内置的 ActorContext 对象。
		//context.actorOf(Props[children], name = "children")
		//如果要创建远程 Actor，需要通过 actorSelection 方法，原 actorFor 方法不再使用。
		//context.actorSelection("akka.tcp://HelloRemoteSystem@127.0.0.1:5150/user/RemoteActor")

		//发消息
		//巨简单，就是一个!，可以发送任意类型的消息，此消息是异步的。
		hello ! "bruce"
		hello ! 10086
		hello ! 10086L

		//同步消息的发送需要使用 Future 对象
		implicit val timeout = Timeout(5,TimeUnit.SECONDS)
		hello.tell("sha",ActorRef.noSender)
		//val result = Await.result(future, timeout.duration).asInstanceOf[String]
	}

	 //定义Actor很简单，继承 akka.actor.Actor ，实现receive方法即可。
	class HelloActor extends Actor {
		def receive = {
			case msg: String => println("receive String:" + msg)
			case i: Int => println("receive Int:" + i)
			case i: Long => println("receive Long:" + i)
			case _ => println("unexpected message")
		}
	}
}
