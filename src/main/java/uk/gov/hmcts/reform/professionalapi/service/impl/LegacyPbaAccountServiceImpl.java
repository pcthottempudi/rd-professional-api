package uk.gov.hmcts.reform.professionalapi.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import uk.gov.hmcts.reform.professionalapi.domain.PaymentAccount;
import uk.gov.hmcts.reform.professionalapi.domain.ProfessionalUser;
import uk.gov.hmcts.reform.professionalapi.domain.UserAccountMap;
import uk.gov.hmcts.reform.professionalapi.service.LegacyPbaAccountService;

@Service
@Slf4j
public class LegacyPbaAccountServiceImpl implements LegacyPbaAccountService {

    List<String> paymentAccountPbaNumbers = new ArrayList<>();

    public List<String> findLegacyPbaAccountByUserEmail(ProfessionalUser professionalUser) {

        List<String> pbaNumbers = null;
        List<PaymentAccount>  paymentAccountsFromEntity = new ArrayList<>();

        if (professionalUser.getOrganisation().getPaymentAccounts().isEmpty()) {

            log.debug("No Payments associated with the user::");
            pbaNumbers = new ArrayList<>();

        } else {

            paymentAccountsFromEntity = professionalUser.getOrganisation().getPaymentAccounts();
            log.info("payments size:::" + paymentAccountsFromEntity.size());

            List<PaymentAccount>  userMapPaymentAccount = getPaymentAccountsFromUserAccountMap(professionalUser.getUserAccountMap());

            pbaNumbers = getPbaNumbersFromPaymentAccount(userMapPaymentAccount, paymentAccountsFromEntity);

        }

        return pbaNumbers;
    }

    private List<String> getPbaNumbersFromPaymentAccount(List<PaymentAccount> userMapPaymentAccount, List<PaymentAccount> paymentAccountsEntity) {

        if (!paymentAccountsEntity.isEmpty()) {

            paymentAccountsEntity.forEach(paymentAccount -> {
                for (PaymentAccount usrMapAccount : userMapPaymentAccount) {
                    if (usrMapAccount.getId().equals(paymentAccount.getId())) {

                        paymentAccountPbaNumbers.add(paymentAccount.getPbaNumber());
                    }
                }
            });
        }
        return paymentAccountPbaNumbers;
    }

    private List<PaymentAccount> getPaymentAccountsFromUserAccountMap(List<UserAccountMap> userAccountMaps) {

        List<PaymentAccount> userMapPaymentAccount = new ArrayList<>();

        userMapPaymentAccount = userAccountMaps.stream().map(
            userAccountMap -> userAccountMap.getUserAccountMapId().getPaymentAccount())
                .collect(toList());

        return userMapPaymentAccount;
    }
}
