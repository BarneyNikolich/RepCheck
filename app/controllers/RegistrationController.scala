package controllers

import java.io.{File, IOException}
import javax.imageio.ImageIO

import anorm.SQL
import controllers.auth.AuthAction
import models.forms.RegistrationForm
import play.api.db.DB
import play.api.mvc.{Action, MultipartFormData}
import play.api.Play.current
import play.api.data.Form
import play.api.i18n.Messages.Implicits._
import play.api.libs.Files




/**
  * Created by Barn on 22/04/2017.
  */
class RegistrationController extends AuthAction {


  var password: Option[String] = None
  var formWithErrorsHack: Option[Form[RegistrationForm]] = None
  var emaill: Option[String] = None
  var usernamee: Option[String] = None

  def showRegistrationForm(username: String, email: String, passwd: String) = Action { implicit request =>
    val amazonScoreList = List("5.0", "4.9", "4.8", "4.7", "4.6", "4.3", "4.2", "4.1", "4.0", "3.9", "3.8", "3.7", "3.6", "3.5", "3.4", "3.3", "3.2", "3.1", "3.0")
    password = Some(passwd)
    emaill = Some(email)
    usernamee = Some(username)

    val form = RegistrationForm.form
    val formORerorrs = formWithErrorsHack.getOrElse(form.fill(RegistrationForm(username, "", "", email, "", Some(0), Some(0), Some(0), "", "", Some(""), "", None)))
    Ok(views.html.registrationSteps(formORerorrs, getList, amazonScoreList))
  }

  def getList() = {
    import scala.collection.mutable.ListBuffer
    var ebayScoreList = new ListBuffer[String]()
    for(i <- 1 to 100) {
      ebayScoreList += i.toString
    }
    ebayScoreList.toList.reverse
  }


  def submitRegistrationForm = Action(parse.multipartFormData) { implicit request =>

    RegistrationForm.form.bindFromRequest().fold(
      hasErrors => {
        println(hasErrors)
        formWithErrorsHack = Some(hasErrors)
        Redirect(routes.RegistrationController.showRegistrationForm(usernamee.getOrElse(""), emaill.getOrElse(""),""))
      },
      success => {

        request.body.file("picture") map { picture =>

          if (picture.filename.length != 0){

            //Check if user has indicated they previously use amazon or ebay
            val user = success match {
              case RegistrationForm(_, _, _, _, _, _, _, _, "no", "no", _, "no", _) =>
                RegistrationForm(success.username, success.firtname, success.lastname, success.email, success.phonenumber, None, None, None, success.retailebay, success.retailamazon, None, success.retailfacebook, None)
              case RegistrationForm(_, _, _, _, _, _, _, _, "no", "yes", _, "no", _) =>
                RegistrationForm(success.username, success.firtname, success.lastname, success.email, success.phonenumber, None, success.totalamazonsales, success.amazonscore, success.retailebay, success.retailamazon, None, success.retailfacebook, None)
              case RegistrationForm(_, _, _, _, _, _, _, _, "yes", "no", _, "no", _) =>
                RegistrationForm(success.username, success.firtname, success.lastname, success.email, success.phonenumber, success.ebayscore, None, None, success.retailebay, success.retailamazon, success.ebayname, success.retailfacebook, None)
              case RegistrationForm(_, _, _, _, _, _, _, _, "no", "no", _, "yes", _) =>
                RegistrationForm(success.username, success.firtname, success.lastname, success.email, success.phonenumber, None, None, None, success.retailebay, success.retailamazon, None, success.retailfacebook, success.facebookemail)
              case RegistrationForm(_, _, _, _, _, _, _, _, "yes", "yes", _, "yes", _) =>
                RegistrationForm(success.username, success.firtname, success.lastname, success.email, success.phonenumber, success.ebayscore, success.totalamazonsales, success.amazonscore, success.retailebay, success.retailamazon, success.ebayname, success.retailfacebook, success.facebookemail)
              case _ => success
            }


            println(user)
            addUserToDatabase(user, saveProfilePictureGetLocation(picture, success.username))
            Redirect(routes.HomeController.index()).withSession("loggedin" -> success.username) //Handle next stage of application
          }
          else Redirect(routes.RegistrationController.showRegistrationForm(success.username, success.email, "")) //no picture present
        } getOrElse Ok("No picture found!")
      }
    )
  }


  def saveProfilePictureGetLocation(picture: MultipartFormData.FilePart[Files.TemporaryFile], username: String): String = {
      import java.io.File
      val photo = ImageIO.read(picture.ref.file)

//      ImageIO.write(photo, "jpg", new File("/profilePictures/"+username+".jpg"))

    /**
      * Uncomment below line to work in local!!!!
      */
        ImageIO.write(photo, "jpg", new File("public/profilePictures/"+username+".jpg"))


    //"profilePictures/"+username+".jpg"
      "/profilePictures/"+username+".jpg"
  }


  def addUserToDatabase(user: RegistrationForm, pictureLocation: String) = {


    DB.withConnection { implicit c =>

      val id: Option[Long] =
        SQL("insert into Userdata(username, firstname, lastname, email, phonenumber, profilepiclocation, password, ebayname, facebookemail, ebayScore) values ({username}, {firstname}, {lastname}," +
          "{email}, {phonenumber}, {profilepiclocation}, {password}, {ebayname}, {facebookemail}, {ebayscore})")
          .on('username -> user.username, 'firstname -> user.firtname, 'lastname -> user.lastname, 'email -> user.email,
            'phonenumber -> user.phonenumber, 'profilepiclocation -> pictureLocation, 'password -> password.getOrElse("password"),
            'ebayname -> user.ebayname.getOrElse("None"), 'facebookemail -> user.facebookemail.getOrElse("None"), 'ebayscore -> user.ebayscore.getOrElse(0)).executeInsert()
    }
  }










}
