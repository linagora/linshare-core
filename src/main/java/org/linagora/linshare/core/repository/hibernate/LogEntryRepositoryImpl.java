/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
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
package org.linagora.linshare.core.repository.hibernate;

import java.util.Calendar;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.repository.LogEntryRepository;
import org.linagora.linshare.view.tapestry.beans.LogCriteriaBean;
import org.linagora.linshare.view.tapestry.enums.CriterionMatchMode;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class LogEntryRepositoryImpl extends AbstractRepositoryImpl<LogEntry> implements
		LogEntryRepository {

    public LogEntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
        super(hibernateTemplate);
    }
    
	protected DetachedCriteria getNaturalKeyCriteria(LogEntry entity) {
		DetachedCriteria det = DetachedCriteria.forClass(LogEntry.class).add(Restrictions.eq("id", entity.getPersistenceId()));
        return det;
	}
	
	public List<LogEntry> findByUser(String mail) {
		List<LogEntry> logEntry = findByCriteria(Restrictions.eq("actorMail", mail));
		logEntry.addAll(findByCriteria(Restrictions.eq("targetMail", mail)));
		return logEntry;
	}
	
	@SuppressWarnings("unchecked")
	public List<LogEntry> findByDate(String mail, Calendar beginDate,
			Calendar endDate) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(LogEntry.class);
		
		criteria.add(Restrictions.eq("actorMail", mail));
		
		if (beginDate != null) {
			criteria.add(Restrictions.gt("actionDate", beginDate));
		}
		
		if (endDate != null) {
			criteria.add(Restrictions.lt("actionDate", endDate));
		}
		
		return getHibernateTemplate().findByCriteria(criteria);
	}

	
	
	public List<LogEntry> findByCriteria(LogCriteriaBean logCriteria, String domainId) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(LogEntry.class);
		
		
		if ((logCriteria.getActorMails()!=null) && (logCriteria.getActorMails().size()>0)) {
			Disjunction or = Restrictions.disjunction();
			for (String mail : logCriteria.getActorMails()) {
				or.add(Restrictions.like("actorMail", mail, MatchMode.ANYWHERE));
			}
			criteria.add(or);
		}
		
		if ((logCriteria.getTargetMails()!=null) && (logCriteria.getTargetMails().size()>0)) {
			Disjunction or = Restrictions.disjunction();
			for (String mail : logCriteria.getTargetMails()) {
				or.add(Restrictions.like("targetMail", mail, MatchMode.ANYWHERE));
			}
			criteria.add(or);
		}
		
		if ((logCriteria.getActorFirstname()!=null) && (logCriteria.getActorFirstname().length()>0)) {
			criteria.add(Restrictions.like("actorFirstname", logCriteria.getActorFirstname(), MatchMode.START).ignoreCase());
		}
		
		if ((logCriteria.getActorLastname()!=null) && (logCriteria.getActorLastname().length()>0)) {
			criteria.add(Restrictions.like("actorLastname", logCriteria.getActorLastname(), MatchMode.START).ignoreCase());
		}
		
		if (domainId != null && domainId.length() > 0) {
			criteria.add(Restrictions.like("actorDomain", domainId));
		} else if (logCriteria.getActorDomain() != null && logCriteria.getActorDomain().length() > 0) {
			criteria.add(Restrictions.like("actorDomain", logCriteria.getActorDomain()));
		}
		
		if ((logCriteria.getTargetFirstname()!=null) && (logCriteria.getTargetFirstname().length()>0)) {
			criteria.add(Restrictions.like("targetFirstname", logCriteria.getTargetFirstname(), MatchMode.START).ignoreCase());
		}
		
		if ((logCriteria.getTargetLastname()!=null) && (logCriteria.getTargetLastname().length()>0)) {
			criteria.add(Restrictions.like("targetLastname", logCriteria.getTargetLastname(), MatchMode.START).ignoreCase());
		}
		
		if (logCriteria.getTargetDomain() != null && logCriteria.getTargetDomain().length() > 0) {
			criteria.add(Restrictions.like("targetDomain", logCriteria.getTargetDomain()));
		}
		
		if ((logCriteria.getLogActions()!=null) && (logCriteria.getLogActions().size()>0)) {
			criteria.add(Restrictions.in("logAction", logCriteria.getLogActions()));
		}
		if (logCriteria.getBeforeDate() != null) {
			criteria.add(Restrictions.gt("actionDate", logCriteria.getBeforeDate()));
		}
		
		if (logCriteria.getAfterDate() != null) {
			criteria.add(Restrictions.lt("actionDate", logCriteria.getAfterDate()));
		}
		
		if (logCriteria.getFileName() != null) {
			
			if(logCriteria.getFileNameMatchMode().equals(CriterionMatchMode.START)){
				criteria.add(Restrictions.like("fileName", logCriteria.getFileName(), MatchMode.START).ignoreCase());
			} else {
				criteria.add(Restrictions.like("fileName", logCriteria.getFileName(), MatchMode.ANYWHERE).ignoreCase());
			}
		}
		
		if (logCriteria.getFileExtension() != null) {
			criteria.add(Restrictions.like("fileName", logCriteria.getFileExtension(), MatchMode.END).ignoreCase());
		}
		
		criteria.addOrder(Order.desc("actionDate"));
		
		return getHibernateTemplate().findByCriteria(criteria);
	}



	
}
