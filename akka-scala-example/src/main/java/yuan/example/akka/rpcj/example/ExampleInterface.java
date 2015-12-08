package yuan.example.akka.rpcj.example;

import java.io.Serializable;

public interface ExampleInterface extends Serializable{

	public String sayHello(String name);
}
