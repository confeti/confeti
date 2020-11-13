package org.confeti.service;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import org.confeti.db.dao.conference.ConferenceBySpeakerDao;
import org.confeti.db.dao.conference.ConferenceDao;
import org.confeti.db.model.conference.ConferenceBySpeakerEntity;
import org.confeti.db.model.conference.ConferenceEntity;
import org.confeti.service.dto.Conference;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.UUID;

@Service
public final class ConferenceService extends AbstractEntityService<ConferenceEntity, Conference, ConferenceDao> {

    private SpeakerService speakerService;
    private final ConferenceBySpeakerDao conferenceBySpeakerDao;

    protected ConferenceService(final ConferenceDao conferenceDao,
                                final ConferenceBySpeakerDao conferenceBySpeakerDao) {
        super(conferenceDao);
        this.conferenceBySpeakerDao = conferenceBySpeakerDao;
    }

    @Autowired
    public void setSpeakerService(final SpeakerService speakerService) {
        this.speakerService = speakerService;
    }

    @NotNull
    @Override
    public Mono<Conference> upsert(@NotNull final Conference conference) {
        final var savedEntity = upsert(conference, ConferenceEntity::from).cache();
        return savedEntity
                .flatMapMany(conf -> speakerService.findBy(conf.getName(), conf.getYear()).zipWith(Mono.just(conf)))
                .flatMap(TupleUtils.function((speaker, conf) -> upsert(conf, speaker.getId())))
                .then(savedEntity.map(Conference::from));
    }

    @NotNull
    public Mono<Conference> upsert(@NotNull final Conference conference,
                                   @NotNull final UUID speakerId) {
        return upsert(
                upsert(conference),
                sp -> speakerService.findBy(speakerId)
                        .map(speaker -> ConferenceBySpeakerEntity.from(speaker.getId(), conference)),
                conferenceBySpeakerDao);
    }

    @NotNull
    private Mono<Conference> upsert(@NotNull final ConferenceEntity conference,
                                    @NotNull final UUID speakerId) {
        return upsert(
                upsert(conference).map(Conference::from),
                sp -> speakerService.findBy(speakerId)
                        .map(speaker -> ConferenceBySpeakerEntity.from(speaker.getId(), conference)),
                conferenceBySpeakerDao);
    }

    @NotNull
    public Mono<Conference> findBy(@NotNull final String conferenceName,
                                   @NotNull final Integer year) {
        return findOneBy(
                dao.findByNameForYear(conferenceName, year),
                Conference::from);
    }

    @NotNull
    public Flux<Conference> findBy(@NotNull final String conferenceName) {
        return findAllBy(
                dao.findByName(conferenceName),
                Conference::from);
    }

    @NotNull
    public Flux<Conference> findBy(@NotNull final UUID speakerId) {
        return findAllBy(
                conferenceBySpeakerDao.findBySpeakerId(speakerId),
                Conference::from);
    }

    @NotNull
    public Flux<Conference> findBy(@NotNull final UUID speakerId,
                                   @NotNull final Integer year) {
        return findAllBy(
                conferenceBySpeakerDao.findBySpeakerIdForYear(speakerId, year),
                Conference::from);
    }

    @NotNull
    @Override
    protected MappedReactiveResultSet<ConferenceEntity> findByPrimaryKey(@NotNull final Conference conference) {
        return dao.findByNameForYear(conference.getName(), conference.getYear());
    }
}
