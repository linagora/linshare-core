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

import org.linagora.linShare.core.domain.entities.Signature;
import org.linagora.linShare.core.domain.transformers.Transformer;
import org.linagora.linShare.core.domain.vo.SignatureVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.TechnicalException;

public class SignatureTransformer implements Transformer<Signature, SignatureVo>{

	
	private final UserTransformer userTransformer;
	
	
	public SignatureTransformer(final UserTransformer userTransformer){
		this.userTransformer = userTransformer;
	}
	
	public Signature assemble(SignatureVo valueObject) {
		throw new TechnicalException("not implemented, should not be used");
	}

	public List<Signature> assembleList(List<SignatureVo> valueObjectList) {
		throw new TechnicalException("not implemented, should not be used");
	}

	public SignatureVo disassemble(Signature entityObject) {
		
		SignatureVo res = null;
		
		if(null!=entityObject){
			res = new SignatureVo();
			res.setIdentifier(entityObject.getIdentifier());
			res.setCreationDate(entityObject.getCreationDate());
			res.setCertIssuerDn(entityObject.getCertIssuerDn());
			res.setCertSubjectDn(entityObject.getCertSubjectDn());
			res.setCertNotAfter(entityObject.getCertNotAfter());
			res.setCert(entityObject.getCert());
			res.setSize(entityObject.getSize());
			res.setName(entityObject.getName());
			res.setPersistenceId(entityObject.getPersistenceId());
			
			UserVo signer = userTransformer.disassemble(entityObject.getSigner());
			res.setSigner(signer);	
		}
		return res;
	}

	public List<SignatureVo> disassembleList(List<Signature> entityObjectList) {
		ArrayList<SignatureVo> allowedMimeTypes=new ArrayList<SignatureVo>();
		for(Signature allowedMimeType :entityObjectList){
			allowedMimeTypes.add(disassemble(allowedMimeType));
		}
		return allowedMimeTypes;
	}

}
