package mixit.web.handler

import mixit.model.*
import mixit.repository.EventRepository
import mixit.repository.UserRepository
import mixit.util.language
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import java.time.LocalDate

@Component
class SponsorHandler(private val userRepository: UserRepository,
                     private val eventRepository: EventRepository) {

    fun viewWithSponsors(view: String, title: String?, req: ServerRequest) = eventRepository.findOne("mixit17").flatMap { event ->
        userRepository.findMany(event.sponsors.map { it.sponsorId }).collectMap(User::login).flatMap { sponsorsByLogin ->
            val sponsorsByEvent = event.sponsors.groupBy { it.level }
            ServerResponse.ok().render(view, mapOf(
                    Pair("sponsors-gold", sponsorsByEvent[SponsorshipLevel.GOLD]?.map { it.toDto(sponsorsByLogin[it.sponsorId]!!, req.language()) }),
                    Pair("sponsors-silver", sponsorsByEvent[SponsorshipLevel.SILVER]?.map { it.toDto(sponsorsByLogin[it.sponsorId]!!, req.language()) }),
                    Pair("sponsors-hosting", sponsorsByEvent[SponsorshipLevel.HOSTING]?.map { it.toDto(sponsorsByLogin[it.sponsorId]!!, req.language()) }),
                    Pair("sponsors-lanyard", sponsorsByEvent[SponsorshipLevel.LANYARD]?.map { it.toDto(sponsorsByLogin[it.sponsorId]!!, req.language()) }),
                    Pair("sponsors-party", sponsorsByEvent[SponsorshipLevel.PARTY]?.map { it.toDto(sponsorsByLogin[it.sponsorId]!!, req.language()) }),
                    Pair("sponsors-video", sponsorsByEvent[SponsorshipLevel.VIDEO]?.map { it.toDto(sponsorsByLogin[it.sponsorId]!!, req.language()) }),
                    Pair("title", title)
            ))
    }}

}

private class EventSponsoringDto(
        val level: SponsorshipLevel,
        val sponsor: UserDto,
        val subscriptionDate: LocalDate = LocalDate.now()
)

private fun EventSponsoring.toDto(sponsor: User, language: Language) =
        EventSponsoringDto(level, sponsor.toDto(language), subscriptionDate)
