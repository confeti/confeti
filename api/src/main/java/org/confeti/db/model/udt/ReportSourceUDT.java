package org.confeti.db.model.udt;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.confeti.service.dto.Report.ReportSource;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import static com.datastax.oss.driver.api.mapper.annotations.SchemaHint.TargetElement.UDT;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@SchemaHint(targetElement = UDT)
@CqlName(ReportSourceUDT.REPORT_SOURCE_UDT)
public class ReportSourceUDT implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_SOURCE_UDT = "report_source";
    public static final String REPORT_SOURCE_ATT_PRES_URL = "presentation_url";
    public static final String REPORT_SOURCE_ATT_REPO_URL = "repo_url";
    public static final String REPORT_SOURCE_ATT_VIDEO_URL = "video_url";
    public static final String REPORT_SOURCE_ATT_TALK_URL = "talk_url";
    public static final String REPORT_SOURCE_ATT_ARTICLE_URL = "article_url";

    @CqlName(REPORT_SOURCE_ATT_PRES_URL)
    private String presentationUrl;

    @CqlName(REPORT_SOURCE_ATT_REPO_URL)
    private String repoUrl;

    @CqlName(REPORT_SOURCE_ATT_VIDEO_URL)
    private String videoUrl;

    @CqlName(REPORT_SOURCE_ATT_TALK_URL)
    private String talkUrl;

    @CqlName(REPORT_SOURCE_ATT_ARTICLE_URL)
    private String articleUrl;

    @NotNull
    public static ReportSourceUDT from(@NotNull final ReportSource source) {
        return ReportSourceUDT.builder()
                .presentationUrl(source.getPresentation())
                .videoUrl(source.getVideo())
                .repoUrl(source.getRepo())
                .talkUrl(source.getTalk())
                .articleUrl(source.getArticle())
                .build();
    }

    @NotNull
    public static ReportSourceUDT from(@NotNull final ReportSourceUDT source) {
        return ReportSourceUDT.builder()
                .presentationUrl(source.getPresentationUrl())
                .videoUrl(source.getVideoUrl())
                .repoUrl(source.getRepoUrl())
                .talkUrl(source.getTalkUrl())
                .articleUrl(source.getArticleUrl())
                .build();
    }
}
