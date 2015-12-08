package yuan.example.akka.hello;

import akka.Main;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.ActorRef;

public class AkkaHelloWorld extends UntypedActor{
	public static void main(String[] args){
		Main.main(new String[]{"yuan.example.akka.hello.AkkaHelloWorld"});
	}

	@Override
	public void preStart(){
		final ActorRef greeter=getContext().actorOf(Props.create(Greeter.class), "greeter");
		greeter.tell(Greeter.Msg.GREET, getSelf());
	}

	@Override
	public void onReceive(Object msg){
		if(msg==Greeter.Msg.DONE){
			getContext().stop(getSelf());
		}else{
			unhandled(msg);
		}
	}

	static class Greeter extends UntypedActor {
		public enum Msg {
			GREET, DONE
		}

		@Override
		public void onReceive(Object msg) {
			if (msg == Msg.GREET) {
				System.out.println("Hello World!");
				getSender().tell(Msg.DONE, getSelf());
			} else {
				unhandled(msg);
			}
		}
	}
}

