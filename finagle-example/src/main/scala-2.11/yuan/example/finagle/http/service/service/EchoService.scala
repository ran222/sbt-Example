package yuan.example.finagle.http.service.service

import com.twitter.finagle.Service
import org.jboss.netty.handler.codec.http.{DefaultHttpResponse, HttpResponse, HttpRequest}
import org.jboss.netty.handler.codec.http.HttpVersion._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.buffer.ChannelBuffers._
import org.jboss.netty.util.CharsetUtil._
import com.twitter.util.Future
import com.twitter.finagle.http.{Status, Version, Response, Request}

class EchoService extends Service[Request, Response] {
	def apply(request: Request) = {
		val requestMessage = request.getParam("q")
		println("receive:"+requestMessage)
		/*val response = new DefaultHttpResponse(HTTP_1_1, OK)
		response.setContent(copiedBuffer(requestMessage, UTF_8))
		Future.value(HttpResponse(response))*/

		val response = Response(Version.Http11, Status.Ok)
		//response.contentString= copiedBuffer(requestMessage, UTF_8).toString
		response.contentString= requestMessage
		Future.value(response)
	}
}

