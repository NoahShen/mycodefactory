package com.github.noahshen.mycodefactory.littleproxy


import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import com.netaporter.uri.Uri
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import org.littleshoot.proxy.{HttpFilters, HttpFiltersAdapter, HttpFiltersSourceAdapter}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.adapters.MySQLAdapter
import org.squeryl.{Session, SessionFactory}
import play.api.libs.json.{JsArray, JsValue, Json}

import slick.driver.MySQLDriver.api._

class DefaultFilter extends HttpFilters {
  override def responsePre(httpObject: HttpObject): HttpObject = {httpObject}

  override def requestPre(httpObject: HttpObject): HttpResponse = {null}

  override def responsePost(httpObject: HttpObject): HttpObject = {httpObject}

  override def requestPost(httpObject: HttpObject): HttpResponse = {null}
}

object LoadLocationFilter {
  val LoadLocationUrl = "http://qywx.dper.com/app/checkin/loadSign"

}
class LoadLocationFilter(request: HttpRequest, ctx: ChannelHandlerContext) extends HttpFiltersAdapter(request, ctx) {

  override def requestPre(obj: HttpObject): HttpResponse = {
    obj match {
      case res: DefaultHttpRequest =>
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

case class SignedUserInfo(name: String, time: Date)

object SignFilter {
  val SignUrl = "http://qywx.dper.com/app/checkin/sign"

}
class SignFilter(request: HttpRequest, ctx: ChannelHandlerContext) extends HttpFiltersAdapter(request, ctx) {

  val builder = new StringBuilder


  /**
   *
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
    ContentResult.fromObject(obj).foreach { result =>
      builder.append(result.content)
      if (result.last) {
        val json: JsValue = Json.parse(builder.toString())
        (json \ "data").asOpt[JsArray].foreach { jsArr =>
          val firstUserInfo = jsArr(0)
          val nameOpt = (firstUserInfo \ "name").asOpt[String]
          val timeOpt = (firstUserInfo \ "time").asOpt[String]
          if (nameOpt.isDefined && timeOpt.isDefined) {
            val sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val signTime = sdf.parse(timeOpt.get)
            println(s"${nameOpt.get} sign at ${signTime}")

            inTransaction {
              Library.signRecords.insert(new SignRecord(0, nameOpt.get, new Timestamp(signTime.getTime), new Timestamp(System.currentTimeMillis())))
            }
          }
        }

      }
    }
    obj
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

class SignFilterAdapter extends HttpFiltersSourceAdapter {

  override def filterRequest(orig: HttpRequest, ctx: ChannelHandlerContext): HttpFilters = {
    val socketAddress = ctx.channel().remoteAddress()
    println(socketAddress)
    if (orig.getUri.startsWith(LoadLocationFilter.LoadLocationUrl)) {
      return new LoadLocationFilter(orig, ctx)
    } else if (orig.getUri.startsWith(SignFilter.SignUrl)){
      return new SignFilter(orig, ctx)
    }
    return new DefaultFilter
  }
}



object ProxyMain extends App {

  override val args: Array[String] = if (super.args.isEmpty) Array("7878") else super.args

  Class.forName("com.mysql.jdbc.Driver")
  val url = "jdbc:mysql://noahsara.com:13306/grabit?useUnicode=yes&characterEncoding=utf8"
  val user = "nike"
  val password = "store"
  SessionFactory.concreteFactory = Some(()=>
    Session.create(java.sql.DriverManager.getConnection(url, user, password), new MySQLAdapter)
  )
  val port = args(0).toInt
  val server = DefaultHttpProxyServer.bootstrap()
    .withAddress(new InetSocketAddress("0.0.0.0", port))
    .withFiltersSource(new SignFilterAdapter)
  server.start()
}

