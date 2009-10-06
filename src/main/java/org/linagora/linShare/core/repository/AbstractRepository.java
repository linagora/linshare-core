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

import java.util.List;

import org.linagora.linShare.core.exception.BusinessException;

/** This abstract repository provides common methods for repository management.
 *
 */
public interface AbstractRepository<T> {

    /** Persist the provided entiry.
     * @param entity the entity to persist.
     * @return persisted entity.
     * @throws org.linagora.linShare.core.exception.BusinessException in case of failure.
     * @throws java.lang.IllegalArgumentException if entity is null.
     */
    T create(T entity) throws BusinessException, IllegalArgumentException;

    /** Attach the entity to the session context.
     * @param entity the entity to load.
     * @return attached entity.
     * @throws org.linagora.linShare.core.exception.BusinessException in case of failure.
     * @throws java.lang.IllegalArgumentException if entity is null or if the type of the give entity doesn't match with
     * the type of the element retrieved in database..
     */
    T load(T entity) throws BusinessException, IllegalArgumentException;

    /** Update the provided entity.
     * @param entity the entity to update.
     * @return persisted entity.
     * @throws org.linagora.linShare.core.exception.BusinessException in case of failure.
     * @throws java.lang.IllegalArgumentException if entity is null.
     */
    T update(T entity) throws BusinessException, IllegalArgumentException;

    /** Find all entities of this type.
     * @return result list.
     */
    List<T> findAll();

    /** Delete the provided entity.
     * @param entity the entity to delete.
     * @throws org.linagora.linShare.core.exception.BusinessException in case of failure.
     * @throws java.lang.IllegalArgumentException if entity is null.
     */
    void delete(T entity) throws BusinessException, IllegalArgumentException;
}
