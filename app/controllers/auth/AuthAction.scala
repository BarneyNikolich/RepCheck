package controllers.auth

import controllers.routes
import models.Userdata
import play.api.mvc._

/**
  * Created by Barn on 10/03/2017.
  */
trait AuthAction extends Controller {

  /**
    * If the session exists then go to desired path.
    * Else Redirect the User to the home page.
    * The request is as it comes in... hasn't changed state to a BadRequest or OK.
    * It essentially has access to the block of the action.
    */
  def AuthAction(block: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      request.session.get("loggedin") match {
        case Some(user) => block(request)   //Checks for a username variable. If exists in session then execute block
        case None => Redirect(routes.HomeController.index())
      }
    }
  }

  /*
    * The below code does what the AuthAction does but you insert this into each method.
    * Rather than AuthAction which is passed as Parameter
    *
    val loggedin = request.session.get("loggedin")
    loggedin match {
      case Some(user) => Ok(views.html.index(user, contentsList))
      case None => Redirect(routes.Application.viewForm())
    }
    */
}
