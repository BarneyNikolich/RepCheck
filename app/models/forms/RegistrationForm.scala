package models.forms

import org.joda.time.LocalDate
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.data.format.Formats._

/**
  * Created by Barn on 22/04/2017.
  */
case class RegistrationForm(
                           username: String,
                           firtname: String,
                           lastname: String,
//                           dateOfBirth: String,
                           email: String,
                           phonenumber: String,
                           ebayscore: Int,
                           totalamazonsales: Int,
                           amazonscore: Double

                           )





object RegistrationForm {

  implicit val formats = Json.format[RegistrationForm]

  val form = Form(
    mapping(
      "username" -> nonEmptyText,
      "firstname" -> nonEmptyText,
      "lastname" -> nonEmptyText,
//      "dateOfBirth" -> nonEmptyText,
      "email" -> nonEmptyText,
      "phonenumber" -> nonEmptyText.verifying("Phone number must be 11 long, eg: 01234567891", e => e.matches("[0-9]{11}")),
      "ebayscore" -> number,
      "totalamazonsales" -> number,
      "amazonscore" -> of(doubleFormat)
    )(RegistrationForm.apply)(RegistrationForm.unapply)
  )

}