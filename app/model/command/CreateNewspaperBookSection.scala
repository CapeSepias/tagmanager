package model.command

import com.gu.tagmanagement.{EventType, SectionEvent, TagEvent}
import org.apache.commons.lang3.StringUtils
import org.joda.time.{DateTime, DateTimeZone}
import ai.x.play.json.Jsonx
import ai.x.play.json.Encoders.encoder
import ai.x.play.json.implicits.optionWithNull
import repositories._
import CommandError._
import model.command.logic.TagPathCalculator
import model.{PodcastMetadata, Reference, Tag, TagAudit}
import services.{KinesisStreams}


import scala.concurrent.{Future, ExecutionContext}
case class CreateNewspaperBookSection(
                             internalName: String,
                             externalName: String,
                             slug: String,
                             preCalculatedPath: String,

                             parentNewspaperBook: Long,

                             section: Long,
                             capiSectionId: Option[String]
                           ) extends Command {

  type T = Tag

  def process()(implicit username: Option[String] = None,  ec: ExecutionContext): Future[Option[Tag]] = Future {

    val tagId = Sequences.tagId.getNextId

    val sectionId = Some(section)

    val pageId = try { PathManager.registerPathAndGetPageId(preCalculatedPath) } catch { case p: PathRegistrationFailed => PathInUse}

    val tag = Tag(
      id = tagId,
      path = preCalculatedPath,
      pageId = pageId,
      internalName = internalName,
      externalName = externalName,
      slug = slug,
      section = sectionId,
      capiSectionId = capiSectionId, // <-- this corresponds to the section id but he capi version
      parents = Set(parentNewspaperBook),

      // Hardcoded
      `type`= "NewspaperBookSection",
      comparableValue = externalName.toLowerCase(),
      hidden = false,
      legallySensitive = false,
      publication = None,
      categories = Set(),
      description = None,
      externalReferences = Nil,
      podcastMetadata = None,
      contributorInformation = None,
      publicationInformation = None,
      isMicrosite = false,
      trackingInformation = None,
      campaignInformation = None,
      activeSponsorships = Nil,
      sponsorship = None,
      paidContentInformation = None,
      expired = false,
      adBlockingLevel = None,
      contributionBlockingLevel = None,
      updatedAt = new DateTime(DateTimeZone.UTC).getMillis
    )

    val result = TagRepository.upsertTag(tag)

    KinesisStreams.tagUpdateStream.publishUpdate(tag.id.toString, TagEvent(EventType.Update, tag.id, Some(tag.asThrift)))

    TagAuditRepository.upsertTagAudit(TagAudit.created(tag))

    result
  }
}

object CreateNewspaperBookSection {
  implicit val createNewspaperBookSectionCommandFormat = Jsonx.formatCaseClassUseDefaults[CreateNewspaperBookSection]
}