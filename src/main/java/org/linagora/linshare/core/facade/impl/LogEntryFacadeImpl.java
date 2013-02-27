/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.transformers.impl.DisplayableLogEntryTransformer;
import org.linagora.linshare.core.domain.transformers.impl.LogEntryTransformer;
import org.linagora.linshare.core.domain.vo.DisplayableLogEntryVo;
import org.linagora.linshare.core.domain.vo.LogEntryVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.facade.LogEntryFacade;
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
