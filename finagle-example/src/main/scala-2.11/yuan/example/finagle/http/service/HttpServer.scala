package yuan.example.finagle.http.service


import com.twitter.finagle.SimpleFilter
import java.net.InetSocketAddress
import com.twitter.finagle.builder.Server
import yuan.example.finagle.http.service.service.{EchoService, HelloService}
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http._
import com.twitter.finagle.Service


/**
 * This example demonstrates a sophisticated HTTP server that handles exceptions
 * and performs authorization via a shared secret. The exception handling and
 * authorization code are written as Filters, thus isolating these aspects from
 * the main service (here called "Respond") for better code organization.
 */
object HttpServer extends App {

	/**
	 * A simple Filter that catches exceptions and converts them to appropriate
	 * HTTP responses.
	 */
	class HandleExceptions extends SimpleFilter[Request, Response] {
		def apply(request: Request, service: Service[Request, Response]) = {

			// `handle` asynchronously handles exceptions.
			service(request) handle { case error =>
				val statusCode = error match {
					case _: IllegalArgumentException =>
						Status.Forbidden
					case _ =>
						Status.InternalServerError
				}
				val errorResponse = Response(Version.Http11, statusCode)
				errorResponse.contentString = error.getStackTraceString

				errorResponse
			}
		}
	}

	val handleExceptions = new HandleExceptions

	private def auth(service: Service[Request, Response]) = {
		handleExceptions andThen service
	}
	//val respond = new HelloService

	// compose the Filters and Service together:
	//val myService: Service[HttpRequest, HttpResponse] = handleExceptions andThen respond

	val routingService =
		RoutingService.byPath {
			case "/hello" => new HelloService
			case "/echo" => new EchoService
		}

	val server: Server = ServerBuilder()
		.codec(Http())
		.bindTo(new InetSocketAddress(8080))
		.name("httpServer")
		.build(routingService)


}
