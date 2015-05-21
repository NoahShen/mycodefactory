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

  val LoadLocationUrl = "http://qywx.dper.com/app/checkin/loadSign"
  val SignUrl = "http://qywx.dper.com/app/checkin/sign"

  override def filterRequest(orig: HttpRequest, ctx: ChannelHandlerContext): HttpFilters = {
    println (orig.getUri)
    val builder = new StringBuilder
    new HttpFiltersAdapter(orig, ctx) {
      override def requestPre(obj: HttpObject): HttpResponse = {
        obj match {
          case res: DefaultHttpRequest =>
            if (!res.getUri.startsWith(LoadLocationUrl)) {
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

      /**
       * {
    "data": [
        {
            "avatar": "http://shp.qpic.cn/bizmp/iciaW8ibhaG6vNRhg4J8mt2ibwUUwzrrsftXfYDib8RwGx5IGibVGFZtjonA/",
            "name": "XXX",
            "comment": "愉(yin)快(dang)的一天开始了~",
            "time": "2015-05-21 22:57:53"
        },
        {
            "avatar": "http://shp.qpic.cn/bizmp/iciaW8ibhaG6vPb1srklrgIibFxoXeYCaMeKJPWMGWANFpe4clZiblDVjjQ/",
            "name": "XXX",
            "comment": "愉(yin)快(dang)的一天开始了~",
            "time": "2015-05-21 22:50:09"
        },
        {
            "avatar": "http://shp.qpic.cn/bizmp/iciaW8ibhaG6vMibtFpGwYpInSHHqKuoPrEajxjN3lXMN6VZ7BDoqqsiaibA/",
            "name": "XXX",
            "comment": "愉(yin)快(dang)的一天开始了~",
            "time": "2015-05-21 22:15:45"
        }
    ],
    "code": 200
}
       * @param obj
       * @return
       */
      override def responsePost(obj: HttpObject): HttpObject = {
        // TODO save response
            ContentResult.fromObject(obj).foreach { result =>
              builder.append(result.content)
              if(result.last) {
                import scala.util.parsing.json._

                val result = JSON.parseFull(builder.toString())

                result match {
                  case Some(json: Map[String, Any]) =>
                    json.get("data").map { signRecords =>
                      signRecords
                    }
                  case None => obj
                }
              }
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