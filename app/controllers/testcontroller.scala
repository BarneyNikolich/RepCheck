package controllers

import controllers.auth.AuthAction
import models.{Company, Computer}
import play.api.data.Form
import play.api.mvc.Action
import views.html

import play.api.data.Forms._

import play.api.Play.current
import play.api.i18n.Messages.Implicits._


/**
  * Created by Barn on 27/04/2017.
  */
class testcontroller extends AuthAction {

  val Home = Redirect(routes.testcontroller.list(0, 2, ""))


  /**
    * Describe the computer form (used in both edit and create screens).
    */
  val computerForm = Form(
    mapping(
      "id" -> ignored(None:Option[Long]),
      "name" -> nonEmptyText,
      "introduced" -> optional(date("yyyy-MM-dd")),
      "discontinued" -> optional(date("yyyy-MM-dd")),
      "company" -> optional(longNumber)
    )(Computer.apply)(Computer.unapply)
  )

  /**
    * Handle default path requests, redirect to computers list
    */
  def index = Action { Home }


  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.list(
      models.Transaction.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

  /**
    * Display the 'edit form' of a existing Computer.
    *
    * @param id Id of the computer to edit
    */
  def edit(id: Long) = Action {
    Computer.findById(id).map { computer =>
      Ok(html.editForm(id, computerForm.fill(computer), Company.options))
    }.getOrElse(NotFound)
  }

  /**
    * Handle the 'edit form' submission
    *
    * @param id Id of the computer to edit
    */
  def update(id: Long) = Action { implicit request =>
    computerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id, formWithErrors, Company.options)),
      computer => {
        Computer.update(id, computer)
        Home.flashing("success" -> "Computer %s has been updated".format(computer.name))
      }
    )
  }

  /**
    * Display the 'new computer form'.
    */
  def create = Action {
    Ok(html.createForm(computerForm, Company.options))
  }

  /**
    * Handle the 'new computer form' submission.
    */
  def save = Action { implicit request =>
    computerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors, Company.options)),
      computer => {
        Computer.insert(computer)
        Home.flashing("success" -> "Computer %s has been created".format(computer.name))
      }
    )
  }

  /**
    * Handle computer deletion.
    */
  def delete(id: Long) = Action {
    Computer.delete(id)
    Home.flashing("success" -> "Computer has been deleted")
  }


}
