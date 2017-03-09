package controllers

import javax.inject._

import play.api.mvc._
import models._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Action
import anorm._
import play.api.db.DB
import models.Userdata._
import play.api.i18n.Messages


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
    Ok(views.html.index(Userdata.userForm, None))
  }


  def submitform = Action { implicit request =>

    Userdata.userForm.bindFromRequest.fold(
      formWithErrors => {

        //Maybe handle the errors here and return list of messages!
        println(formWithErrors)

        BadRequest(views.html.index(formWithErrors, None))
      },
      successform => {

        //Redirect to specific action - Login or sign up form
//        LOOK HOW PERTAX FRONTEND HANDLES DATA FROM A FORM
        successform match {
          case Userdata(_, username, _, Some(passwd), _, _) =>

            if(userExists(username, passwd)) {
              Redirect(routes.HomeController.processsLoginForm(username))
            } else BadRequest(views.html.index(Userdata.userForm, Some(Messages("error.uname_or_pword_incorrect"))))


          case Userdata(_, username, Some(email), _, Some(passwd1), _)  =>

            Redirect(routes.HomeController.processSignupForm(username, email, passwd1))

        }
      }
    )

  }

  /**
    * check table if user exists
    * @param username
    * @return
    */
  def processsLoginForm(username: String) = Action {

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
  def processSignupForm(username: String, email: String, passwd: String) = Action {

    DB.withConnection { implicit c =>

      val result = SQL"insert into User(username, email, password) values ($username, $email, $passwd)".execute()
//        val query = SQL("Select * from repcheck.page_retrieval")
      Ok(result.toString)
    }


  }



  def testdb = Action { implicit request =>

//    DB.withConnection { implicit c =>
//      val result: Boolean = SQL("CREATE TABLE Persons(PersonID int,LastName varchar(255));").execute()
//    }

    Ok
  }

}
