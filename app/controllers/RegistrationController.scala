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


  def showRegistrationForm(username: String, email: String, passwd: String) = Action { implicit request =>
    Ok(views.html.registrationSteps(RegistrationForm.form.fill(RegistrationForm(username = username, "", "", email, ""))))
  }


  def submitRegistrationForm = Action(parse.multipartFormData) { implicit request =>

    RegistrationForm.form.bindFromRequest().fold(
      hasErrors => {
        Redirect(routes.RegistrationController.showRegistrationForm("", "", ""))
      },
      success => {
        request.body.file("picture") map { picture =>

          if (picture.filename.length != 0){
            saveProfilePicture(picture, success.username)
            Ok("File found and succesfull form") //Handle next stage of application
          }
          else Redirect(routes.RegistrationController.showRegistrationForm(success.username, success.email, ""))
        } getOrElse Ok("No picture found!")
      }
    )
  }


  def saveProfilePicture(picture: MultipartFormData.FilePart[Files.TemporaryFile], username: String) = {
      import java.io.File
      val photo = ImageIO.read(picture.ref.file)
      ImageIO.write(photo, "jpg", new File("profilePictures/"+username+".jpg"));
  }











}
