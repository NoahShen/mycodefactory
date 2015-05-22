package com.github.noahshen.mycodefactory.littleproxy


import java.net.{URI, InetSocketAddress}
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date

import com.netaporter.uri.Uri
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import org.littleshoot.proxy.{HttpFilters, HttpFiltersAdapter, HttpFiltersSourceAdapter}
import play.api.libs.json.{JsArray, JsValue, Json}


object Main extends App {
  val server = DefaultHttpProxyServer.bootstrap()
    .withAddress(new InetSocketAddress("0.0.0.0", 7878))
    .withFiltersSource(new SignFilterAdapter)
  server.start()
}

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
    // TODO save response
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
          }
        }
//        val userInfo = parseUserInfo(resultJson)
//        println(userInfo)
      }
    }
    obj
  }
//
//  def parseUserInfo(resultJson: Option[Any]): Option[SignedUserInfo] = {
//    var employeeName: Option[String] = None
//    var signTime: Option[Date] = None
//    resultJson match {
//      case Some(json: Map[String, Any]) =>
//        json.get("data").map {  =>
//          case signRecords: List =>
//            signRecords.headOption.map {
//              case Some(("name", name:String)) =>
//                employeeName = Some(name)
//              case Some(("time", time:String)) =>
//                val sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
//                signTime = Some(sdf.parse(time))
//            }
//        }
//      case None => None
//    }
//    if (employeeName.isDefined && signTime.isDefined) {
//      return Some(SignedUserInfo(employeeName.get, signTime.get))
//    }
//    return None
//  }
}

class SignFilterAdapter extends HttpFiltersSourceAdapter {

  override def filterRequest(orig: HttpRequest, ctx: ChannelHandlerContext): HttpFilters = {
    if (orig.getUri.startsWith(LoadLocationFilter.LoadLocationUrl)) {
      return new LoadLocationFilter(orig, ctx)
    } else if (orig.getUri.startsWith(SignFilter.SignUrl)){
      return new SignFilter(orig, ctx)
    }
    return new DefaultFilter
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