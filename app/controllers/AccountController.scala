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

  var loggedinUser: Option[String] = None

  def showAccount(name: String, newPage : Option[Int], newOrder: Option[Int], newFilter: Option[String]) = Action { implicit request =>

    loggedinUser = Some(request.session.get("loggedin")).getOrElse(None)

    println("In method!!!" + name)
    if (userExists(name)) {

      CurrentUser.findByUsername(name) map { user =>

        val picLocation = "/assets"+user.profilepiclocation
        val fullname = user.firtname.capitalize + " " + user.lastname.capitalize




        val page = newPage.getOrElse(0)
        val orderBy = newOrder.getOrElse(2)
        val filter = newFilter.getOrElse(name)



        Ok(views.html.acctmp(user.username, fullname, user.phonenumber, user.email, picLocation, user.ebayname, models.Transaction.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
          orderBy, filter))


      } getOrElse( NotFound("Couldnt find by username!") )


    } else {
        Redirect(routes.HomeController.index())
    }
  }




  /**
    * Display the 'new transaction form'.
    */
  def create(name: String) = Action {
    Ok(views.html.addTransaction(models.Transaction.transacrionForm, name))
  }


  /**
    * Handle the 'new computer form' submission.
    */
  def save = Action { implicit request =>
    models.Transaction.transacrionForm.bindFromRequest.fold(
      errors => {
        println("TRANSACTION FORM ERROR:    " + errors)
        BadRequest(views.html.addTransaction(errors, request.session.get("loggedin").get))
      }
      ,
      success => {
        models.Transaction.insert(success, request.session.get("loggedin").get  )
        Redirect(routes.AccountController.showAccount(request.session.get("loggedin").get, None, None, None)).
        flashing("success" -> "Computer %s has been created".format(success.item))
      }
    )

//    computerForm.bindFromRequest.fold(
//      formWithErrors => BadRequest(html.createForm(models.Transaction.transacrionForm
//        , "")),
//      computer => {
//        Computer.insert(computer)
//        Home.flashing("success" -> "Computer %s has been created".format(computer.name))
//      }
//    )
  }

}
