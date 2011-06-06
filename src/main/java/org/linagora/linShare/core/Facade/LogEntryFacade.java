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
package org.linagora.linShare.core.Facade;

import java.util.Calendar;
import java.util.List;

import org.linagora.linShare.core.domain.vo.DisplayableLogEntryVo;
import org.linagora.linShare.core.domain.vo.LogEntryVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.view.tapestry.beans.LogCriteriaBean;

/**
 * Facade for reading log entry from the DB
 * We should not need to write log entry from outside of a facade; so we do not provide for this feature
 * @author ncharles
 *
 */
public interface LogEntryFacade {
	/**
	 * Return all the log entries of a specific user (actor or target)
	 * @param mail
	 * @return
	 */
	public List<LogEntryVo> findByUser(String mail);

	/**
	 * Return all the log entries of a specific user, between two date (one may be null)
	 * @param mail 
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List<LogEntryVo> findByDate(String mail, Calendar beginDate, Calendar endDate);
	
	
	/**
	 * Return all the log entries corresponding to the criteria
	 * @param criteria
	 * @param userLoggedIn the actor
	 * @return
	 */
	public List<DisplayableLogEntryVo> findByCriteria(LogCriteriaBean criteria, UserVo userLoggedIn);
}
