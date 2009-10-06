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
package org.linagora.linShare.core.repository;

import java.util.Calendar;
import java.util.List;
import org.linagora.linShare.core.domain.entities.LogEntry;
import org.linagora.linShare.view.tapestry.beans.LogCriteriaBean;

public interface LogEntryRepository extends AbstractRepository<LogEntry> {

	/**
	 * Return all the log entries of a specific user (actor or target)
	 * @param mail
	 * @return
	 */
	public List<LogEntry> findByUser(String mail);

	/**
	 * Return all the log entries of a specific user, between two date (one may be null)
	 * @param mail 
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List<LogEntry> findByDate(String mail, Calendar beginDate, Calendar endDate);
	
	
	public List<LogEntry> findByCriteria(LogCriteriaBean criteria);
	
}
