package org.confeti.service;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import org.confeti.db.dao.speaker.SpeakerByConferenceDao;
import org.confeti.db.dao.speaker.SpeakerDao;
import org.confeti.db.model.speaker.SpeakerEntity;
import org.confeti.exception.NotFoundException;
import org.confeti.handlers.dto.Speaker;
import org.confeti.util.converter.DtoToModelConverter;
import org.confeti.util.converter.ModelToDtoConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.confeti.exception.NotFoundException.Entity;

@Service
public final class SpeakerService extends AbstractEntityService<SpeakerEntity, Speaker, SpeakerDao> {

    private final SpeakerByConferenceDao speakerByConferenceDao;
    private ConferenceService conferenceService;

    protected SpeakerService(final SpeakerDao speakerDao,
                             final SpeakerByConferenceDao speakerByConferenceDao) {
        super(speakerDao);
        this.speakerByConferenceDao = speakerByConferenceDao;
    }

    public void setConferenceService(final ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @NotNull
    @Override
    public Mono<Speaker> upsert(@NotNull final Speaker speaker) {
        if (speaker.getId() == null) {
            speaker.setId(UUID.randomUUID());
        }
        final var savedSpeaker = upsert(speaker, DtoToModelConverter::convert, ModelToDtoConverter::convert);
        return savedSpeaker.flatMapMany(sp -> conferenceService.findBy(sp.getId()))
                .flatMap(conference -> upsert(speaker, conference.getName(), conference.getYear()))
                .then(savedSpeaker);
    }

    @NotNull
    Mono<Speaker> upsert(@NotNull final Speaker speaker,
                         @NotNull final String conferenceName,
                         @NotNull final Integer year) {
        return upsert(
                upsert(speaker, DtoToModelConverter::convert, ModelToDtoConverter::convert),
                sp -> conferenceService.findBy(conferenceName, year)
                        .then(Mono.just(DtoToModelConverter.convert(sp, conferenceName, year))),
                speakerByConferenceDao,
                ModelToDtoConverter::convert);
    }

    @NotNull
    @Override
    protected MappedReactiveResultSet<SpeakerEntity> findByPrimaryKey(@NotNull final Speaker speaker) {
        return dao.findById(speaker.getId());
    }

    @NotNull
    public Mono<Speaker> findBy(@NotNull final UUID id) {
        return findBy(
                dao.findById(id),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.SPEAKER, id.toString()));
    }

    @NotNull
    public Flux<Speaker> findBy(@NotNull final String conferenceName) {
        return findBy(
                speakerByConferenceDao.findByConferenceName(conferenceName),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.SPEAKER, conferenceName)).flux();
    }

    @NotNull
    public Flux<Speaker> findBy(@NotNull final String conferenceName,
                                @NotNull final Integer year) {
        return findBy(
                speakerByConferenceDao.findByConferenceNameForYear(conferenceName, year),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.SPEAKER, String.format("%s:%d", conferenceName, year))).flux();
    }
}
