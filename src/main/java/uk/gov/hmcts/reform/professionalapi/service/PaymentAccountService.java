package uk.gov.hmcts.reform.professionalapi.service;

import uk.gov.hmcts.reform.professionalapi.domain.Organisation;

public interface PaymentAccountService {

    Organisation findPaymentAccountsByEmail(String email);
}
