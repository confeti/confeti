package org.confeti.db.model.udt;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private static final long serialVersionUID = 0L;

    public static final String REPORT_SOURCE_UDT = "report_source";
    public static final String REPORT_SOURCE_ATT_PRESENTATION_URL = "presentation_url";
    public static final String REPORT_SOURCE_ATT_REPO_URL = "repo_url";
    public static final String REPORT_SOURCE_ATT_VIDEO_URL = "video_url";

    @CqlName(REPORT_SOURCE_ATT_PRESENTATION_URL)
    private String presentationUrl;

    @CqlName(REPORT_SOURCE_ATT_REPO_URL)
    private String repoUrl;

    @CqlName(REPORT_SOURCE_ATT_VIDEO_URL)
    private String videoUrl;
}
