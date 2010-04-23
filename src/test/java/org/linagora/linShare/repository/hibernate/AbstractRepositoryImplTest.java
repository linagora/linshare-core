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
package org.linagora.linShare.repository.hibernate;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.TransientObjectException;
import org.junit.Test;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.AbstractRepository;
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
        int count = simpleJdbcTemplate.queryForInt(getSqlQueryForEntityDataCheck());
        assertEquals("There is already a same entity in database", 0, count);

        T result = getAbstractRepository().create(entity);
        SessionFactoryUtils.getSession(getSessionFactory(), false).flush();
        assertEquals(entity, result);

        count = simpleJdbcTemplate.queryForInt(getSqlQueryForEntityDataCheck());
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
        int count = simpleJdbcTemplate.queryForInt(getSqlQueryForEntityDataCheck());
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
        int count = simpleJdbcTemplate.queryForInt(getSqlQueryForEntityDataCheck());
        assertTrue(count == 1);
        entity = getCompleteEntity();
        getAbstractRepository().delete(entity);
        SessionFactoryUtils.getSession(getSessionFactory(), false).flush();
        count = simpleJdbcTemplate.queryForInt(getSqlQueryForEntityDataCheck());
        assertEquals(0, count);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeleteIllegalArgument() throws Exception {
        getAbstractRepository().delete(null);
    }

    @Test
    public void testUpdate() throws Exception {
        prepareDataSet();
        int count = simpleJdbcTemplate.queryForInt(getSqlQueryForEntityDataCheck());
        assertEquals(1, count);
        T loadedEntity = getAbstractRepository().load(getCompleteEntity());
        T updatedEntity = updateEntityForTest(loadedEntity);

        getAbstractRepository().update(updatedEntity);

        SessionFactoryUtils.getSession(getSessionFactory(), false).flush();
        count = simpleJdbcTemplate.queryForInt(getSqlQueryForUpdatedEntityCheck());
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
        int count = simpleJdbcTemplate.queryForInt(getSqlQueryForEntityDataCheck());
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

