package org.linagora.linshare.core.Facade;

import java.io.InputStream;
import java.util.List;

import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.TagEnumVo;
import org.linagora.linshare.core.domain.vo.TagVo;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface ThreadEntryFacade {
	
	public ThreadEntryVo insertFile(UserVo actorVo, ThreadVo threadVo, InputStream stream, Long size, String fileName) throws BusinessException ;
	
	public List<ThreadVo> getAllThread();
	
	public List<ThreadEntryVo> getAllThreadEntryVo(UserVo actorVo, ThreadVo threadVo) throws BusinessException;

	public TagEnumVo getTagEnumVo(UserVo actorVo, ThreadVo threadVo, String name) throws BusinessException;
	
	public void setTagsToThreadEntries(UserVo actorVo, ThreadVo threadVo, List<ThreadEntryVo> threadEntriesVo, List<TagVo> tags) throws BusinessException;
}
