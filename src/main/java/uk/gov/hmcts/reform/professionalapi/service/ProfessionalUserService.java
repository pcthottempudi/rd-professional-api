package uk.gov.hmcts.reform.professionalapi.service;

import java.util.List;
import java.util.UUID;

import uk.gov.hmcts.reform.professionalapi.controller.request.NewUserCreationRequest;
import uk.gov.hmcts.reform.professionalapi.controller.response.NewUserResponse;
import uk.gov.hmcts.reform.professionalapi.domain.Organisation;
import uk.gov.hmcts.reform.professionalapi.domain.ProfessionalUser;

public interface ProfessionalUserService {

    NewUserResponse addNewUserToAnOrganisation(NewUserCreationRequest newUserCreationRequest, UUID organisationIdentifier);

    ProfessionalUser findProfessionalUserByEmailAddress(String email);

    List<ProfessionalUser> findProfessionalUsersByOrganisation(Organisation existingOrganisation, boolean showDeleted);
}

