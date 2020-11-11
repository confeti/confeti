package org.confeti.service;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import org.confeti.db.dao.speaker.SpeakerByConferenceDao;
import org.confeti.db.dao.speaker.SpeakerDao;
import org.confeti.db.model.speaker.SpeakerByConferenceEntity;
import org.confeti.db.model.speaker.SpeakerEntity;
import org.confeti.service.dto.Speaker;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.UUID;

@Service
public final class SpeakerService extends AbstractEntityService<SpeakerEntity, Speaker, SpeakerDao> {

    private final SpeakerByConferenceDao speakerByConferenceDao;
    private ConferenceService conferenceService;

    protected SpeakerService(final SpeakerDao speakerDao,
                             final SpeakerByConferenceDao speakerByConferenceDao) {
        super(speakerDao);
        this.speakerByConferenceDao = speakerByConferenceDao;
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
        final var speakerEntity = SpeakerEntity.from(speaker);
        final var savedEntity = Mono.from(findByPrimaryKey(speaker))
                .doOnNext(se -> se.updateFrom(speakerEntity))
                .defaultIfEmpty(speakerEntity)
                .flatMap(this::upsert)
                .cache();
        return savedEntity
                .flatMapMany(se -> conferenceService.findBy(se.getId()).zipWith(Mono.just(se)))
                .flatMap(TupleUtils.function((conf, se) -> upsert(se, conf.getName(), conf.getYear())))
                .then(savedEntity.map(Speaker::from));
    }

    @NotNull
    public Mono<Speaker> upsert(@NotNull final Speaker speaker,
                                @NotNull final String conferenceName,
                                @NotNull final Integer year) {
        return upsert(SpeakerEntity.from(speaker), conferenceName, year)
                .map(Speaker::from);
    }

    @NotNull
    private Mono<SpeakerEntity> upsert(@NotNull final SpeakerEntity speakerEntity,
                                       @NotNull final String conferenceName,
                                       @NotNull final Integer year) {
        return upsert(
                upsert(speakerEntity),
                sp -> conferenceService.findBy(conferenceName, year)
                        .map(conference -> SpeakerByConferenceEntity.from(conference.getName(), year, sp)),
                speakerByConferenceDao);
    }

    @NotNull
    public Mono<Speaker> findBy(@NotNull final UUID id) {
        return findOneBy(dao.findById(id), Speaker::from);
    }

    @NotNull
    public Flux<Speaker> findBy(@NotNull final String conferenceName) {
        return findAllBy(
                speakerByConferenceDao.findByConferenceName(conferenceName),
                Speaker::from);
    }

    @NotNull
    public Flux<Speaker> findBy(@NotNull final String conferenceName,
                                @NotNull final Integer year) {
        return findAllBy(
                speakerByConferenceDao.findByConferenceNameForYear(conferenceName, year),
                Speaker::from);
    }

    @NotNull
    @Override
    protected MappedReactiveResultSet<SpeakerEntity> findByPrimaryKey(@NotNull final Speaker dto) {
        return dao.findById(dto.getId());
    }
}
