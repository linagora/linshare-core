package org.linagora.linshare.core.domain.transformers.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.transformers.Transformer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.repository.ThreadEntryRepository;


public class ThreadEntryTransformer implements Transformer<ThreadEntry, DocumentVo> {

	private final ThreadEntryRepository threadEntryRepository;

	public ThreadEntryTransformer(ThreadEntryRepository threadEntryRepository) {
		super();
		this.threadEntryRepository = threadEntryRepository;
	}

	@Override
	public DocumentVo disassemble(ThreadEntry entityObject) {

		if(null!=entityObject){
				return new DocumentVo(entityObject.getUuid(),entityObject.getName(), entityObject.getComment() ,entityObject.getCreationDate(),entityObject.getExpirationDate(),
					entityObject.getDocument().getType(), entityObject.getEntryOwner().getLsUuid(), entityObject.getCiphered(),
					false,entityObject.getDocument().getSize());
		}
		return null;
	}
	
	@Override
	public List<DocumentVo> disassembleList(List<ThreadEntry> entityObjectList) {
		ArrayList<DocumentVo> documents=new ArrayList<DocumentVo>();
		for(ThreadEntry document : entityObjectList){
			documents.add(disassemble(document));
		}
		return documents;
	}

	@Override
	public ThreadEntry assemble(DocumentVo valueObject) {
		if(null!=valueObject){
			return (ThreadEntry) threadEntryRepository.findByUuid(valueObject.getIdentifier());
		}else{	
			return null;
		}
	}

	@Override
	public List<ThreadEntry> assembleList(List<DocumentVo> valueObjectList) {
		ArrayList<ThreadEntry> documents=new ArrayList<ThreadEntry>();
		for(DocumentVo documentVo :valueObjectList){
			documents.add(assemble(documentVo));
		}
		return documents;
	}

}
