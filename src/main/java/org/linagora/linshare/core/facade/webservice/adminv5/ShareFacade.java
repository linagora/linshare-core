package org.linagora.linshare.core.facade.webservice.adminv5;

import java.util.List;

import org.linagora.linshare.core.facade.webservice.admin.AdminGenericFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ShareRecipientStatisticDto;

public interface ShareFacade extends AdminGenericFacade {

    List<ShareRecipientStatisticDto> getTopSharesByFileSize(String domainUuid, String beginDate, String endDate);
}
