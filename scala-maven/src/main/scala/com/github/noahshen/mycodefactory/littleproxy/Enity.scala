package com.github.noahshen.mycodefactory.littleproxy

import java.sql.Date
import slick.driver.MySQLDriver.api._

/**
 * Created by noahshen on 15/5/23.
 */
// Definition of the SUPPLIERS table
class SignRecord(tag: Tag) extends Table[(Option[Int], String, Date, Date)](tag, "SIGN_RECORD") {
  def id = column[Int]("RECORD_ID", O.PrimaryKey) // This is the primary key column
  def name = column[String]("NAME")
  def signTime = column[Date]("SIGN_TIME")
  def addTime = column[Date]("ADD_TIME")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id.?, name, signTime, addTime)
}

object SignRecord {
  val SignRecords = TableQuery[SignRecord]
}


object Main extends App {
  val signRecords = TableQuery[SignRecord]


  val db = Database.forConfig("mysqltest")
  try {
    val setup = DBIO.seq(
      // Create the tables, including primary and foreign keys
      (signRecords.schema).create,

      // Insert some suppliers
      signRecords += (None, "Noah", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()))
      // Equivalent SQL code:
      // insert into SIGN_RECORD(SUP_ID, SUP_NAME, STREET, CITY, STATE, ZIP) values (?,?,?,?,?,?)

    )

    val setupFuture = db.run(setup)
//    setupFuture.onSuccess {
//      case _ => println("create success")
//    }
  } finally {
    db.close
  }
}