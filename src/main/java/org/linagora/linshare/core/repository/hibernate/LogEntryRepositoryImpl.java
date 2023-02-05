/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.repository.hibernate;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.repository.LogEntryRepository;
import org.linagora.linshare.view.tapestry.beans.LogCriteriaBean;
import org.linagora.linshare.view.tapestry.enums.CriterionMatchMode;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class LogEntryRepositoryImpl extends AbstractRepositoryImpl<LogEntry>
		implements LogEntryRepository {

	public LogEntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	protected DetachedCriteria getNaturalKeyCriteria(LogEntry entity) {
		DetachedCriteria det = DetachedCriteria.forClass(LogEntry.class).add(
				Restrictions.eq("id", entity.getPersistenceId()));
		return det;
	}

	@Override
	public List<LogEntry> findByCriteria(LogCriteriaBean logCriteria,
			String domainId) {

		DetachedCriteria criteria = DetachedCriteria.forClass(LogEntry.class);

		if (CollectionUtils.isNotEmpty(logCriteria.getActorMails())) {
			Disjunction or = Restrictions.disjunction();
			for (String mail : logCriteria.getActorMails()) {
				if (StringUtils.isNotBlank(mail))
					or.add(Restrictions.like("actorMail", mail,
							MatchMode.ANYWHERE));
			}
			criteria.add(or);
		}

		if (CollectionUtils.isNotEmpty(logCriteria.getTargetMails())) {
			Disjunction or = Restrictions.disjunction();
			for (String mail : logCriteria.getTargetMails()) {
				if (StringUtils.isNotBlank(mail))
					or.add(Restrictions.like("targetMail", mail,
							MatchMode.ANYWHERE));
			}
			criteria.add(or);
		}

		if (StringUtils.isNotBlank(logCriteria.getActorFirstname())) {
			criteria.add(Restrictions.like("actorFirstname",
					logCriteria.getActorFirstname(), MatchMode.ANYWHERE)
					.ignoreCase());
		}

		if (StringUtils.isNotBlank(logCriteria.getActorLastname())
				&& (logCriteria.getActorLastname().length() > 0)) {
			criteria.add(Restrictions.like("actorLastname",
					logCriteria.getActorLastname(), MatchMode.ANYWHERE)
					.ignoreCase());
		}

		if (StringUtils.isNotBlank(domainId)) {
			criteria.add(Restrictions.like("actorDomain", domainId));
		} else if (StringUtils.isNotBlank(logCriteria.getActorDomain())) {
			criteria.add(Restrictions.like("actorDomain",
					logCriteria.getActorDomain()));
		}

		if (StringUtils.isNotBlank(logCriteria.getTargetFirstname())) {
			criteria.add(Restrictions.like("targetFirstname",
					logCriteria.getTargetFirstname(), MatchMode.ANYWHERE)
					.ignoreCase());
		}

		if (StringUtils.isNotBlank(logCriteria.getTargetLastname())) {
			criteria.add(Restrictions.like("targetLastname",
					logCriteria.getTargetLastname(), MatchMode.ANYWHERE)
					.ignoreCase());
		}

		if (StringUtils.isNotBlank(logCriteria.getTargetDomain())) {
			criteria.add(Restrictions.like("targetDomain",
					logCriteria.getTargetDomain()));
		}

		if (CollectionUtils.isNotEmpty(logCriteria.getLogActions())) {
			criteria.add(Restrictions.in("logAction",
					logCriteria.getLogActions()));
		}
		if (logCriteria.getBeforeDate() != null) {
			criteria.add(Restrictions.gt("actionDate",
					logCriteria.getBeforeDate()));
		}

		if (logCriteria.getAfterDate() != null) {
			criteria.add(Restrictions.lt("actionDate",
					logCriteria.getAfterDate()));
		}

		if (StringUtils.isNotBlank(logCriteria.getFileName())) {

			if (logCriteria.getFileNameMatchMode().equals(
					CriterionMatchMode.ANYWHERE)) {
				criteria.add(Restrictions.like("fileName",
						logCriteria.getFileName(), MatchMode.ANYWHERE)
						.ignoreCase());
			} else if (logCriteria.getFileNameMatchMode().equals(
					CriterionMatchMode.START)){
				criteria.add(Restrictions.like("fileName",
						logCriteria.getFileName(), MatchMode.START)
						.ignoreCase());
			} else if (logCriteria.getFileNameMatchMode().equals(
					CriterionMatchMode.EXACT)){
				criteria.add(Restrictions.like("fileName",
						logCriteria.getFileName(), MatchMode.EXACT));
			} else {
				criteria.add(Restrictions.like("fileName",
						logCriteria.getFileName(), MatchMode.ANYWHERE)
						.ignoreCase());
			}
		}

		if (StringUtils.isNotBlank(logCriteria.getFileExtension())) {
			criteria.add(Restrictions.like("fileName",
					logCriteria.getFileExtension(), MatchMode.END).ignoreCase());
		}

		criteria.addOrder(Order.desc("actionDate"));

		return findBy(criteria);
	}

	private List<LogEntry> findBy(DetachedCriteria criteria) {
		return findByCriteria(criteria);
	}

}
