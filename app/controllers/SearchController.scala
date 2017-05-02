package controllers

import controllers.auth.AuthAction
import models.{CurrentUser, Transaction}
import models.Userdata._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Action
import views.html


/**
  * Created by Barn on 16/03/2017.
  */
class SearchController extends AuthAction {

  val Home = Redirect(routes.SearchController.list(0, 2, ""))

  def showSearch() = Action {
     Home
  }

  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>

    val picLocation = "/assets"


    Ok(html.search(
      models.CurrentUser.listUsers(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter, picLocation
    ))



 }







}
