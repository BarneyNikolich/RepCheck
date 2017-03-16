package controllers

import controllers.auth.AuthAction
import models.Userdata
import models.Userdata._
import play.api.Play.current


/**
  * Created by Barn on 16/03/2017.
  */
class AccountController extends AuthAction {

  def showAccount(name: String) = AuthAction { implicit request =>

    if (userExists(name)) {

      Userdata.findByUsername(name) map { user =>

        Ok(views.html.acctmp(user.username, user.email.getOrElse("")))
      } getOrElse( NotFound )


    } else {
        Redirect(routes.HomeController.index())
    }
  }




}
