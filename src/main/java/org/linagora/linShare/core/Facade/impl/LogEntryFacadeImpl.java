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
package org.linagora.linShare.core.Facade.impl;

import java.util.Calendar;
import java.util.List;

import org.linagora.linShare.core.Facade.LogEntryFacade;
import org.linagora.linShare.core.domain.transformers.impl.DisplayableLogEntryTransformer;
import org.linagora.linShare.core.domain.transformers.impl.LogEntryTransformer;
import org.linagora.linShare.core.domain.vo.DisplayableLogEntryVo;
import org.linagora.linShare.core.domain.vo.LogEntryVo;
import org.linagora.linShare.core.repository.LogEntryRepository;
import org.linagora.linShare.view.tapestry.beans.LogCriteriaBean;

public class LogEntryFacadeImpl implements LogEntryFacade {

	private final LogEntryRepository logEntryRepository;
	
	private final LogEntryTransformer logEntryTransformer;
	
	private final DisplayableLogEntryTransformer displayableLogEntryTransformer;
	
	
	public LogEntryFacadeImpl(final LogEntryRepository logEntryRepository,
			final LogEntryTransformer logEntryTransformer,
			final DisplayableLogEntryTransformer displayableLogEntryTransformer) {
		this.logEntryRepository = logEntryRepository;
		this.logEntryTransformer = logEntryTransformer;
		this.displayableLogEntryTransformer = displayableLogEntryTransformer;
	}

	public List<LogEntryVo> findByDate(String mail, Calendar beginDate,
			Calendar endDate) {
		return logEntryTransformer.disassembleList(logEntryRepository.findByDate(mail, beginDate, endDate));
	}

	public List<LogEntryVo> findByUser(String mail) {
		return logEntryTransformer.disassembleList(logEntryRepository.findByUser(mail));
	}

	public List<DisplayableLogEntryVo> findByCriteria(LogCriteriaBean criteria) {
		return displayableLogEntryTransformer.disassembleList(logEntryRepository.findByCriteria(criteria));
	}

}
