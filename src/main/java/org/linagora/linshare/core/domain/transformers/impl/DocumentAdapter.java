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
package org.linagora.linshare.core.domain.transformers.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;

public class DocumentAdapter {

	
	public DocumentVo disassemble(Document doc) {
		if(null!=doc){
			
			return new DocumentVo(doc.getUuid(),doc.getName(), doc.getFileComment(), doc.getCreationDate(),doc.getExpirationDate(),
					doc.getType(), doc.getOwner().getLogin(), doc.getEncrypted(),
					doc.getShared(),doc.getSize());
		}else{	
			return null;
		}
	}
	
	
	public List<DocumentVo> disassembleDocList(List<Document> docs) {
		List<DocumentVo> documents=new ArrayList<DocumentVo>();
		for(Document document :docs){
			documents.add(disassemble(document));
		}
		return documents;
	}
	
	
}
