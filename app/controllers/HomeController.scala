package controllers

import javax.inject._
import play.api.mvc._
import models._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Action


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
//  def index = Action {
//    Ok(views.html.index("Your new application is ready."))
//  }

  def index = Action {
  //Which form to serve????
    Ok(views.html.index(Userdata.userForm))
  }

  /**
    * Have signle form submission action... check if the form data contains login or signup then handle accordingly
    * MAYBE sign up could link to action which serves the page with a sign up form??? not login form??
    */

  def submitform = Action { implicit request =>

    Userdata.userForm.bindFromRequest.fold(
      formWithErrors => {
        println(formWithErrors)
        BadRequest(views.html.index(formWithErrors))
      },
      successform => {
        val formResults = List (
          successform.username,
          successform.email,
          successform.password,
          successform.confirmpassword
        )

        println(successform)

        Ok(formResults.toString())
      }
    )

  }




  def test = Action {
    Ok(views.html.test())
  }

}
