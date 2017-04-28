package controllers

import controllers.auth.AuthAction
import models.{CurrentUser, Userdata}
import models.Userdata._
import play.api.Play.current
import play.api.mvc.Action
import views.html
import play.api.i18n.Messages.Implicits._



/**
  * Created by Barn on 16/03/2017.
  */
class AccountController extends AuthAction {

  def showAccount(name: String) = Action { implicit request =>

    if (userExists(name)) {

      CurrentUser.findByUsername(name) map { user =>

        val picLocation = "/assets"+user.profilepiclocation
        val fullname = user.firtname.capitalize + " " + user.lastname.capitalize



        val page =0
        val orderBy = 2
        val filter = ""
        (
          models.Transaction.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
          orderBy, filter
        )
        Ok(views.html.acctmp(fullname, user.phonenumber, user.email, picLocation, user.ebayname, models.Transaction.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
          orderBy, filter))


      } getOrElse( NotFound("Couldnt find by username!") )


    } else {
        Redirect(routes.HomeController.index())
    }
  }




}
