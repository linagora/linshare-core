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
package org.linagora.linshare.repository.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.TransientObjectException;
import org.junit.Test;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractRepository;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/** Abstract class that test CRUD methods.
 *
 * @param T entity type.
 */
public abstract class AbstractRepositoryImplTest<T> extends AbstractTransactionalJUnit4SpringContextTests {

    private T entity;

    @Test
    public void testCreate() throws Exception {
        entity = getCompleteEntity();
        int count = jdbcTemplate.queryForObject(getSqlQueryForEntityDataCheck(), Integer.class);
        assertEquals("There is already a same entity in database", 0, count);

        T result = getAbstractRepository().create(entity);
        SessionFactoryUtils.getSession(getSessionFactory(), false).flush();
        assertEquals(entity, result);

        count = jdbcTemplate.queryForObject(getSqlQueryForEntityDataCheck(), Integer.class);
        assertEquals("The entity should now exist in database", 1, count);
    }

    @Test
    public void testCreateSameName() throws Exception {
        prepareDataSet();
        try {
            getAbstractRepository().create(getCompleteEntity());
        } catch (BusinessException e) {
            assertEquals(BusinessErrorCode.UNKNOWN, e.getErrorCode());
            return;
        }
        fail("should have thrown an exception");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateIllegalArgument() throws Exception {
        getAbstractRepository().create(null);
    }

    @Test
    public void testLoad() throws Exception {
        prepareDataSet();
        int count = jdbcTemplate.queryForObject(getSqlQueryForEntityDataCheck(), Integer.class);
        assertEquals(1, count);
        entity = getCompleteEntity();
        T result = getAbstractRepository().load(entity);
        assertEquals(entity, result);
    }

    @Test(expected=TransientObjectException.class)
    public void testLoadTransient() throws Exception {
        getAbstractRepository().load(getCompleteEntity());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLoadIllegalArgument() throws Exception {
        getAbstractRepository().load(null);
    }

    @Test
    public void testDelete() throws Exception {
        prepareDataSet();
        int count = jdbcTemplate.queryForObject(getSqlQueryForEntityDataCheck(), Integer.class);
        assertTrue(count == 1);
        entity = getCompleteEntity();
        getAbstractRepository().delete(entity);
        SessionFactoryUtils.getSession(getSessionFactory(), false).flush();
        count = jdbcTemplate.queryForObject(getSqlQueryForEntityDataCheck(), Integer.class);
        assertEquals(0, count);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeleteIllegalArgument() throws Exception {
        getAbstractRepository().delete(null);
    }

    @Test
    public void testUpdate() throws Exception {
        prepareDataSet();
        int count = jdbcTemplate.queryForObject(getSqlQueryForEntityDataCheck(), Integer.class);
        assertEquals(1, count);
        T loadedEntity = getAbstractRepository().load(getCompleteEntity());
        T updatedEntity = updateEntityForTest(loadedEntity);

        getAbstractRepository().update(updatedEntity);

        SessionFactoryUtils.getSession(getSessionFactory(), false).flush();
        count = jdbcTemplate.queryForObject(getSqlQueryForUpdatedEntityCheck(), Integer.class);
        assertEquals(1, count);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUpdateIllegalArgument() throws Exception {
        T nullEntity = null;
        getAbstractRepository().update(nullEntity);
    }

    @Test
    public void testFindAll() {
        prepareDataSet();
        int count = jdbcTemplate.queryForObject(getSqlQueryForEntityDataCheck(), Integer.class);
        assertEquals(1, count);

        List<T> results = getAbstractRepository().findAll();

        assertEquals(1, results.size());
        assertEquals(getCompleteEntity(), results.get(0));
    }

    /** @return repository.  */
    protected abstract AbstractRepository<T> getAbstractRepository();

    /** @return session factory. */
    protected abstract SessionFactory getSessionFactory();

    /** @return Entity with all data filled. */
    protected abstract T getCompleteEntity();

    /** @return sql query for entity data check. */
    protected abstract String getSqlQueryForEntityDataCheck();

    /** Prepare Data set for tests. */
    protected abstract void prepareDataSet();

    /** Update entity for update test.
     * @return updated entity.
     */
    protected abstract T updateEntityForTest(T entity);

    /** @return sql query for updated entity data check. */
    protected abstract String getSqlQueryForUpdatedEntityCheck();
}

