package controllers

import play.api.mvc.{Action, Controller}
import repositories.{TagSearchCriteria, TagRepository}
import play.api.Logger
import services.LogShipping

object App extends Controller with PanDomainAuthActions {

  LogShipping.bootstrap

  def index(id: String = "") = AuthAction {

    val jsFileName = "build/app.js"

    val jsLocation = sys.env.get("JS_ASSET_HOST") match {
      case Some(assetHost) => assetHost + jsFileName
      case None => routes.Assets.versioned(jsFileName).toString
    }

    Ok(views.html.Application.app("Tag Manager", jsLocation))
  }

  def hello = AuthAction {
    Logger.info("saying hello")
    Ok(views.html.Application.hello("Hello world"))
  }

  def testScan = Action { req =>

    val criteria = TagSearchCriteria(
      q = req.getQueryString("q"),
      types = req.getQueryString("types").map(_.split(",").toList)
    )

    val startTime = System.currentTimeMillis
    val paths = TagRepository.scanSearch(criteria)
    val time = System.currentTimeMillis - startTime

    paths foreach println
    Ok(s"scan complete ${paths.size} items in $time ms")
  }

}
