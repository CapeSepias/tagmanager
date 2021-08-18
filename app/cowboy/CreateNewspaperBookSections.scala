package cowboy

import scala.concurrent._
import scala.concurrent.duration._

import services.Config
import model.command.CreateNewspaperBookSection

import scala.concurrent.ExecutionContext.Implicits.global
object CreateNewspaperBookSections extends App {

  // Initialize config
  Config()

  implicit val username: Option[String] = Some("Sam Cutler")


  println("--------------------------------------------------------------")
  println("--------------------------------------------------------------")
  println("Creating Guardian Saturday Cuttings NBS")

  Await.result(CreateNewspaperBookSection(
    internalName = "Gdn: Saturday Cuttings (nbs)",
    externalName = "Cuttings",
    slug = "cuttings",
    preCalculatedPath = "theguardian/saturday/cuttings",

    parentNewspaperBook = 90568,
    section = 196,
    capiSectionId = Some("lifeandstyle")
  ).process, 10.seconds)

  println("--------------------------------------------------------------")
  println("--------------------------------------------------------------")
}