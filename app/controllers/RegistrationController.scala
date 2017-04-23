package controllers

import java.io.{File, IOException}
import javax.imageio.ImageIO

import anorm.SQL
import controllers.auth.AuthAction
import models.forms.RegistrationForm
import play.api.db.DB
import play.api.mvc.{Action, MultipartFormData, Request}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.Files




/**
  * Created by Barn on 22/04/2017.
  */
class RegistrationController extends AuthAction {


  var password: Option[String] = None

  def showRegistrationForm(username: String, email: String, passwd: String) = Action { implicit request =>

    import scala.collection.mutable.ListBuffer
    var ebayScoreList = new ListBuffer[String]()
    ebayScoreList += "-- SELECT --"

    for(i <- 1 to 100) {
      ebayScoreList += i.toString
    }


    val amazonScoreList = List("5.0", "4.9", "4.8", "4.7", "4.6", "4.3", "4.2", "4.1", "4.0", "3.9", "3.8", "3.7", "3.6", "3.5", "3.4", "3.3", "3.2", "3.1", "3.0")

    password = Some(passwd)
    Ok(views.html.registrationSteps(RegistrationForm.form.fill(RegistrationForm(username = username, "", "", email, "", 0, 0, 0)), ebayScoreList.toList, amazonScoreList))
  }


  def submitRegistrationForm = Action(parse.multipartFormData) { implicit request =>

    RegistrationForm.form.bindFromRequest().fold(
      hasErrors => {
        println(hasErrors)
        Redirect(routes.RegistrationController.showRegistrationForm("", "", ""))
      },
      success => {
        request.body.file("picture") map { picture =>

          if (picture.filename.length != 0){

            addUserToDatabase(success, saveProfilePictureGetLocation(picture, success.username))
            Redirect(routes.HomeController.index()).withSession("loggedin" -> success.username) //Handle next stage of application
          }
          else Redirect(routes.RegistrationController.showRegistrationForm(success.username, success.email, ""))
        } getOrElse Ok("No picture found!")
      }
    )
  }


  def saveProfilePictureGetLocation(picture: MultipartFormData.FilePart[Files.TemporaryFile], username: String): String = {
      import java.io.File
      val photo = ImageIO.read(picture.ref.file)
      ImageIO.write(photo, "jpg", new File("profilePictures/"+username+".jpg"))
      "profilePictures/"+username+".jpg"
  }


  def addUserToDatabase(user: RegistrationForm, pictureLocation: String) = {


    DB.withConnection { implicit c =>

      val id: Option[Long] =
        SQL("insert into Userdata(username, firstname, lastname, email, phonenumber, profilepiclocation, password) values ({username}, {firstname}, {lastname}," +
          "{email}, {phonenumber}, {profilepiclocation}, {password})")
          .on('username -> user.username, 'firstname -> user.firtname, 'lastname -> user.lastname, 'email -> user.email,
            'phonenumber -> user.phonenumber, 'profilepiclocation -> user.phonenumber, 'password -> password.getOrElse("password")).executeInsert()

      //      val result = SQL"insert into repcheck.User(username, email, password) values ($username, $email, $passwd)".execute()


    }
  }










}
