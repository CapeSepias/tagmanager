package cowboy
import services.Config
import model.command.CreateNewspaperBook
import scala.concurrent._
import scala.concurrent.duration._

object CreateNewspaperBooks extends App {

  // Initialize config
  Config()

  // User for auditing (which doesnt seem to work for some reason lmao)
  implicit val username: Option[String] = Some("Sam Cutler")

  println("--------------------------------------------------------------")
  println("--------------------------------------------------------------")
  println("Creating Observer Design NB")

  // Create newspaper books
  Await.result(
    CreateNewspaperBook(
      internalName = "Gdn: Saturday Magazine (nb)",
      externalName = "Saturday",
      slug = "saturday",
      preCalculatedPath = "theguardian/saturday",

      publication = 2,
      section = 196,
      capiSectionId = "lifeandstyle"
    ).process, 10.seconds)

  println("--------------------------------------------------------------")
  println("--------------------------------------------------------------")
}
