package yuan.example.akka.rpcj.server;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import yuan.example.akka.rpcj.example.ExampleInterface;
import yuan.example.akka.rpcj.example.ExampleInterfaceImpl;

public class ServerMain {

	public static void main(String[] args) {
		final Config config = ConfigFactory
				.parseString("akka.remote.netty.tcp.port=" + 2551)
				.withFallback(
						ConfigFactory
								.parseString("akka.cluster.roles = [RpcServer]"))
				.withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("EsbSystem", config);
		
		// Server 加入发布的服务
		Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();
		beans.put(ExampleInterface.class, new ExampleInterfaceImpl());
		system.actorOf(Props.create(RpcServerActor.class, beans), "rpcServer");
	}
}
