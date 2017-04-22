package controllers

import anorm.SQL
import controllers.auth.AuthAction
import play.api.db.DB
import play.api.mvc.Action
import play.api.Play.current



/**
  * Created by Barn on 22/04/2017.
  */
class RegistrationController extends AuthAction {


  def processSignupForm(username: String, email: String, passwd: String) = Action { implicit request =>

//    DB.withConnection { implicit c =>
//
//      val id: Option[Long] =
//        SQL("insert into User(username, email, password) values ({username}, {email}, {password})")
//          .on('username -> username, 'email -> email, 'password -> passwd).executeInsert()
//
//      //      val result = SQL"insert into repcheck.User(username, email, password) values ($username, $email, $passwd)".execute()
//      Ok("SIGN UP! " )
//    }





    Ok(views.html.registrationSteps())


  }




}
