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
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;

public class DocumentAdapter {

	
	public DocumentVo disassemble(Document doc) {
		if(null!=doc){
			
			return new DocumentVo(doc.getIdentifier(),doc.getName(), doc.getFileComment(), doc.getCreationDate(),doc.getExpirationDate(),
					doc.getType(), doc.getOwner().getLogin(), doc.getEncrypted(),
					doc.getShared(),doc.getSharedWithGroup(),doc.getSize());
		}else{	
			return null;
		}
	}
	
	public ShareDocumentVo disassemble(Share share) {
		if(null!=share){
			
			return new ShareDocumentVo(share.getDocument().getIdentifier(),share.getDocument().getName(),share.getDocument().getFileComment() ,share.getDocument().getCreationDate(),share.getDocument().getExpirationDate(),
					share.getDocument().getType(), share.getDocument().getOwner().getLogin(), share.getDocument().getEncrypted(),
					share.getDocument().getShared(),share.getDocument().getSharedWithGroup(),share.getDocument().getSize(),
					new UserVo(share.getSender()), new UserVo(share.getReceiver()),share.getExpirationDate(),
			share.getDownloaded(), share.getComment());
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
	
	public List<ShareDocumentVo> disassembleShareList(List<Share> shares) {
		List<ShareDocumentVo> documents=new ArrayList<ShareDocumentVo>();
		for(Share share :shares){
			documents.add(disassemble(share));
		}
		return documents;
	}
	
	public List<DocumentVo> disassembleList(List<Document> docs, List<Share> shares) {
		List<DocumentVo> documents=disassembleDocList(docs);
		documents.addAll(disassembleShareList(shares));
		return documents;
	}
	
}
