/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.domain.objects;

import java.util.Calendar;

public class FileInfo {

	private String name;
	private String path;

	private Long size;
	private String mimeType;
	private String uuid;
	private Calendar lastModified;
	


	public FileInfo(){
		
	}
	
	public FileInfo(String uuid,String name,String path){
		this.uuid=uuid;
		this.name=name;
		this.path=path;

	}
	
	public FileInfo(String uuid,String name,String path,Long size){
		this.uuid=uuid;
		this.name=name;
		this.path=path;

		this.size=size;
		
	}
	
	public FileInfo(String uuid,String name,String path,Long size,String mimeType){
		this.uuid=uuid;
		this.name=name;
		this.path=path;

		this.size=size;
		this.mimeType=mimeType;
		
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Calendar getLastModified() {
		return lastModified;
	}

	public void setLastModified(Calendar lastModified) {
		this.lastModified = lastModified;
	}
	
	
	
}
