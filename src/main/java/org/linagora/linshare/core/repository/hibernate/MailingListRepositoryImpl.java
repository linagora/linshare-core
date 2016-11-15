/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class MailingListRepositoryImpl extends AbstractRepositoryImpl<MailingList> implements MailingListRepository {

	public MailingListRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailingList entity) {
		return DetachedCriteria.forClass(getPersistentClass()).add(Restrictions.eq("uuid", entity.getUuid()));
	}

	@Override
	public MailingList findByUuid(String uuid) {
		List<MailingList> mailingList = findByCriteria(Restrictions.eq("uuid", uuid));

		if (mailingList == null || mailingList.isEmpty()) {
			return null;
		} else if (mailingList.size() == 1) {
			return mailingList.get(0);
		} else {
			throw new IllegalStateException("Uuid must be unique");
		}
	}

	@Override
	public MailingList findByIdentifier(User owner, String identifier) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());

		det.add(Restrictions.and(Restrictions.eq("identifier", identifier), Restrictions.eq("owner", owner)));
		List<MailingList> mailingList = findByCriteria(det);

		if (mailingList == null || mailingList.isEmpty()) {
			return null;
		} else if (mailingList.size() == 1) {
			return mailingList.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	public List<MailingList> findAllListWhereOwner(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("owner", user));
		return findByCriteria(det);
	}

	@Override
	public List<MailingList> searchMyListWithInput(User user, String input) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.and(Restrictions.eq("owner", user), Restrictions.like("identifier", "%" + input + "%")
				.ignoreCase()));
		det.addOrder(Property.forName("identifier").desc());
		return findByCriteria(det);
	}

	@Override
	public List<MailingList> searchListWithInput(User user, String input) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());

		if (user.hasSuperAdminRole()) {
			det.add(Restrictions.like("identifier", "%" + input + "%").ignoreCase());
		} else {
			// all public lists that belong to my domain.
			LogicalExpression allPublicLists = Restrictions.and(Restrictions.eq("isPublic", true),
					Restrictions.eq("domain", user.getDomain()));
			// we exclude my personal lists.
			LogicalExpression allMyDomainPublicLists = Restrictions.and(allPublicLists, Restrictions.ne("owner", user));
			// adding all private and public lists that belong to me, to the
			// public
			// lists.

			LogicalExpression allMyLists = Restrictions.or(Restrictions.eq("owner", user), allMyDomainPublicLists);
			det.add(Restrictions.and(allMyLists, Restrictions.like("identifier", "%" + input + "%").ignoreCase()));
		}
		det.addOrder(Property.forName("identifier").desc());

		return findByCriteria(det);
	}

	@Override
	public List<MailingList> findAll(User user) {
		// all public lists owned by others people than current user.
		Conjunction public_others = Restrictions.conjunction();
		public_others.add(Restrictions.eq("domain", user.getDomain()));
		public_others.add(Restrictions.eq("isPublic", true));
		public_others.add(Restrictions.ne("owner", user));

		// all private lists owned by the current user.
		Conjunction private_mine = Restrictions.conjunction();
		private_mine.add(Restrictions.eq("domain", user.getDomain()));
		private_mine.add(Restrictions.eq("owner", user));

		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.or(public_others, private_mine));
		return findByCriteria(det);
	}

	@Override
	public List<MailingList> findAllMine(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("domain", user.getDomain()));
		det.add(Restrictions.eq("owner", user));
		return findByCriteria(det);
	}

	@Override
	public List<MailingList> findAllOthers(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("domain", user.getDomain()));
		det.add(Restrictions.eq("isPublic", true));
		det.add(Restrictions.ne("owner", user));
		return findByCriteria(det);
	}

	@Override
	public List<MailingList> findAllMyList(User user) {
		if (user.hasSuperAdminRole()) {
			return findAll();
		}
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());

		// all public lists that belong to my domain.
		LogicalExpression allPublicLists = Restrictions.and(Restrictions.eq("isPublic", true),
				Restrictions.eq("domain", user.getDomain()));
		// we exclude my personal lists.
		LogicalExpression allMyDomainPublicLists = Restrictions.and(allPublicLists, Restrictions.ne("owner", user));
		// adding all private and public lists that belong to me, to the public
		// lists.
		det.add(Restrictions.or(Restrictions.eq("owner", user), allMyDomainPublicLists));

		return findByCriteria(det);
	}

	@Override
	public MailingList update(MailingList entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public MailingList create(MailingList entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public List<MailingList> searchWithInputByVisibility(User user, boolean isPublic, String input) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());

		if (isPublic == false) {
			if (user.hasSuperAdminRole()) {
				det.add(Restrictions.and(Restrictions.like("identifier", "%" + input + "%").ignoreCase(),
						Restrictions.eq("isPublic", false)));
			} else {
				LogicalExpression publicLists = Restrictions.and(Restrictions.eq("owner", user),
						Restrictions.eq("isPublic", false));
				det.add(Restrictions.and(Restrictions.like("identifier", "%" + input + "%").ignoreCase(), publicLists));
			}
		} else {
			if (user.hasSuperAdminRole()) {
				det.add(Restrictions.and(Restrictions.like("identifier", "%" + input + "%").ignoreCase(),
						Restrictions.eq("isPublic", true)));
			} else {
				LogicalExpression privateLists = Restrictions.and(Restrictions.eq("isPublic", true),
						Restrictions.eq("domain", user.getDomain()));
				det.add(Restrictions.and(Restrictions.like("identifier", "%" + input + "%").ignoreCase(), privateLists));
			}
		}
		det.addOrder(Property.forName("identifier").desc());
		return findByCriteria(det);
	}

	@Override
	public List<MailingList> searchListByVisibility(User user, boolean isPublic) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());

		if (isPublic == false) {
			if (user.hasSuperAdminRole()) {
				det.add(Restrictions.eq("isPublic", false));
			} else {
				det.add(Restrictions.and(Restrictions.eq("owner", user), Restrictions.eq("isPublic", false)));
			}
		} else {
			if (user.hasSuperAdminRole()) {
				det.add(Restrictions.eq("isPublic", true));
			} else {
				det.add(Restrictions.and(Restrictions.eq("isPublic", true), Restrictions.eq("domain", user.getDomain())));
			}
		}
		det.addOrder(Property.forName("identifier").desc());
		return findByCriteria(det);
	}

}
