package com.github.noahshen.mycodefactory.littleproxy

/**
 * Created by noahshen on 15/5/24.
 */

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.adapters.MySQLAdapter
import org.squeryl.{KeyedEntity, Session, SessionFactory, Schema}
import java.sql.Timestamp

import org.squeryl.annotations.Column

class SignRecord(@Column("RECORD_ID") val id: Long,
                 @Column("NAME") val name: String,
                 @Column("SIGN_TIME") val signTime: Timestamp,
                 @Column("ADD_TIME") val addTime: Timestamp) extends KeyedEntity[Long]{
}

object Library extends Schema {

  val signRecords = table[SignRecord]("SIGN_RECORD")

  on(signRecords)(s => declare(
    s.id is(unique, autoIncremented)
  ))
}

object SquerylMainApp extends App {

  Class.forName("com.mysql.jdbc.Driver")
  val url = "jdbc:mysql://noahsara.com:13306/grabit?useUnicode=yes&characterEncoding=utf8"
  val user = "nike"
  val password = "store"
  SessionFactory.concreteFactory = Some(()=>
      Session.create(java.sql.DriverManager.getConnection(url, user, password), new MySQLAdapter)
  )

  inTransaction {

    Session.currentSession.setLogger { sql =>
      println(sql)
    }
    Library.signRecords.insert(new SignRecord(0, "NoahS", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis())))
  }

}