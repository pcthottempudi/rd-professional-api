package uk.gov.hmcts.reform.professionalapi.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.meanbean.factories.FactoryCollection;
import org.meanbean.lang.Factory;
import org.meanbean.test.BeanTester;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrganisationTest extends AbstractEntityTest{

    @Test
    public void creates_organisation_correctly() {

        Organisation organisation = new Organisation("some-name", "some-status",
                "sra-id","company-number",Boolean.FALSE,"company-url");

        assertThat(organisation.getName()).isEqualTo("some-name");
        assertThat(organisation.getStatus()).isEqualTo("some-status");
        assertThat(organisation.getSraId()).isEqualTo("sra-id");
        assertThat(organisation.getCompanyNumber()).isEqualTo("company-number");
        assertThat(organisation.getSraRegulated()).isEqualTo(Boolean.FALSE);
        assertThat(organisation.getCompanyUrl()).isEqualTo("company-url");
        assertThat(organisation.getId()).isNull();              // hibernate generated
    }

    @Test
    public void adds_users_correctly() {

        ProfessionalUser professionalUser = mock(ProfessionalUser.class);

        Organisation organisation = new Organisation();
        organisation.addProfessionalUser(professionalUser);

        assertThat(organisation.getUsers())
                .containsExactly(professionalUser);
    }

    @Test
    public void adds_payment_account_correctly() {

        PaymentAccount paymentAccount = mock(PaymentAccount.class);

        Organisation organisation = new Organisation();
        organisation.addPaymentAccount(paymentAccount);

        assertThat(organisation.getPaymentAccounts())
                .containsExactly(paymentAccount);
    }

    @Override
    protected Organisation getBeanInstance() {
        return new Organisation();
    }
}