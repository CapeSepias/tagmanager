package controllers

import com.squareup.okhttp.{Request, OkHttpClient, Credentials}
import org.cvogt.play.json.Jsonx
import play.api.Logger
import play.api.libs.json.{JsString, Json}
import play.api.mvc.{Action, Controller}
import repositories.TagLookupCache
import services.{Config, ImageMetadataService}
import java.util.concurrent.TimeUnit

object Support extends Controller with PanDomainAuthActions {

  def imageMetadata(imageUrl: String = "") = APIAuthAction {
    ImageMetadataService.fetch(imageUrl).map { imageMetadata =>
      Ok(Json.toJson(imageMetadata))
    }.getOrElse(NotFound)
  }

  def previewCapiProxy(path: String) = APIAuthAction { request =>
    val httpClient = new OkHttpClient()

    val url = s"${Config().capiPreviewUrl}/${path}?${request.rawQueryString}"
    Logger.info(s"Requesting CAPI preview -> ${url}")

    val req = new Request.Builder()
      .url(url)
      .header("Authorization", Credentials.basic(Config().capiPreviewUser, Config().capiPreviewPassword))
      .build

    httpClient.setConnectTimeout(5, TimeUnit.SECONDS)
    val resp = httpClient.newCall(req).execute

    resp.code match {
      case 200 => {
        Ok(resp.body.string).as(JSON)
      }
      case c => {
        BadRequest
      }
    }

  }


  def flexMigrationSpecificData = Action {
    Ok(
      Json.toJson(TagLookupCache.allTags.get.map(tag => tag.id.toString -> JsString(tag.path)).toMap)
    )
  }
}
