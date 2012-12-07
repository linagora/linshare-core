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

import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.transformers.Transformer;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;

public class ShareEntryTransformer implements Transformer<ShareEntry, ShareDocumentVo> {

	private final ShareEntryBusinessService shareEntryBusinessService;
	

	public ShareEntryTransformer(ShareEntryBusinessService shareEntryService) {
		super();
		this.shareEntryBusinessService = shareEntryService;
	}


	@Override
	public ShareDocumentVo disassemble(ShareEntry share) {
		
		UserVo sender = new UserVo(share.getEntryOwner());
		UserVo recipient = new UserVo(share.getRecipient());
		boolean downloaded = false;
		if(share.getDownloaded() >= 1) downloaded = true;
		
		return new ShareDocumentVo(share.getUuid(), 
				share.getName(),
				share.getComment(),
				share.getCreationDate(),
				share.getExpirationDate(),
				share.getType(),
				sender.getLsUid(),
				share.getDocumentEntry().getCiphered(),
				true,
				share.getSize(),
				sender,
				recipient,
				share.getExpirationDate(),
				share.getCreationDate(),
				downloaded);
	}


	@Override
	public List<ShareDocumentVo> disassembleList(List<ShareEntry> entityObjectList) {
		List<ShareDocumentVo> shareList = new ArrayList<ShareDocumentVo>();
		if (entityObjectList!=null) {
			for (ShareEntry share : entityObjectList) {
				shareList.add(disassemble(share));
			}
		}
		return shareList;
	}


	@Override
	public ShareEntry assemble(ShareDocumentVo valueObject) {
		return shareEntryBusinessService.findByUuid(valueObject.getIdentifier());
	}


	@Override
	public List<ShareEntry> assembleList(List<ShareDocumentVo> valueObjectList) {
		List<ShareEntry> shareList = new ArrayList<ShareEntry>();
		if (valueObjectList!=null) {
			for (ShareDocumentVo share : valueObjectList) {
				shareList.add(assemble(share));
			}
		}
		return shareList;
	}
}
