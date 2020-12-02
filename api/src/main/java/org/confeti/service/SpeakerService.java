package org.confeti.service;

import org.confeti.db.dao.speaker.SpeakerByConferenceDao;
import org.confeti.db.dao.speaker.SpeakerDao;
import org.confeti.db.model.speaker.SpeakerByConferenceEntity;
import org.confeti.db.model.speaker.SpeakerEntity;
import org.confeti.service.dto.Company;
import org.confeti.service.dto.Speaker;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.Optional;
import java.util.UUID;

import static org.confeti.service.BaseEntityService.findMany;
import static org.confeti.service.BaseEntityService.findOne;

@Service
public final class SpeakerService extends AbstractEntityService<SpeakerEntity, Speaker, SpeakerDao> {

    private final SpeakerByConferenceDao speakerByConferenceDao;
    private final CompanyService companyService;
    private ConferenceService conferenceService;

    protected SpeakerService(final SpeakerDao speakerDao,
                             final SpeakerByConferenceDao speakerByConferenceDao,
                             final CompanyService companyService) {
        super(speakerDao);
        this.speakerByConferenceDao = speakerByConferenceDao;
        this.companyService = companyService;
    }

    @Autowired
    public void setConferenceService(final ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @NotNull
    @Override
    public Mono<Speaker> upsert(@NotNull final Speaker speaker) {
        if (speaker.getId() == null) {
            speaker.setId(UUID.randomUUID());
        }

        final var savedEntity = upsertCompany(speaker)
                .flatMapMany(sp -> findByName(sp.getName()))
                .filter(foundSpeaker -> foundSpeaker.canBeUpdatedTo(speaker))
                .collectList()
                .map(foundSpeakers -> foundSpeakers.isEmpty()
                        ? speaker
                        : Speaker.updateOrNew(foundSpeakers.get(0), speaker))
                .flatMap(sp -> upsert(sp, SpeakerEntity::from)).cache();
        return savedEntity
                .flatMapMany(se -> conferenceService.findBy(se.getId()).zipWith(Mono.just(se)))
                .flatMap(TupleUtils.function((conf, se) -> upsert(se, conf.getName(), conf.getYear())))
                .then(savedEntity.map(Speaker::from));
    }

    @NotNull
    public Mono<Speaker> upsert(@NotNull final Speaker speaker,
                                @NotNull final String conferenceName,
                                @NotNull final Integer year) {
        return upsert(
                upsert(speaker),
                sp -> conferenceService.findBy(conferenceName, year)
                        .map(conference -> SpeakerByConferenceEntity.from(conference.getName(), year, sp)),
                speakerByConferenceDao);
    }

    @NotNull
    private Mono<Speaker> upsert(@NotNull final SpeakerEntity speaker,
                                 @NotNull final String conferenceName,
                                 @NotNull final Integer year) {
        return upsert(
                upsert(speaker).map(Speaker::from),
                sp -> conferenceService.findBy(conferenceName, year)
                        .map(conference -> SpeakerByConferenceEntity.from(conference.getName(), year, sp)),
                speakerByConferenceDao);
    }

    @NotNull
    private Mono<Speaker> upsertCompany(@NotNull final Speaker speaker) {
        final var companyName = speaker.getContactInfo() != null && speaker.getContactInfo().getCompany() != null
                ? Optional.ofNullable(speaker.getContactInfo().getCompany().getName())
                : Optional.<String>empty();
        return Mono.justOrEmpty(companyName)
                .flatMap(company -> companyService.upsert(Company.builder(company).build()))
                .then(Mono.just(speaker));
    }

    @NotNull
    public Flux<Speaker> findByName(@NotNull final String name) {
        return findMany(dao.findByName(name), Speaker::from);
    }

    @NotNull
    public Mono<Speaker> findBy(@NotNull final UUID id) {
        return findOne(dao.findById(id), Speaker::from);
    }

    @NotNull
    public Flux<Speaker> findBy(@NotNull final String conferenceName) {
        return findMany(
                speakerByConferenceDao.findByConferenceName(conferenceName),
                Speaker::from);
    }

    @NotNull
    public Flux<Speaker> findBy(@NotNull final String conferenceName,
                                @NotNull final Integer year) {
        return findMany(
                speakerByConferenceDao.findByConferenceNameForYear(conferenceName, year),
                Speaker::from);
    }

    @NotNull
    public Flux<Speaker> findAll() {
        return findMany(dao.findAll(), Speaker::from);
    }

    @NotNull
    @Override
    protected Mono<SpeakerEntity> findByPrimaryKey(@NotNull final Speaker speaker) {
        return Mono.from(dao.findById(speaker.getId()));
    }
}
