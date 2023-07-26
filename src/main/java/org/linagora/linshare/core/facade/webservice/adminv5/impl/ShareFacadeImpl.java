package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.business.service.DomainBusinessService;
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

    public ShareFacadeImpl(AccountService accountService, ShareEntryService shareService, DomainBusinessService abstractDomainService) {
        super(accountService);
        this.shareService = shareService;
        this.abstractDomainService = abstractDomainService;
    }

    @Override
    public List<ShareRecipientStatisticDto> getTopSharesByFileSize(String domainUuid, String beginDate, String endDate) {
        Account authUser = checkAuthentication(Role.ADMIN);
        if (!StringUtils.isBlank(domainUuid)){
            AbstractDomain domain = abstractDomainService.findById(domainUuid);
            if (domain == null){
                throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST, "Cannot find domain with uuid : " + domainUuid);
            } else if (!domain.isManagedBy(authUser)){
                throw new BusinessException(BusinessErrorCode.DOMAIN_FORBIDDEN, "You are not allowed to manage this domain");
            }
        }
        return shareService.getTopSharesByFileSize(domainUuid, beginDate, endDate).stream().map(ShareRecipientStatisticDto.toDto()).collect(Collectors.toList());
    }
}
