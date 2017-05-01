package controllers

import javax.inject._

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


@Singleton
class HomeController @Inject() extends AuthAction {


  def index = Action { implicit request =>
    println(request.session.data)


    if(userIsLoggedIn(request)) Ok(views.html.index(Userdata.userForm, None, false, false, loggedIn = true))
    else  Ok(views.html.index(Userdata.userForm, None, false, false, loggedIn = false))
  }

  def rate = Action {
    Ok(views.html.ratingtemplate())
  }


  def test = AuthAction { implicit request =>

    Ok("Must be logged in to see this page")
  }

}
