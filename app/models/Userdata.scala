package models


import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.i18n.Messages

/**
  * Created by Barn on 06/03/2017.
  */
case class Userdata(username: Option[String],
                    email: Option[String],
                    loginpasswd: Option[String],
                    password: Option[String],
                    confirmpassword: Option[String])

object Userdata {

  implicit val formats = Json.format[Userdata]

  val userForm = Form(
    mapping (
      "username" -> optional(nonEmptyText.verifying("Must not contain numbers!", c => c.matches("[a-zA-Z]*"))
        .verifying("Must be between 3 and 10 characters", s => s.matches("[a-zA-Z]{3,10}"))),
      "email" -> optional(email),
      "loginpasswd" -> optional(text(6, 15)),
      "password" -> optional(text(6, 15)),
      "confirmpassword" -> optional(text(6, 15))
    ) (Userdata.apply) (Userdata.unapply).verifying("Passwords must match", field => field.password.equals(field.confirmpassword))
  )


}


