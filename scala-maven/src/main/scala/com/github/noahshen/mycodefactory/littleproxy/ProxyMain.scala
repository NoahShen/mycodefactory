package com.github.noahshen.mycodefactory.littleproxy


import java.net.InetSocketAddress
import java.nio.charset.Charset

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import org.littleshoot.proxy.{HttpFilters, HttpFiltersAdapter, HttpFiltersSourceAdapter}


object Main extends App {
  val server = DefaultHttpProxyServer.bootstrap()
    .withAddress(new InetSocketAddress("0.0.0.0", 8080))
    .withFiltersSource(new PrintFilter)
  server.start()
}

class PrintFilter extends HttpFiltersSourceAdapter {

  override def filterRequest(orig: HttpRequest, ctx: ChannelHandlerContext): HttpFilters = {
    println(orig.getUri)
    val builder = new StringBuilder
    new HttpFiltersAdapter(orig, ctx) {
      override def responsePre(obj: HttpObject): HttpObject = {
        ContentResult.fromObject(obj).foreach { result =>
          builder.append(result.content)
          if(result.last) println(builder.toString()) // 濂姐亶銇嚘鐞嗐倰鍣涖伨銇�
        }
        obj
      }
    }
  }
}

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