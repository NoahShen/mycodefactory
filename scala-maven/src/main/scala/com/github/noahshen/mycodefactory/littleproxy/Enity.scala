package com.github.noahshen.mycodefactory.littleproxy

import java.sql.{Timestamp, Date}
import slick.driver.MySQLDriver.api._
import scala.concurrent.duration.Duration
import scala.concurrent.Await

/**
 * Created by noahshen on 15/5/23.
 */
//
//case class SignRecord(id: Option[Int] = None, name: String, signTime: Timestamp, addTime: Timestamp)
//
//class SignRecords(tag: Tag) extends Table[SignRecord](tag, "SIGN_RECORD") {
//  def id = column[Int]("RECORD_ID", O.PrimaryKey, O.AutoInc) // This is the primary key column
//  def name = column[String]("NAME")
//  def signTime = column[Timestamp]("SIGN_TIME")
//  def addTime = column[Timestamp]("ADD_TIME")
//  // Every table needs a * projection with the same type as the table's type parameter
//  def * = (id.?, name, signTime, addTime) <> (SignRecord.tupled, SignRecord.unapply)
//}
//
//object SignRecords {
//  val SignRecords = TableQuery[SignRecords]
//}
//
////
//object Main extends App {
//  val signRecords = TableQuery[SignRecords]
//
//  // Print the SQL for the filter query
//
//  // Execute the query and print the Seq of results
//
//  val db = Database.forConfig("mysqltest")
//  try {
//    Await.result(db.run(DBIO.seq(
//      (signRecords returning signRecords.map(_.id)) += SignRecord(None, "Noah", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()))
//    )), Duration.Inf)
//  } finally {
//    db.close
//  }
//}
