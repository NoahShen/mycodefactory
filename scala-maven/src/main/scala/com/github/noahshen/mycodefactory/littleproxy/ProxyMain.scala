package com.github.noahshen.mycodefactory.littleproxy


import java.net.{URI, InetSocketAddress}
import java.nio.charset.Charset

import com.netaporter.uri.Uri
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import org.littleshoot.proxy.{HttpFilters, HttpFiltersAdapter, HttpFiltersSourceAdapter}


object Main extends App {
  val server = DefaultHttpProxyServer.bootstrap()
    .withAddress(new InetSocketAddress("0.0.0.0", 7878))
    .withFiltersSource(new PrintFilter)
  server.start()
}

class PrintFilter extends HttpFiltersSourceAdapter {

  val InterceptUrl = "http://qywx.dper.com/app/checkin/loadSign"

  override def filterRequest(orig: HttpRequest, ctx: ChannelHandlerContext): HttpFilters = {
    println (orig.getUri)
    new HttpFiltersAdapter(orig, ctx) {
      override def requestPre(obj: HttpObject): HttpResponse = {
        obj match {
          case res: DefaultHttpRequest =>
            if (!res.getUri.startsWith(InterceptUrl)) {
              return null
            }
            import com.netaporter.uri.dsl._

            val uri: Uri = res.getUri
            val r = scala.util.Random

            var oldLat: String = null
            var oldLong: String = null

            val newUri = uri.mapQuery {
              case ("latitude", Some(lat)) =>
                oldLat = lat
                ("latitude", Some(s"31.21772${r.nextInt(9)}"))
              case ("longitude", Some(long)) =>
                oldLong = long
                ("longitude", Some(s"121.4160${r.nextInt(9)}"))
              case (n, v) =>
                (n, v)
            }
            val newUrl = newUri.toString()
            res.setUri(newUrl)
            null
          case _ => null
        }
      }
    }
  }
}

case class RequestInfo(host: String, path: String, param: Map[String, String])

case class ContentResult(last: Boolean, content: String)

object ContentResult {
  def fromObject(obj: HttpObject): Option[ContentResult] = {
    try {
      obj match {
        case res: LastHttpContent => Some(ContentResult(true, toString(res)))
        case res: HttpContent => Some(ContentResult(false, toString(res)))
        case _ => None
      }
    } catch {
      case e: Throwable => e.printStackTrace(); None
    }
  }

  private[this] def toString(cont: HttpContent): String = {
    cont.content().toString(Charset.forName("UTF-8"))
  }
}