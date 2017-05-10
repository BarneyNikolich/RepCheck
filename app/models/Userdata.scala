package models

import anorm.SqlParser.{get, scalar}
import play.api.Application
import play.api.data.Forms._
import play.api.db.DB
import play.api.libs.json.Json
import org.mindrot.jbcrypt.BCrypt
import anorm._
import play.api.data.Form



import scala.language.postfixOps







//import play.api.Play.current
import play.api.i18n.Messages.Implicits._


/**
  * Created by Barn on 06/03/2017.
  */


case class cheese[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

case class CurrentUser(
                      username: String,
                      firtname: String,
                      lastname: String,
                      email: String,
                      phonenumber: String,
                      profilepiclocation: String,
                      password: String,
                      ebayname: String,
                      facebookemail: String,
                      ebayscore: String
                      )

object CurrentUser {
    /**
    * Parse a user from a result set
    */
  val simple = {
      get[String]("Userdata.username") ~
      get[String]("Userdata.firstname") ~
      get[String]("Userdata.lastname") ~
      get[String]("Userdata.email") ~
      get[String]("Userdata.phonenumber") ~
      get[String]("Userdata.profilepiclocation") ~
      get[String]("Userdata.password") ~
      get[String]("Userdata.ebayname") ~
      get[String]("Userdata.facebookemail") ~
      get[String]("Userdata.ebayscore")  map {
      case username~firstname~lastname~email~phonenumber~profilepiclocation~password~ebayname~facebookemail~ebayscore =>
        CurrentUser(username, firstname, lastname, email, phonenumber, profilepiclocation, password, ebayname, facebookemail, ebayscore)
    }
  }

//  val withTransactions = CurrentUser.simple ~ (Transaction.simple ?) map {
//    case currentuser~transaction => (currentuser, transaction)
//  }



  def listUsers(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): cheese[CurrentUser] = {

        val offest = pageSize * page
    implicit val app = play.api.Play.current


    DB.withConnection { implicit connection =>

      val users = SQL(
        """
              select * from repcheck.Userdata
              where repcheck.Userdata.username like {filter}
              order by {orderBy}
              limit {pageSize} offset {offset}
            """
      ).on(
        'pageSize -> pageSize,
        'offset -> offest,
        'filter -> filter,
        'orderBy -> orderBy
      ).as(CurrentUser.simple *)

      val totalRows = SQL(
        """
              select count(*) from repcheck.Userdata
              where repcheck.Userdata.username like {filter}
            """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)


      cheese(users, page, offest, totalRows)
    }
  }


//  def listusers(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): cheese[(CurrentUser, Option[Transaction])] = {
//
//    println("IN THE LIST METHOD BEFORE SQL")
//    val offest = pageSize * page
//
//    DB.withConnection { implicit connection =>
//
//      val users = SQL(
//        """
//          select * from repcheck.Userdata
//          left join repcheck.Transaction on repcheck.Transaction.usernameId = repcheck.Userdata.username
//          where repcheck.Userdata.username like {filter}
//          order by {orderBy}
//          limit {pageSize} offset {offset}
//        """
//      ).on(
//        'pageSize -> pageSize,
//        'offset -> offest,
//        'filter -> filter,
//        'orderBy -> orderBy
//      ).as(CurrentUser.withTransactions *)
//
//
//      println("********* --------------------->>>>>>>>>>>>>>>>>>>> " + users)
//
//      val totalRows = SQL(
//        """
//          select count(*) from repcheck.Userdata
//          left join repcheck.Transaction on repcheck.Userdata.username = repcheck.Transaction.usernameId
//          where repcheck.Userdata.username like {filter}
//        """
//      ).on(
//        'filter -> filter
//      ).as(scalar[Long].single)
//
//      println("COUNTED ROWSSS ------------->    " + totalRows)
//
//      cheese(users, page, offest, totalRows)
//
//    }
//  }




  /**
    * Retrieve a user from the database identified by username.
    */
  def findByUsername(username: String)(implicit app: Application): Option[CurrentUser] = {
    DB.withConnection { implicit connection =>
      SQL("select * from repcheck.Userdata where username = {username}").on('username -> username).as(CurrentUser.simple.singleOpt)
    }
  }


  /**
    * Check if a user exists with a username and a password
    * @param username
    * @param passwd
    * @param app
    * @return
    */
  def userExists(username: String, passwd: String)(implicit app: Application): Boolean = {

    DB.withConnection { implicit connection =>
      val userdata = SQL("select * from repcheck.Userdata where username = {username} and password = {password}").on('username -> username, 'password -> passwd).as(CurrentUser.simple.singleOpt)

      userdata match {
        case Some(CurrentUser(_, _, _, _, _, _, _, _,_ ,_)) => true
        case _ => false
      }
    }

  }

  /**
    * Check if a user exists with just a username
    * @param username
    * @param app
    * @return
    */
  def userExists(username: String)(implicit app: Application): Boolean = {

    DB.withConnection { implicit connection =>
      val userdata = SQL("select * from repcheck.Userdata where username = {username}").on('username -> username).as(CurrentUser.simple.singleOpt)

      userdata match {
        case Some(CurrentUser(_, _, _, _, _, _, _, _, _, _)) => true
        case _ => false
      }
    }

  }

}






case class Userdata(id: Option[Long],
                    username: String,
                    email: Option[String],
                    loginpasswd: Option[String],
                    password: Option[String],
                    confirmpassword: Option[String])

object Userdata {

  /**
    * Parse a user from a result set
    */
  val simple = {
      get [Option[Long]]("User.id") ~
      get[String]("User.username") ~
      get[Option[String]]("User.email") ~
      get[Option[String]]("User.password") map {
      case id~username~email~password => Userdata(id, username, email, password, None, None)
    }
  }

  /**
    * Retrieve a user from the username.
    */
  def findByUsername(username: String)(implicit app: Application): Option[CurrentUser] = {
    DB.withConnection { implicit connection =>
      SQL("select * from repcheck.User where username = {username}").on('username -> username).as(CurrentUser.simple.singleOpt)
    }
  }


  /**
    * This method checks if a user exists in the database that matches a queried username and password
    * BCrypt cryptographic hashing library is used to encrypt and decrypt plain text passwords
    * @param username
    * @param passwd
    * @param app
    * @return Boolean
    */
  def userExists(username: String, passwd: String)(implicit app: Application): Boolean = {
    DB.withConnection { implicit connection =>
      val userdata = SQL("select * from repcheck.Userdata where username = {username}").on('username -> username ).as(CurrentUser.simple.singleOpt)

        userdata match {
        case Some(CurrentUser(_, _, _, _, _, _, haspw, _, _, _)) => {
          BCrypt.checkpw(passwd, haspw) //returns true if password matches hash
        }
        case _ => false
      }
    }
  }

  def userExists(username: String)(implicit app: Application): Boolean = {

    DB.withConnection { implicit connection =>
      val userdata = SQL("select * from repcheck.Userdata where username = {username}").on('username -> username).as(CurrentUser.simple.singleOpt)

      userdata match {
        case Some(CurrentUser(_, _, _, _, _, _, _, _, _, _)) => true
        case _ => false
      }
    }

  }

  implicit val formats = Json.format[Userdata]

  val userForm = Form(
    mapping (
      "id" -> ignored(None:Option[Long]),
      "username" -> nonEmptyText.verifying("Username must not contain numbers!", c => c.matches("[a-zA-Z]*"))
        .verifying("Username must be between 3 and 15 characters", s => s.matches("[a-zA-Z]{3,10}")),
      "email" -> optional(email),
      "loginpasswd" -> optional(text(6, 15)),
      "password" -> optional(text(6, 15)),
      "confirmpassword" -> optional(text(6, 15))
    ) (Userdata.apply) (Userdata.unapply).verifying("Passwords must match", field => field.password.equals(field.confirmpassword))
  )


}


