package models


import play.api.data.Form
import play.api.data.Forms._
/**
  * Created by Barn on 06/03/2017.
  */
case class Userdata(username: String,
                    password: String)

object Userdata {

  val userForm = Form(
    mapping (
      "name" -> nonEmptyText.verifying("Must not contain numbers!", c => c.matches("[a-zA-Z]*"))
        .verifying("Must be between 4 and 10 characters", s => s.matches("[a-zA-Z]{4,10}")),
      "password" -> text(6, 15)
    ) (Userdata.apply) (Userdata.unapply)
  )


}


