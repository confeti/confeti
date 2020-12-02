package org.confeti.service;

import org.confeti.service.dto.Company;
import org.confeti.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.confeti.support.TestUtil.generateCompany;
import static org.confeti.support.TestUtil.generateSpeaker;
import static org.confeti.support.TestUtil.updateCompany;

public class CompanyServiceTest extends AbstractIntegrationTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SpeakerService speakerService;

    @Test
    public void testInsertCompany() {
        final var company = generateCompany();

        StepVerifier.create(companyService.upsert(company))
                .expectNext(company)
                .expectComplete()
                .verify();
    }

    @Test
    public void testInsertConferenceWhenInsertSpeaker() {
        final var speaker = generateSpeaker();
        final var companyName = speaker.getContactInfo().getCompany().getName();
        speakerService.upsert(speaker).block();

        final var expectedCompany = Company.builder(companyName).build();

        StepVerifier.create(companyService.findBy(companyName))
                .expectNext(expectedCompany)
                .expectComplete()
                .verify();
    }

    @Test
    public void testUpdateCompany() {
        final var company = generateCompany();
        final var updatedCompany = updateCompany(company);
        companyService.upsert(company).block();

        StepVerifier.create(companyService.upsert(updatedCompany))
                .expectNext(updatedCompany)
                .expectComplete()
                .verify();

        StepVerifier.create(companyService.findBy(company.getName()))
                .expectNext(updatedCompany)
                .expectComplete()
                .verify();
    }
}
