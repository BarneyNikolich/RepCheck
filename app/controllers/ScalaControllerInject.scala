package controllers

/**
  * Created by Barn on 07/03/2017.
  */

import javax.inject.Inject

import play.api.Play.current
import play.api.mvc._
import play.api.db._

class ScalaControllerInject @Inject()(db: Database) extends Controller {

  def index = Action {


    val databaseurl = db.url

    var outString = "Currently connected to: " + databaseurl
    val conn = db.getConnection()

    for(i <- 1 to 10){
     println(db.name)
    }

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT * FROM repcheck.page_retrieval")

      while (rs.next()) {
//        outString += rs.getString("timestamp")
      }
    } finally {
      conn.close()
    }


    Ok(outString)
  }

}