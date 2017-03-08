package controllers

import javax.inject._
import play.api.mvc._
import models._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Action
import anorm._
import play.api.db.DB


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


  def submitform = Action { implicit request =>

    Userdata.userForm.bindFromRequest.fold(
      formWithErrors => {

        //Maybe handle the errors here and return list of messages!
        println(formWithErrors)

        BadRequest(views.html.index(formWithErrors))
      },
      successform => {

        //Redirect to specific action - Login or sign up form
//        LOOK HOW PERTAX FRONTEND HANDLES DATA FROM A FORM
        successform match {
          case Userdata(Some(username), _, Some(passwd), _, _) =>
            val result = List(username, passwd)
            Redirect(routes.HomeController.processsLoginForm(result)) //Redirect the submit action to the formResults method. This is a post.
          case Userdata(Some(username), Some(email), _, Some(passwd1), _)  =>
            val result = List(username, email, passwd1)
            Redirect(routes.HomeController.processSignupForm(result))

        }
      }
    )

  }

  def processsLoginForm(logindata: List[String]) = Action {
    for(d <- logindata){
      println(d)
    }

    Ok
  }




  def processSignupForm(data: List[String]) = Action {

    DB.withConnection { implicit c =>
      val result: Boolean = SQL("Select * from repcheck.page_retrieval").execute()

      val sqlQuery = SQL(
        """
    select * from repcheck.page_retrieval;
  """

      )
          println(sqlQuery.params)
    }

    Ok
  }



  def testdb = Action { implicit request =>

//    DB.withConnection { implicit c =>
//      val result: Boolean = SQL("CREATE TABLE Persons(PersonID int,LastName varchar(255));").execute()
//    }

    Ok
  }

}
