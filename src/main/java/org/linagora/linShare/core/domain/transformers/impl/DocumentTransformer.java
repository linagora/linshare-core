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
package org.linagora.linShare.core.domain.transformers.impl;


import java.util.ArrayList;
import java.util.List;


import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.transformers.Transformer;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.repository.DocumentRepository;

public class DocumentTransformer implements Transformer<Document, DocumentVo>{

	private final DocumentRepository documentRepository;
	
	
	public DocumentTransformer(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	public Document assemble(DocumentVo valueObject) {
				
		if(null!=valueObject){
			
			Document document=documentRepository.findById(valueObject.getIdentifier());
			
			
			return document;
		}else{	
			return null;
		}
	}

	public List<Document> assembleList(List<DocumentVo> valueObjectList) {
		ArrayList<Document> documents=new ArrayList<Document>();
		for(DocumentVo documentVo :valueObjectList){
			documents.add(assemble(documentVo));
		}
		return documents;
	}

	public DocumentVo disassemble(Document entityObject) {
		
		
		if(null!=entityObject){
			
			return new DocumentVo(entityObject.getIdentifier(),entityObject.getName(), entityObject.getFileComment() ,entityObject.getCreationDate(),entityObject.getExpirationDate(),
					entityObject.getType(), entityObject.getOwner().getLogin(), entityObject.getEncrypted(),
					entityObject.getShared(),entityObject.getSharedWithGroup(),entityObject.getSize());
		}else{	
			return null;
		}
	}

	public List<DocumentVo> disassembleList(List<Document> entityObjectList) {
		ArrayList<DocumentVo> documents=new ArrayList<DocumentVo>();
		for(Document document :entityObjectList){
			documents.add(disassemble(document));
		}
		return documents;
	}

}
