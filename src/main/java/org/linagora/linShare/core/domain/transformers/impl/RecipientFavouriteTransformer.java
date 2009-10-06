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

import org.linagora.linShare.core.domain.entities.RecipientFavourite;
import org.linagora.linShare.core.domain.transformers.Transformer;
import org.linagora.linShare.core.domain.vo.RecipientFavouriteVO;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.TechnicalException;


public class RecipientFavouriteTransformer implements Transformer<RecipientFavourite, RecipientFavouriteVO>{

	public RecipientFavourite assemble(RecipientFavouriteVO valueObject) {
		throw new TechnicalException("Should not be used");
	}

	public List<RecipientFavourite> assembleList(
			List<RecipientFavouriteVO> valueObjectList) {
		throw new TechnicalException("Should not be used");
	}

	public RecipientFavouriteVO disassemble(RecipientFavourite entityObject) {
		if(null!=entityObject){
			return new RecipientFavouriteVO(new UserVo(entityObject.getOwner()),entityObject.getRecipient(),entityObject.getWeight());
		}else{
			return null;
		}
	}

	public List<RecipientFavouriteVO> disassembleList(
			List<RecipientFavourite> entityObjectList) {
		ArrayList<RecipientFavouriteVO> recipients=new ArrayList<RecipientFavouriteVO>();
		for(RecipientFavourite recipientFavourite:entityObjectList){
			recipients.add(disassemble(recipientFavourite));
		}
		return recipients;
	}

}
