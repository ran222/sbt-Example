package yuan.example.finagle.http.basic

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await

object Proxy extends App {
	val client: Service[Request, Response] =
		Http.newService("www.baidu.com:80")

	val server = Http.serve(":18080", client)
	Await.ready(server)
}
