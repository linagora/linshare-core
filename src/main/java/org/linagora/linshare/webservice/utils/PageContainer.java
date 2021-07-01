/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
import java.util.Objects;

import org.jclouds.rest.ResourceNotFoundException;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageContainer<T> {

	private static final Pageable DEFAULT_PAGE_REQUEST = PageRequest.of(0, 50);

	private Integer pageNumber;

	private Integer pageSize;

	private Boolean enabled;

	private PageResponse<T> pageResponse;


	public PageContainer() {
		this.enabled = false;
		this.pageResponse = new PageResponse<>();
	}

	public PageContainer(Integer pageNumber, Integer pageSize) {
		this.pageSize = Objects.nonNull(pageSize) ? pageSize : DEFAULT_PAGE_REQUEST.getPageSize();
		this.pageNumber = Objects.nonNull(pageNumber) ? pageNumber : DEFAULT_PAGE_REQUEST.getPageNumber();
		this.enabled = getPagingStatus(pageNumber, pageSize);
		this.pageResponse = new PageResponse<>();
	}

	public PageContainer(Integer pageNumber, Integer pageSize, Long totalElements, List<T> list) {
		super();
		// Pagination feature is disabled if all pagination attributes equal -1
		this.enabled = getPagingStatus(pageNumber, pageSize);
		if (enabled) {
			if (pageNumber < 0) {
				throw new BusinessException(BusinessErrorCode.WRONG_PAGE_PARAMETERS,
						"Page number can not be less than 0");
			}
			this.pageNumber = pageNumber;
			if (pageSize <= 0) {
				this.pageSize = DEFAULT_PAGE_REQUEST.getPageSize();
			} else {
				this.pageSize = pageSize;
			}
			Integer totalPagesCount = pageCount(this.pageSize, totalElements);
			Boolean isFirst = isFirst(pageNumber, totalPagesCount, totalElements);
			Boolean isLast = isLast(totalPagesCount, totalElements);
			this.pageResponse = new PageResponse<T>(totalElements, totalPagesCount, list, isFirst, isLast);
			if (pageNumber >= totalPagesCount && totalElements > 0) {
				throw new BusinessException(BusinessErrorCode.WRONG_PAGE_PARAMETERS, String.format(
						"knowing that the page number starts from 0, you have exceeded the total pages' count:  %1$s , or you may exceeded the total elements' count: %2$s, please check the entered pageNumber: %3$s , and pageSize: %4$s",
						totalPagesCount, totalElements, pageNumber, pageSize));
			}
		}
	}

	private Boolean isLast(Integer pageCount, Long totalElements) {
		return (pageNumber == pageCount - 1) || (pageSize >= totalElements) || (pageCount == 1) || (totalElements == 0);
	}

	private Integer pageCount(Integer pageSize, Long totalElements) {
		Integer localPageSize = pageSize;
		Long pageCount = totalElements / localPageSize;
		if (totalElements % pageSize != 0) {
			pageCount++;
		}
		return pageCount.intValue();
	}

	private Boolean isFirst(Integer pageNumber, Integer pageCount, Long totalElements) {
		return (pageNumber == 0) || (pageSize >= totalElements) || (pageCount == 1) || (totalElements == 0);
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Pageable getDefaultPageRequest() {
		return DEFAULT_PAGE_REQUEST;
	}

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "PageContainer [pageNumber=" + pageNumber + ", pageSize=" + pageSize + ", totalElements=" + ", enabled="
				+ enabled + "]";
	}

	public PageResponse<T> getPageResponse() {
		return pageResponse;
	}

	public void setPageResponse(PageResponse<T> pageResponse) {
		this.pageResponse = pageResponse;
	}

	public void updatePageResponse(Page<T> page) {
		this.pageNumber = page.getNumber();
		this.pageSize = page.getNumberOfElements();
		this.pageResponse.update(page);
	}

	public Boolean getPagingStatus(Integer pageNumber, Integer pageSize) {
		return ((Objects.isNull(pageNumber) && Objects.isNull(pageSize)) || (pageNumber == -1 && pageSize == -1))
				? false
				: true;
	}
}
