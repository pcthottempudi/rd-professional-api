package uk.gov.hmcts.reform.professionalapi;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.professionalapi.utils.OrganisationFixtures.organisationRequestWithAllFields;
import static uk.gov.hmcts.reform.professionalapi.utils.OrganisationFixtures.organisationRequestWithAllFieldsAreUpdated;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.professionalapi.controller.request.OrganisationCreationRequest;
import uk.gov.hmcts.reform.professionalapi.domain.ContactInformation;
import uk.gov.hmcts.reform.professionalapi.domain.Organisation;
import uk.gov.hmcts.reform.professionalapi.domain.OrganisationStatus;
import uk.gov.hmcts.reform.professionalapi.domain.PaymentAccount;
import uk.gov.hmcts.reform.professionalapi.domain.ProfessionalUser;
import uk.gov.hmcts.reform.professionalapi.persistence.ContactInformationRepository;
import uk.gov.hmcts.reform.professionalapi.persistence.DxAddressRepository;
import uk.gov.hmcts.reform.professionalapi.persistence.OrganisationRepository;
import uk.gov.hmcts.reform.professionalapi.persistence.PaymentAccountRepository;
import uk.gov.hmcts.reform.professionalapi.persistence.ProfessionalUserRepository;
import uk.gov.hmcts.reform.professionalapi.util.ProfessionalReferenceDataClient;
import uk.gov.hmcts.reform.professionalapi.util.Service2ServiceEnabledIntegrationTest;

public class UpdateOrganisationTest extends Service2ServiceEnabledIntegrationTest {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ProfessionalUserRepository professionalUserRepository;

    @Autowired
    private ContactInformationRepository contactInformationRepository;

    @Autowired
    private DxAddressRepository dxAddressRepository;

    @Autowired
    private PaymentAccountRepository paymentAccountRepository;

    private ProfessionalReferenceDataClient professionalReferenceDataClient;

    @Before
    public void setUp() {
        professionalReferenceDataClient = new ProfessionalReferenceDataClient(port);
        dxAddressRepository.deleteAll();
        contactInformationRepository.deleteAll();
        professionalUserRepository.deleteAll();
        paymentAccountRepository.deleteAll();
        organisationRepository.deleteAll();
    }

    @Test
    public void updates_non_existing_organisation_returns_status_404() {
        updateAndValidateOrganisation(UUID.randomUUID().toString(),OrganisationStatus.ACTIVE,404);
    }

    @Test
    public void updates_organisation_with_organisation_identifier_null_returns_status_400() {
        updateAndValidateOrganisation(null,OrganisationStatus.ACTIVE,400);
    }

    @Test
    public void updates_organisation_with_invalid_organisation_identifier_returns_status_400() {
        updateAndValidateOrganisation("1234ab12",OrganisationStatus.ACTIVE,400);
    }

    @Test
    public void can_update_organisation_status_from_pending_to_active_should_returns_status_200() {
        updateAndValidateOrganisation(createOrganisationRequest(),OrganisationStatus.ACTIVE,200);
    }

    @Test
    public void can_update_organisation_status_from_active_to_blocked_should_returns_status_200() {
        String organisationIdentifier = createOrganisationRequest();
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.ACTIVE,200);
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.BLOCKED,200);
    }

    @Test
    public void can_update_organisation_status_from_active_to_deleted_should_returns_status_200() {
        String organisationIdentifier = createOrganisationRequest();
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.ACTIVE,200);
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.DELETED,200);
    }

    @Test
    public void can_update_organisation_status_from_pending_to_deleted_should_returns_status_200() {
        updateAndValidateOrganisation(createOrganisationRequest(),OrganisationStatus.DELETED,200);
    }

    @Test
    public void can_update_organisation_status_from_pending_to_pending_should_returns_status_200() {
        String organisationIdentifier = createOrganisationRequest();
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.PENDING,200);
    }

    @Test
    public void can_update_organisation_status_from_active_to_active_should_returns_status_200() {
        String organisationIdentifier = createOrganisationRequest();
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.ACTIVE,200);
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.ACTIVE,200);
    }

    @Test
    public void can_update_organisation_status_from_blocked_to_blocked_should_returns_status_200() {
        String organisationIdentifier = createOrganisationRequest();
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.BLOCKED,200);
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.BLOCKED,200);
    }

    @Test
    public void can_not_update_organisation_status_from_deleted_to_active_should_returns_status_400() {
        String organisationIdentifier = createOrganisationRequest();
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.DELETED,200);
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.ACTIVE,400);
    }

    @Test
    public void can_not_update_organisation_status_from_deleted_to_pending_should_returns_status_400() {
        String organisationIdentifier = createOrganisationRequest();
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.DELETED,200);
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.PENDING,400);
    }

    @Test
    public void can_not_update_organisation_status_from_deleted_to_blocked_should_returns_status_400() {
        String organisationIdentifier = createOrganisationRequest();
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.DELETED,200);
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.BLOCKED,400);
    }

    @Test
    public void can_not_update_organisation_status_from_deleted_to_deleted_should_returns_status_400() {
        String organisationIdentifier = createOrganisationRequest();
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.DELETED,200);
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.DELETED,400);
    }

    @Test
    public void can_not_update_organisation_status_from_active_to_pending_should_returns_status_400() {
        String organisationIdentifier = createOrganisationRequest();
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.ACTIVE,200);
        updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.PENDING,400);
    }

    @Test
    public void entities_other_than_organisation_should_remain_unchanged_if_updated_and_should_returns_status_200() {
        String organisationIdentifier = createOrganisationRequest();
        Organisation persistedOrganisation = updateAndValidateOrganisation(organisationIdentifier,OrganisationStatus.ACTIVE,200);

        List<PaymentAccount> pbaAccounts = persistedOrganisation.getPaymentAccounts();
        PaymentAccount paymentAccount = pbaAccounts.get(0);
        assertThat(paymentAccount.getPbaNumber()).isEqualTo("pbaNumber-1");

        List<ProfessionalUser> professionalUsers = persistedOrganisation.getUsers();
        ProfessionalUser professionalUser = professionalUsers.get(0);
        assertThat(professionalUser.getFirstName()).isEqualTo("some-fname");
        assertThat(professionalUser.getLastName()).isEqualTo("some-lname");
        assertThat(professionalUser.getEmailAddress()).isEqualTo("someone@somewhere.com");

        List<ContactInformation> contactInformations = persistedOrganisation.getContactInformations();
        ContactInformation contactInformation = contactInformations.get(0);
        assertThat(contactInformation.getAddressLine1()).isEqualTo("addressLine1");
        assertThat(contactInformation.getAddressLine2()).isEqualTo("addressLine2");
        assertThat(contactInformation.getAddressLine3()).isEqualTo("addressLine3");
        assertThat(contactInformation.getTownCity()).isEqualTo("town-city");
        assertThat(contactInformation.getCounty()).isEqualTo("county");
        assertThat(contactInformation.getCountry()).isEqualTo("country");
        assertThat(contactInformation.getPostCode()).isEqualTo("some-post-code");
    }

    @Test
    public void fields_other_than_organisation_should_override_if_same_and_should_returns_status_200() {
        String organisationIdentifier = createOrganisationRequest();
        Organisation persistedOrganisation = updateAndValidateOrganisation(organisationIdentifier, OrganisationStatus.ACTIVE, 200);
    }

    @Test
    public void can_not_update_entities_other_than_organisation_should_returns_status_200() {
        String organisationIdentifier = createOrganisationRequest();
        OrganisationCreationRequest organisationUpdateRequest = organisationRequestWithAllFieldsAreUpdated()
                .status(OrganisationStatus.ACTIVE)
                .name("some-org-name")
                .sraId("sra-id")
                .sraRegulated(Boolean.FALSE)
                .companyUrl("company-url")
                .companyNumber("company")
                .build();
        Map<String, Object> responseForOrganisationUpdate =
                professionalReferenceDataClient.updateOrganisation(organisationUpdateRequest, organisationIdentifier);

        Organisation persistedOrganisation = organisationRepository
                .findByOrganisationIdentifier(UUID.fromString(organisationIdentifier));

        assertThat(persistedOrganisation.getName()).isEqualTo("some-org-name");
        assertThat(persistedOrganisation.getStatus()).isEqualTo(OrganisationStatus.ACTIVE);
        assertThat(persistedOrganisation.getSraId()).isEqualTo("sra-id");
        assertThat(persistedOrganisation.getSraRegulated()).isEqualTo(Boolean.FALSE);
        assertThat(persistedOrganisation.getCompanyUrl()).isEqualTo("company-url");
        assertThat(persistedOrganisation.getCompanyNumber()).isEqualTo("company");
    }

    public String createOrganisationRequest() {
        OrganisationCreationRequest organisationCreationRequest = organisationRequestWithAllFields().build();
        Map<String, Object> responseForOrganisationCreation = professionalReferenceDataClient.createOrganisation(organisationCreationRequest);
        return (String) responseForOrganisationCreation.get("organisationIdentifier");
    }

    public Organisation updateAndValidateOrganisation(String organisationIdentifier, OrganisationStatus status, Integer httpStatus) {
        Organisation persistedOrganisation = null;
        OrganisationCreationRequest organisationUpdateRequest = organisationRequestWithAllFieldsAreUpdated().status(status).build();

        Map<String, Object> responseForOrganisationUpdate =
                professionalReferenceDataClient.updateOrganisation(organisationUpdateRequest, organisationIdentifier);

        if (httpStatus == 200) {
            persistedOrganisation = organisationRepository
                    .findByOrganisationIdentifier(UUID.fromString(organisationIdentifier));

            assertThat(persistedOrganisation.getName()).isEqualTo("some-org-name1");
            assertThat(persistedOrganisation.getStatus()).isEqualTo(status);
            assertThat(persistedOrganisation.getSraId()).isEqualTo("sra-id1");
            assertThat(persistedOrganisation.getSraRegulated()).isEqualTo(Boolean.TRUE);
            assertThat(persistedOrganisation.getCompanyUrl()).isEqualTo("company-url1");
            assertThat(responseForOrganisationUpdate.get("http_status")).isEqualTo(httpStatus);
        } else {
            if (responseForOrganisationUpdate.get("http_status") instanceof String) {
                assertThat(responseForOrganisationUpdate.get("http_status")).isEqualTo(String.valueOf(httpStatus.intValue()));
            } else {
                assertThat(responseForOrganisationUpdate.get("http_status")).isEqualTo(httpStatus);
            }
        }
        return persistedOrganisation;
    }
}