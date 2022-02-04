/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.webservice.utils;

import java.util.List;

import org.springframework.data.domain.Page;

import com.google.common.collect.Lists;

public class PageResponse<T> {

	private Long totalElements;

	private Integer totalPages;

	private List<T> content;

	private Boolean first = false;

	private Boolean last = false;

	public PageResponse(Long totalElements, Integer totalPages, List<T> content, Boolean first, Boolean last) {
		super();
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.content = content;
		this.first = first;
		this.last = last;
	}

	public PageResponse(Page<T> queryResponse) {
		super();
		this.totalElements = queryResponse.getTotalElements();
		this.totalPages = queryResponse.getTotalPages();
		this.content = queryResponse.getContent();
		this.first = queryResponse.isFirst();
		this.last = queryResponse.isLast();
	}

	public PageResponse() {
		super();
		this.totalElements = 0L;
		this.totalPages = 0;
		this.content = Lists.newArrayList();
		this.first = true;
		this.last = true;
	}

	public Long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public Boolean isFirst() {
		return first;
	}

	public void setFirst(Boolean first) {
		this.first = first;
	}

	public Boolean isLast() {
		return last;
	}

	public void setLast(Boolean last) {
		this.last = last;
	}

	public void update(Page<T> page) {
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.content = page.getContent();
		this.first = page.isFirst();
		this.last = page.isLast();
	}

	@Override
	public String toString() {
		return "PageResponse [totalElements=" + totalElements + ", totalPages=" + totalPages + ", content=" + content
				+ ", first=" + first + ", last=" + last + "]";
	}
}
