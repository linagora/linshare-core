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
package org.linagora.linshare.webservice.utils;

import java.util.List;
import java.util.Objects;

import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageContainer<T> {

	private static final Pageable DEFAULT_PAGE_REQUEST = PageRequest.of(0, 50);

	private Integer pageNumber;

	private Integer pageSize;

	private PageResponse<T> pageResponse;

	private Long totalElements;

	private List<T> list;

	private Integer totalPagesCount;

	public PageContainer() {
		this.pageResponse = new PageResponse<>();
	}

	public PageContainer(Integer pageNumber, Integer pageSize) {
		this.pageSize = validatePageSize(pageSize);
		this.pageNumber = validatePageNumber(pageNumber);
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

	public Long getTotalElements() {
		return totalElements;
	}

	private Integer validatePageNumber(Integer pageNumber){
		if (Objects.nonNull(pageNumber)) {
			if (pageNumber < 0) {
				throw new BusinessException(BusinessErrorCode.WRONG_PAGE_PARAMETERS,
						"Page number can not be less than 0");
			}
			return pageNumber;
		}
		return DEFAULT_PAGE_REQUEST.getPageNumber();
	}

	private Integer validatePageSize(Integer pageSize) {
		if (Objects.isNull(pageSize) || pageSize <= 0) {
			return DEFAULT_PAGE_REQUEST.getPageSize();
		}
		return pageSize;
	}

	public void validateTotalPagesCount(Long totalElements) {
		this.totalElements = totalElements;
		this.totalPagesCount = pageCount(totalElements);
		if (pageNumber >= totalPagesCount && totalElements > 0) {
			throw new BusinessException(BusinessErrorCode.WRONG_PAGE_PARAMETERS, String.format(
					"knowing that the page number starts from 0, you have exceeded the total pages' count:  %1$s , or you may exceeded the total elements' count: %2$s, please check the entered pageNumber: %3$s , and pageSize: %4$s",
					totalPagesCount, totalElements, pageNumber, pageSize));
		}
	}

	private Integer pageCount(Long totalElements) {
		Long pageCount = totalElements / pageSize;
		if (totalElements % pageSize != 0) {
			pageCount++;
		}
		return pageCount.intValue();
	}

	public PageContainer<T> loadData(List<T> list) {
		this.list = list;
		this.pageResponse = new PageResponse<T>(totalElements, totalPagesCount, list, isFirst(), isLast());
		return this;
	}

	public PageContainer<T> loadDataAndCount(List<T> list) {
		this.list = list;
		validateTotalPagesCount((long) list.size());
		this.pageResponse = new PageResponse<T>(totalElements, totalPagesCount, list, isFirst(), isLast());
		return this;
	}

	public PageContainer<T> loadDataAndPaginate(List<T> list) {
		this.list = list;
		validateTotalPagesCount((long) list.size());
		int fromIndex = pageNumber * pageSize; //inclusive
		int toIndex = Math.min(fromIndex + pageSize, list.size()); //exclusive
		this.pageResponse = new PageResponse<T>(totalElements, totalPagesCount, list.subList(fromIndex, toIndex), isFirst(), isLast());
		return this;
	}

	public List<T> getList() {
		return list;
	}

	public PageResponse<T> getPageResponse() {
		return pageResponse;
	}

	public void setPageResponse(PageResponse<T> pageResponse) {
		this.pageResponse = pageResponse;
	}

	private Boolean isFirst() {
		return (pageNumber == 0) || (pageSize >= totalElements) || (totalPagesCount == 1) || (totalElements == 0);
	}

	private Boolean isLast() {
		return (pageNumber == totalPagesCount - 1) || (pageSize >= totalElements) || (totalPagesCount == 1) || (totalElements == 0);
	}

	@Override
	public String toString() {
		return "PageContainer [pageNumber=" + pageNumber + ", pageSize=" + pageSize + ", totalElements=" + totalElements
				+ ", totalPagesCount=" + totalPagesCount + "]";
	}
}
