package org.linagora.linshare.core.domain.transformers.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.transformers.Transformer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.repository.DocumentEntryRepository;


public class DocumentEntryTransformer implements Transformer<DocumentEntry, DocumentVo> {

	private final DocumentEntryRepository documentEntryRepository;

	public DocumentEntryTransformer(DocumentEntryRepository documentEntryRepository) {
		super();
		this.documentEntryRepository = documentEntryRepository;
	}

	@Override
	public DocumentVo disassemble(DocumentEntry entityObject) {

		if(null!=entityObject){
				return new DocumentVo(entityObject.getUuid(),entityObject.getName(), entityObject.getComment() ,entityObject.getCreationDate(),entityObject.getExpirationDate(),
					entityObject.getDocument().getType(), entityObject.getEntryOwner().getLsUuid(), entityObject.getCiphered(),
					entityObject.isShared(),entityObject.getDocument().getSize());
		}
			
		return null;
	}
	
	@Override
	public List<DocumentVo> disassembleList(List<DocumentEntry> entityObjectList) {
		ArrayList<DocumentVo> documents=new ArrayList<DocumentVo>();
		for(DocumentEntry documentEntry : entityObjectList){
			documents.add(disassemble(documentEntry));
		}
		return documents;
	}

	@Override
	public DocumentEntry assemble(DocumentVo valueObject) {
		if(null!=valueObject){
			return (DocumentEntry) documentEntryRepository.findById(valueObject.getIdentifier());
		}else{	
			return null;
		}
	}

	@Override
	public List<DocumentEntry> assembleList(List<DocumentVo> valueObjectList) {
		ArrayList<DocumentEntry> documents=new ArrayList<DocumentEntry>();
		for(DocumentVo documentVo :valueObjectList){
			documents.add(assemble(documentVo));
		}
		return documents;
	}

}
