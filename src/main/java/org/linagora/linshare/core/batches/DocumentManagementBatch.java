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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.linagora.linshare.core.batches;


public interface DocumentManagementBatch {

    /** 
     * Check that documents in database are also in jackrabbit repository.
     * If the document is not present in jackrabbit repository, we must delete it in database.
     */
    public void removeMissingDocuments();
    
    
    /**
     * Delete old documents when strong box disallowed
     */
    public void cleanOldDocuments();
}
