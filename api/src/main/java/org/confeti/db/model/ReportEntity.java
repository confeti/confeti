package org.confeti.db.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.confeti.db.model.udt.ConferenceShortInfoUDT;
import org.confeti.db.model.udt.ReportSourceUDT;
import org.confeti.db.model.udt.SpeakerFullInfoUDT;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@CqlName(ReportEntity.REPORT_TABLE)
public class ReportEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String REPORT_TABLE = "report";
    public static final String REPORT_ATT_ID = "id";
    public static final String REPORT_ATT_TITLE = "title";
    public static final String REPORT_ATT_COMPLEXITY = "complexity";
    public static final String REPORT_ATT_CONFERENCES = "conferences";
    public static final String REPORT_ATT_DESCRIPTION = "description";
    public static final String REPORT_ATT_LANGUAGE = "language";
    public static final String REPORT_ATT_SOURCE = "source";
    public static final String REPORT_ATT_SPEAKERS = "speakers";
    public static final String REPORT_ATT_TAGS = "tags";

    @PartitionKey
    @CqlName(REPORT_ATT_ID)
    private UUID id;

    @ClusteringColumn
    @CqlName(REPORT_ATT_TITLE)
    private String title;

    @CqlName(REPORT_ATT_COMPLEXITY)
    private Integer complexity;

    @CqlName(REPORT_ATT_CONFERENCES)
    private Set<ConferenceShortInfoUDT> conferences;

    @CqlName(REPORT_ATT_DESCRIPTION)
    private String description;

    @CqlName(REPORT_ATT_LANGUAGE)
    private String language;

    @CqlName(REPORT_ATT_SOURCE)
    private ReportSourceUDT source;

    @CqlName(REPORT_ATT_SPEAKERS)
    private Set<SpeakerFullInfoUDT> speakers;

    @CqlName(REPORT_ATT_TAGS)
    private Set<String> tags;

    public enum Complexity {
        INTRODUCTION_TO_TECHNOLOGY(0, "Introduction to technology"),
        FOR_PRACTICING_ENGINEERS(1, "For practicing engineers"),
        HARDCORE(2, "Hardcore"),
        ACADEMIC_TALK(3, "Academic talk");

        private final int value;

        @NotNull
        private final String description;

        Complexity(final int value,
                   @NotNull final String description) {
            this.value = value;
            this.description = description;
        }

        public int getId() {
            return value;
        }

        @NotNull
        public String getDescription() {
            return description;
        }

        public static Complexity valueOf(final int value) {
            return switch (value) {
                case 0 -> INTRODUCTION_TO_TECHNOLOGY;
                case 1 -> FOR_PRACTICING_ENGINEERS;
                case 2 -> HARDCORE;
                case 3 -> ACADEMIC_TALK;
                default -> throw new IllegalArgumentException("Unexpected value: " + value);
            };
        }
    }
}
