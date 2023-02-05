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
package org.linagora.linshare.core.repository;



import java.util.List;

import org.linagora.linshare.core.domain.entities.Document;


/**
 * Basic repository to deal with documents
 * @author ncharles
 *
 */
public interface DocumentRepository extends AbstractRepository<Document>{
	
	 /** Find a document using its uuid.
     * @param identifier
     * @return Document found document (null if no document found).
     */
	Document findByUuid(String identifier);

	/**
	 * We should only have one document, but with old versions of LinShare we
	 * could have some duplicated files/hashes.
	 *
	 * @param sha256sum
	 * @return List<Document>
	 */
	List<Document> findBySha256Sum(String sha256sum);

	List<String> findAllDocumentWithMimeTypeCheckEnabled();

	List<String> findAllIdentifiers();

	public List<String> findAllSha256CheckNeededDocuments();

	List<String> findAllDocumentsToUpgrade();

	List<String> findAllDocumentWithComputeThumbnailEnabled();

}
