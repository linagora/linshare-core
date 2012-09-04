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
package org.linagora.linshare.core.repository;


import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;

public interface AccountRepository<U extends Account> extends AbstractRepository<U> {

	
    U findByLsUuid(String lsUuid);
    
    boolean exist(String lsUuid);

	List<U> findByDomain(String domain);

	Account getSystemAccount();
    
} 
