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

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.TransientObjectException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/** This abstract repository provides common methods for repository management.
 *
 * @param T : entity type.
 */
public abstract class AbstractRepositoryImpl<T> implements AbstractRepository<T> {

    /** Logger. */
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Type of the persistence class. */
    private final Class<T> persistentClass;

    /** Hibernate template. */
    private final HibernateTemplate hibernateTemplate;

    /** Constructor.
     * @param hibernateTemplate the hibernate template.
     */
    @SuppressWarnings("unchecked")
	public AbstractRepositoryImpl(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    /** Persist the provided entiry.
     * @param entity the entity to persist.
     * @return persisted entity.
     * @throws org.linagora.linshare.core.BusinessException in case of failure.
     * @throws java.lang.IllegalArgumentException if entity is null.
     */
    public T create(T entity) throws BusinessException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        logger.debug("entity created:"+entity);
        // perform unicity check:
        checkUnicity(entity);

        hibernateTemplate.save(entity);
        return entity;
    }

    /** Attach the entity to the session context.
     * @param entity the entity to load.
     * @return attached entity.
     * @throws TransientObjectException if the entity is transient.
     * @throws java.lang.IllegalArgumentException if entity is null or if the type of the give entity doesn't match with
     * the type of the element retrieved in database..
     */
    public T load(T entity) throws BusinessException, IllegalArgumentException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null. ");
        }
        List<T> results = (List<T>) findByCriteria(getNaturalKeyCriteria(entity));
        if (results.size() == 0) {
            throw new TransientObjectException("The given entity is transient so it can't be loaded.");
        } else if (results.size() == 1) {
            return results.get(0);
        } else { // size > 1
            throw new IllegalStateException("There is a broken integrity constraint. Entity is not unique !");
        }
    }

    /** Update the provided entity.
     * @param entity the entity to update.
     * @return persisted entity.
     * @throws java.lang.IllegalArgumentException if entity is null.
     * @throws TransientObjectException if the entity is transient.
     */
    public T update(T entity) throws BusinessException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        // check that entity is not transient :
        load(entity);
        hibernateTemplate.update(entity);
        return entity;
    }

    /** Find all entities of this type.
     * @return result list.
     */
    public List<T> findAll() {
        return (List<T>) findByCriteria();
    }

    /** Delete the provided entity.
     * @param entity the entity to delete.
     * @throws TransientObjectException if the entity is transient.
     * @throws java.lang.IllegalArgumentException if entity is null.
     */
    public void delete(T entity) throws BusinessException, IllegalArgumentException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        T loadedEntity = load(entity);
        hibernateTemplate.delete(loadedEntity);
    }

    /** Check entity unicity.
     * This method must throw a business exception if the unicity constraint is violated.
     * @param entity entity to check.
     * @throws org.linagora.linshare.core.BusinessException
     */
    private void checkUnicity(T entity) throws BusinessException {
        if (findByCriteria(getNaturalKeyCriteria(entity)).size() > 0) {
            throw new BusinessException(BusinessErrorCode.UNKNOWN,
                    "This entity already exists in database:"+getNaturalKeyCriteria(entity));
        }
    }

    /** Get natural key criteria.
     * the provided criteria match the natural key unicity constraints.
     * @return natural key criteria.
     */
    protected abstract DetachedCriteria getNaturalKeyCriteria(T entity);

    /** Get hibernate template.
     * @return hibernate template.
     */
    protected HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    /** Get the identity of the specified entity.
     * @param entity the entity.
     * @return the identifier of the specified entity.
     * @throws TransientObjectException if the entity is transient.
     */
    protected Serializable getIdentifier(T entity) {
        return hibernateTemplate.getSessionFactory().getCurrentSession().getIdentifier(entity);
    }

    /** Find by criteria.
     * @param criterions search criterion(s).
     * @return search result.
     */
	protected List<T> findByCriteria(final Criterion... criterions) {
        DetachedCriteria criteria = DetachedCriteria.forClass(persistentClass);
        for (Criterion criterion : criterions) {
            criteria.add(criterion);
        }
        return (List<T>) hibernateTemplate.findByCriteria(criteria);
    }

    /** Find by criteria.
     * @param a detached criteria.
     * @return search result.
     */
	protected List<T> findByCriteria(final DetachedCriteria criteria) {
        return (List<T>) hibernateTemplate.findByCriteria(criteria);
    }
    
    /** Find by criteria.
     * @param a detached criteria.
     * @param limit the results
     * @return search result.
     */
	protected List<T> findByCriteria(final DetachedCriteria criteria, int limit) {
        return (List<T>) hibernateTemplate.findByCriteria(criteria, -1, limit);
    }
    
    /** Find by criteria.
     * @param a detached criteria.
     * @return search result.
     */
    @SuppressWarnings("rawtypes")
	protected List listByCriteria(final DetachedCriteria criteria) {
    	return hibernateTemplate.findByCriteria(criteria);
    }
    
    protected Class<T> getPersistentClass() {
    	return persistentClass;
    }
 
    /** Get current Hibernate session from SessionFactory
     * @return current session
     */
    protected Session getCurrentSession() {
    	return hibernateTemplate.getSessionFactory().getCurrentSession();
    }

}
