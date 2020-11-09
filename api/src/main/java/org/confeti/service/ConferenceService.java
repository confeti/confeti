package org.confeti.service;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import org.confeti.db.dao.conference.ConferenceBySpeakerDao;
import org.confeti.db.dao.conference.ConferenceDao;
import org.confeti.db.model.conference.ConferenceEntity;
import org.confeti.exception.NotFoundException;
import org.confeti.handlers.dto.Conference;
import org.confeti.util.converter.DtoToModelConverter;
import org.confeti.util.converter.ModelToDtoConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.confeti.exception.NotFoundException.Entity;

@Service
public final class ConferenceService extends AbstractEntityService<ConferenceEntity, Conference, ConferenceDao> {

    private final SpeakerService speakerService;
    private final ConferenceBySpeakerDao conferenceBySpeakerDao;

    protected ConferenceService(final ConferenceDao conferenceDao,
                                final ConferenceBySpeakerDao conferenceBySpeakerDao,
                                final SpeakerService speakerService) {
        super(conferenceDao);
        this.conferenceBySpeakerDao = conferenceBySpeakerDao;
        this.speakerService = speakerService;
    }

    @NotNull
    @Override
    public Mono<Conference> upsert(@NotNull final Conference conference) {
        final var savedConference = upsert(conference, DtoToModelConverter::convert, ModelToDtoConverter::convert);
        return savedConference.flatMapMany(conf -> speakerService.findBy(conf.getName(), conf.getYear()))
                .flatMap(speaker -> upsert(conference, speaker.getId()))
                .then(savedConference);
    }

    @NotNull
    Mono<Conference> upsert(@NotNull final Conference conference,
                            @NotNull final UUID speakerId) {
        return upsert(
                upsert(conference, DtoToModelConverter::convert, ModelToDtoConverter::convert),
                conf -> speakerService.findBy(speakerId)
                        .then(Mono.just(DtoToModelConverter.convert(conf, speakerId))),
                conferenceBySpeakerDao,
                ModelToDtoConverter::convert);
    }

    @NotNull
    @Override
    protected MappedReactiveResultSet<ConferenceEntity> findByPrimaryKey(@NotNull final Conference conference) {
        return dao.findByNameForYear(conference.getName(), conference.getYear());
    }

    @NotNull
    public Mono<Conference> findBy(@NotNull final String conferenceName,
                                   @NotNull final Integer year) {
        return findBy(
                dao.findByNameForYear(conferenceName, year),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.CONFERENCE, String.format("%s:%d", conferenceName, year)));
    }

    @NotNull
    public Flux<Conference> findBy(@NotNull final String conferenceName) {
        return findBy(
                dao.findByName(conferenceName),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.CONFERENCE, conferenceName)).flux();
    }

    @NotNull
    public Flux<Conference> findBy(@NotNull final UUID speakerId) {
        return findBy(
                conferenceBySpeakerDao.findBySpeakerId(speakerId),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.CONFERENCE, speakerId.toString())).flux();
    }

    @NotNull
    public Flux<Conference> findBy(@NotNull final UUID speakerId,
                                   @NotNull final Integer year) {
        return findBy(
                conferenceBySpeakerDao.findBySpeakerIdForYear(speakerId, year),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.CONFERENCE, String.format("%s:%d", speakerId.toString(), year))).flux();
    }
}
