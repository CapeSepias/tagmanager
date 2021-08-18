package cowboy

import repositories.ContentAPI
import services.{Config, KinesisStreams}

object UnexpireContent extends App {
  Config()

  println("Unexpiring content for 'travel/series/travel-folktales-for-kids'")
  List(
    "books/audio/2017/jun/09/the-legend-of-the-almond-trees-read-by-andrew-scott-travel-folktales-for-kids-podcast",
    "books/audio/2017/jun/02/the-dragon-of-palma-de-mallorca-read-andrew-scott-travel-folktales-for-kids-podcast",
    "books/audio/2017/may/26/the-wandering-knight-of-zagreb-read-by-andrew-scott-travel-folktales-for-kids-podcast",
    "books/audio/2017/may/19/pinocchio-and-the-field-of-miracles-read-by-andrew-scott-travel-folktales-for-kids-podcast",
    "books/audio/2017/may/12/jack-the-giant-killer-read-by-andrew-scott-travel-folktales-for-kids-podcast"
  ) foreach { contentId => KinesisStreams.commercialExpiryStream.publishUpdate(contentId, "false") }
  println("done")
}