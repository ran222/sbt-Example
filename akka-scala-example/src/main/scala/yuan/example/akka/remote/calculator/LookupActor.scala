package yuan.example.akka.remote.calculator

import scala.concurrent.duration._
import akka.actor._

class LookupActor(path: String) extends Actor with ActorLogging {

  sendIdentifyRequest()

  def sendIdentifyRequest(): Unit = {
    context.actorSelection(path) ! Identify(path)
    import context.dispatcher
    context.system.scheduler.scheduleOnce(3.seconds, self, ReceiveTimeout)
  }

  def receive = identifying

  def identifying: Actor.Receive = {
    case ActorIdentity(`path`, Some(actor)) =>
      context.watch(actor)
      context.become(active(actor))
    case ActorIdentity(`path`, None) => log.error(s"Remote actor not available: $path")
    case ReceiveTimeout              => sendIdentifyRequest()
    case _                           => log.info("Not ready yet")
  }

  def active(actor: ActorRef): Actor.Receive = {
    case op: MathOp => actor ! op
    case result: MathResult => result match {
      case AddResult(n1, n2, r) =>
        log.info("Add result: {} + {} = {}", n1, n2, r)
      case SubtractResult(n1, n2, r) =>
        log.info("Sub result: {} + {} = {}", n1, n2, r)
    }
    case Terminated(`actor`) =>
      log.info("Calculator terminated")
      sendIdentifyRequest()
      context.become(identifying)
    case ReceiveTimeout =>
    // ignore

  }
}
