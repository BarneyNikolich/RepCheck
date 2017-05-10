package controllers

import javafx.beans.binding.DoubleExpression

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

  def showAcc(name: String) = Action {
    Redirect(routes.AccountController.showAccount(name, None, None, None))
  }

  def showAccount(name: String, newPage : Option[Int], newOrder: Option[Int], newFilter: Option[String]) = Action { implicit request =>

    loggedinUser = Some(request.session.get("loggedin")).getOrElse(None)

    if (userExists(name)) {

      CurrentUser.findByUsername(name) map { user =>

        val picLocation = "/assets"+user.profilepiclocation
        val fullname = user.firtname.capitalize + " " + user.lastname.capitalize
        val page = newPage.getOrElse(0)
        val orderBy = newOrder.getOrElse(2)
        val filter = newFilter.getOrElse(name)
        val ebayscore = user.ebayscore


        val transactions = models.Transaction.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%"))

        val total = transactions.total


        var mean: Option[Double] = None
        var variance: Option[Double] = None

        var fiveCount: Option[Int] = None
        var fourCount:  Option[Int] = None
        var threeCount:  Option[Int] = None
        var twoCount:  Option[Int] = None
        var oneCount:  Option[Int] = None

        var fivePercent: Option[Int] = None
        var fourPercent:  Option[Int] = None
        var threePercent:  Option[Int] = None
        var twoPercent:  Option[Int] = None
        var onePercent:  Option[Int] = None





        var percentages: models.RatingData =  models.RatingData(0, 0, 0, 0, 0, None, 0 ,0)

        val ratingInfo = calculateVariance(name, page, orderBy, filter)

        if(ratingInfo.isDefined){
          mean = Some(ratingInfo.get.average)
          variance = Some(ratingInfo.get.variance - (ratingInfo.get.variance % 0.01))

          fiveCount = Some(ratingInfo.get.fiveCount)
          fourCount = Some(ratingInfo.get.fourCount)
          threeCount = Some(ratingInfo.get.threeCount)
          twoCount = Some(ratingInfo.get.twoCount)
          oneCount = Some(ratingInfo.get.oneCount)

          fivePercent = Some((fiveCount.get.toDouble / total.toDouble * 100).toInt)
          fourPercent = Some((fourCount.get.toDouble / total.toDouble * 100).toInt)
          threePercent = Some((threeCount.get.toDouble / total.toDouble * 100).toInt)
          twoPercent = Some((twoCount.get.toDouble / total.toDouble * 100).toInt)
          onePercent = Some((oneCount.get.toDouble / total.toDouble * 100).toInt)

          percentages = models.RatingData(fivePercent.get, fourPercent.get, threePercent.get, twoPercent.get, onePercent.get, Some(mean.get), variance.get, total)

        }






        Ok(views.html.accountPage(user.username, fullname, user.phonenumber, user.email, picLocation, user.ebayname, transactions,
          orderBy, filter, getFacebookLink(user.facebookemail), fiveCount, fourCount, threeCount, twoCount, oneCount, percentages, ebayscore))


      } getOrElse( NotFound("Couldnt find by username!") )


    } else {
        Redirect(routes.HomeController.index())
    }
  }

  def getFacebookLink(facebookemail: String) = {
    if(facebookemail != "None") {

      val first =  "https://www.facebook.com/search/str/"
      val split = facebookemail.split("@")
      first + split.mkString("%40") + "/keywords_top"
    } else {
      facebookemail
    }
  }

case class RatingData(variance: Double,
                      average: Double,
                      fiveCount: Int,
                      fourCount: Int,
                      threeCount: Int,
                      twoCount: Int,
                      oneCount: Int
                     )

  def calculateVariance(name: String, page: Int, orderBy: Int, filter: String): Option[RatingData] = {

    import scala.collection.mutable.ListBuffer


    var ratingList = ListBuffer[Int]()
    var ratingMeanSquared = ListBuffer[Double]()

    val temp = models.Transaction.list(page, 50, orderBy = orderBy, filter=  ("%"+filter+"%"))
    temp.items.map { transactions =>
      ratingList += transactions._1.rating
    }

    val mean = ratingList.sum.toDouble / ratingList.length.toDouble

    var fiveCount = 0
    var fourCount = 0
    var threeCount = 0
    var twoCount = 0
    var oneCount = 0


    ratingList map { x =>

      if(x == 5) {
        fiveCount += 1
      } else if(x == 4) {
        fourCount += 1
      } else if(x == 3) {
        threeCount += 1
      } else if(x == 2) {
        twoCount += 1
      } else {
        oneCount += 1
      }


      ratingMeanSquared += (x-mean) * (x-mean)
    }

    val variance: Double = ratingMeanSquared.sum / ratingMeanSquared.length.toDouble

    RatingData(variance, mean, fiveCount, fourCount, threeCount, twoCount, oneCount)
    if(variance.toString == "NaN" || mean.toString == "NaN"){
      None
    }else {
      Some(RatingData(variance, mean, fiveCount, fourCount, threeCount, twoCount, oneCount))

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
