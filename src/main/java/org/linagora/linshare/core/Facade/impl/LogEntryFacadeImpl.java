/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.Facade.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.Facade.LogEntryFacade;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.transformers.impl.DisplayableLogEntryTransformer;
import org.linagora.linshare.core.domain.transformers.impl.LogEntryTransformer;
import org.linagora.linshare.core.domain.vo.DisplayableLogEntryVo;
import org.linagora.linshare.core.domain.vo.LogEntryVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.repository.LogEntryRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.view.tapestry.beans.LogCriteriaBean;

public class LogEntryFacadeImpl implements LogEntryFacade {

	private final LogEntryRepository logEntryRepository;
	
	private final LogEntryTransformer logEntryTransformer;
	
	private final DisplayableLogEntryTransformer displayableLogEntryTransformer;
	
	private final AbstractDomainService abstractDomainService;
	
	
	public LogEntryFacadeImpl(final LogEntryRepository logEntryRepository,
			final LogEntryTransformer logEntryTransformer,
			final DisplayableLogEntryTransformer displayableLogEntryTransformer, AbstractDomainService abstractDomainService) {
		this.logEntryRepository = logEntryRepository;
		this.logEntryTransformer = logEntryTransformer;
		this.displayableLogEntryTransformer = displayableLogEntryTransformer;
		this.abstractDomainService = abstractDomainService;
	}

	public List<LogEntryVo> findByDate(String mail, Calendar beginDate,
			Calendar endDate) {
		return logEntryTransformer.disassembleList(logEntryRepository.findByDate(mail, beginDate, endDate));
	}

	public List<LogEntryVo> findByUser(String mail) {
		return logEntryTransformer.disassembleList(logEntryRepository.findByUser(mail));
	}

	public List<DisplayableLogEntryVo> findByCriteria(LogCriteriaBean criteria, UserVo actorVo) {
		List<LogEntry> list = new ArrayList<LogEntry>();
		
		List<String> allMyDomainIdentifiers = abstractDomainService.getAllMyDomainIdentifiers(actorVo.getDomainIdentifier());
		for (String domainIdentifier: allMyDomainIdentifiers) {
			list.addAll(logEntryRepository.findByCriteria(criteria, domainIdentifier));
		}
		
		return displayableLogEntryTransformer.disassembleList(list);
	}

}
