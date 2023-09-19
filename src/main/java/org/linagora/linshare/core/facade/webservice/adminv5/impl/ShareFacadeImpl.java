package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.ShareFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ShareRecipientStatisticDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ShareEntryService;

public class ShareFacadeImpl extends AdminGenericFacadeImpl implements ShareFacade {

    ShareEntryService shareService;

    DomainBusinessService abstractDomainService;

    DomainPermissionBusinessService domainPermissionBusinessService;

    public ShareFacadeImpl(AccountService accountService, ShareEntryService shareService,
                           DomainBusinessService abstractDomainService, DomainPermissionBusinessService domainPermissionBusinessService) {
        super(accountService);
        this.shareService = shareService;
        this.abstractDomainService = abstractDomainService;
        this.domainPermissionBusinessService = domainPermissionBusinessService;
    }

    @Override
    public List<ShareRecipientStatisticDto> getTopSharesByFileSize(List<String> domainUuids, String beginDate, String endDate) {
        Account authUser = checkAuthentication(Role.ADMIN);
        List<String> allowedDomains = filterAllowedDomains(domainUuids, authUser);
        boolean addAnonymousShares = domainUuids == null || domainUuids.isEmpty();
        return shareService.getTopSharesByFileSize(allowedDomains, beginDate, endDate, addAnonymousShares).stream()
                .map(ShareRecipientStatisticDto.toDto()).collect(Collectors.toList());
    }

    @Override
    public List<ShareRecipientStatisticDto> getTopSharesByFileCount(List<String> domainUuids, String beginDate, String endDate) {
        Account authUser = checkAuthentication(Role.ADMIN);
        List<String> allowedDomains = filterAllowedDomains(domainUuids, authUser);
        boolean addAnonymousShares = domainUuids == null || domainUuids.isEmpty();
        return shareService.getTopSharesByFileCount(allowedDomains, beginDate, endDate, addAnonymousShares).stream()
                .map(ShareRecipientStatisticDto.toDto()).collect(Collectors.toList());
    }

    private List<String> filterAllowedDomains(List<String> domainUuids, Account authUser) {
        if (domainUuids == null || domainUuids.isEmpty()){
            List<String> allowedDomains = domainPermissionBusinessService.getMyAdministratedDomains(authUser)
                    .stream().map(AbstractDomain::getUuid).collect(Collectors.toList());
            if  (allowedDomains.isEmpty()) {
                throw new BusinessException(BusinessErrorCode.DOMAIN_FORBIDDEN, "You are not allowed to manage any domain");
            }
            return allowedDomains;
        } else {
            domainUuids.forEach(domain -> domainCheck(domain, authUser));
            return domainUuids;
        }
    }

    private void domainCheck(String domainUuid, Account authUser) {
        if (!StringUtils.isBlank(domainUuid)) {
            AbstractDomain domain = abstractDomainService.findById(domainUuid);
            if (domain == null) {
                throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST, "Cannot find domain with uuid : " + domainUuid);
            } else if (!domain.isManagedBy(authUser)) {
                throw new BusinessException(BusinessErrorCode.DOMAIN_FORBIDDEN, "You are not allowed to manage this domain : " + domain.getLabel());
            }
        }
    }
}
