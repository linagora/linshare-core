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
package org.linagora.linShare.view.tapestry.pages;

import java.io.InputStream;
import java.util.List;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.objects.FileInfo;
import org.linagora.linShare.view.tapestry.objects.CustomStreamResponse;

public class RepositoryContent {

	@Inject
	private FileSystemDao fileRepository;
	
	@Property
	@Persist
	private List<FileInfo> listPath;
	
	@SuppressWarnings("unused")
	@Property
	@Persist
	private FileInfo file;
	
	
	@SetupRender
	public void initList(){
		listPath=fileRepository.getAllFilePathInSubPath("user1");
		
	}
	
	
	StreamResponse onActionFromDownload(String uuid){
		
		InputStream stream=fileRepository.getFileContentByUUID(uuid);
		FileInfo fileInfo=retrieveFileByUuid(uuid);
		
		
		return new CustomStreamResponse(fileInfo,stream);
	}
	void onActionFromRemove(String uuid){
		fileRepository.removeFileByUUID(uuid);
	}
	private FileInfo retrieveFileByUuid(String uuid){
		for(FileInfo info:listPath){
			if(info.getUuid().equals(uuid)){
				return info;
			}
		}
		return null;
	}
	
}
