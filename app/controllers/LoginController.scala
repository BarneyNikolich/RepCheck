package controllers

import models.Userdata
import models.Userdata.userExists
import play.api.i18n.Messages
import play.api.mvc._
import models._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Action
import play.api.db.DB
import models.Userdata._
import play.api.i18n.Messages
import anorm._
import controllers.auth.AuthAction
import play.mvc.Security.AuthenticatedAction


/**
  * Created by Barn on 10/03/2017.
  */
class LoginController extends AuthAction {

  /**
    * Maybe factor out logic into LoginController???* @return
    */
  def submitform = Action { implicit request =>

    Userdata.userForm.bindFromRequest.fold(
      formWithErrors => {
        if(formWithErrors.data.contains("email")) {
          BadRequest(views.html.index(formWithErrors, None, showSignupForm = true, showLoginForm = false))
        } else {
          BadRequest(views.html.index(formWithErrors, None, showSignupForm = false, showLoginForm = true))
        }
      },
      successform => { successform match {
        case Userdata(_, username, _, Some(passwd), _, _) =>

          if(userExists(username, passwd)) {
            Redirect(routes.LoginController.processsLoginRequest(username)).withSession("loggedin" -> username)
          } else
            BadRequest(views.html.index(Userdata.userForm, Some(Messages("error.uname_or_pword_incorrect")), showSignupForm = false, showLoginForm = true))

        case Userdata(_, username, Some(email), _, Some(passwd1), _) =>
          Redirect(routes.LoginController.processSignupForm(username, email, passwd1))
        case _ =>
          BadRequest(views.html.index(Userdata.userForm, Some(Messages("error.you_missed_something")), showSignupForm = false, showLoginForm = true))
      }
      }
    )
  }


  /**
    * check table if user exists
    * @param username
    * @return
    */
  def processsLoginRequest(username: String) = AuthAction { implicit request =>

    Userdata.findByUsername(username) map { user =>

      val loggedIn = List(user.id, user.username, user.loginpasswd, user.email)
      Ok("Processing login for: " + loggedIn)


    } getOrElse( Ok("Not Found"))
  }


  /**
    * If username doesnt already exist, add user to the database
    * @param username
    * @param email
    * @param passwd
    * @return
    */
  def processSignupForm(username: String, email: String, passwd: String) = Action { implicit request =>

    DB.withConnection { implicit c =>

      val id: Option[Long] =
        SQL("insert into User(username, email, password) values ({username}, {email}, {password})")
          .on('username -> username, 'email -> email, 'password -> passwd).executeInsert()

//      val result = SQL"insert into repcheck.User(username, email, password) values ($username, $email, $passwd)".execute()
      Ok("SIGN UP! " )
    }

  }





}
