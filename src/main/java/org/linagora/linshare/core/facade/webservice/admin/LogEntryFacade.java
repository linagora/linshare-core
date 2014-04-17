package org.linagora.linshare.core.facade.webservice.admin;

import java.util.List;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.webservice.dto.LogCriteriaDto;
import org.linagora.linshare.webservice.dto.LogDto;

public interface LogEntryFacade extends AdminGenericFacade {
	List<LogDto> query(User actor, LogCriteriaDto criteria);
}
