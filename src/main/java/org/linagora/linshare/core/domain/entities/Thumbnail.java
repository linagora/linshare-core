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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.ThumbnailType;

public class Thumbnail {

	private long id;

	/**
	 * the identifier of the thumbnail.
	 */
	private String thumbnailUuid;

	/**
	 * the type of the thumbnail (Small, Medium, Large ...)
	 */
	private ThumbnailType thumbnailType;

	/**
	 * the creation date of the thumbnail.
	 */
	private Date creationDate;

	private Document document;

	public Thumbnail() {
		super();
	}

	public Thumbnail(String thumbnailUuid) {
		super();
		this.thumbnailUuid = thumbnailUuid;
	}

	public Thumbnail(String thumbnailUuid, ThumbnailType thumbnailType, Document document) {
		this.thumbnailUuid = thumbnailUuid;
		this.creationDate = new Date();
		this.document = document;
		this.thumbnailType = thumbnailType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getThumbnailUuid() {
		return thumbnailUuid;
	}

	public void setThumbnailUuid(String thumbnailUuid) {
		this.thumbnailUuid = thumbnailUuid;
	}

	public ThumbnailType getThumbnailType() {
		return thumbnailType;
	}

	public void setThumbnailType(ThumbnailType thumbnailType) {
		this.thumbnailType = thumbnailType;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date date) {
		this.creationDate = date;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

}
