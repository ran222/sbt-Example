package yuan.example.akka.remote.hello

/**
 *
 * Created with IntelliJ IDEA.
 * User: yuanwei
 * Date: 2015.12.8 0008 15:10
 * To change this template use File | Settings | File Templates.
 */
trait Request
trait MathRequest extends Request
trait Response
trait MathResponse extends Response

case class Echo(m:Any) extends MathRequest
case class IntMathRequest(o:Operate.Value,m:Int*) extends MathRequest

case class EchoResponse(m:Any) extends MathResponse
//case class IntMathResponse(o:Operate.Value,m:Int,n:Int,r:Double) extends MathRequest
case class IntMathResponse(o:Operate.Value,r:Double,m:Int*) extends MathRequest

object Operate extends Enumeration{
	val ECHO=Value("ECHO")
	val ADD=Value("+")
	val SUBTRACT=Value("-")
	val MULTIPLY=Value("*")
	val DIVIDE=Value("/")
}

