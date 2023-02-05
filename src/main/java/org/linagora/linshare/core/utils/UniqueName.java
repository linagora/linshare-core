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
package org.linagora.linshare.core.utils;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Lists;

public class UniqueName {

	protected final String originalName;

	protected String baseName;

	protected String suffix;

	protected String searchPattern;

	protected String extractPattern;

	protected List<WorkGroupNode> foundNodes;

	public UniqueName(String name) {
		super();
		originalName = name;
		baseName = name;
		suffix = "";
		foundNodes = null;
		searchPattern = null;
		extractPattern = null;
		
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setSearchPattern(String searchPattern) {
		this.searchPattern = searchPattern;
	}

	public void setExtractPattern(String extractPattern) {
		this.extractPattern = extractPattern;
	}

	public void setFoundNodes(List<WorkGroupNode> foundNodes) {
		this.foundNodes = foundNodes;
	}

	@Override
	public String toString() {
		return "UniqueName [originalName=" + originalName + ", baseName=" + baseName + ", suffix=" + suffix
				+ ", searchPattern=" + searchPattern + ", extractPattern=" + extractPattern + "]";
	}

	public UniqueName validate() {
		Validate.notEmpty(originalName, "Invalid originalName");
		Validate.notEmpty(baseName, "Invalid baseName");
		Validate.notEmpty(searchPattern, "Missing search pattern");
		Validate.notEmpty(extractPattern, "Missing extract pattern");
		return this;
	}

	public Query buildQuery() {
		Validate.notEmpty(searchPattern, "Missing search pattern");
		Query query = new Query();
		query.addCriteria(Criteria.where("name").regex(searchPattern));
		query.with(Sort.by(Sort.Direction.DESC, "name"));
		return query;
	}

	public Long extractCptFromName(String name) {
		Validate.notEmpty(extractPattern, "Missing extract pattern");
		Pattern pattern = Pattern.compile(extractPattern);
		Matcher matcher = pattern.matcher(name);
		Long cpt = 1L;
		if (matcher.find()) {
			String res = matcher.group(1);
			cpt = Long.valueOf(res);
		}
		return cpt;
	}

	public String computeNewName() {
		if (foundNodes != null) {
			Long cpt = 1L;
			if (!foundNodes.isEmpty()) {
				List<Long> cpts = Lists.newArrayList();
				for (WorkGroupNode node : foundNodes) {
					cpts.add(extractCptFromName(node.getName()));
				}
				cpt = (Long) Collections.max(cpts);
				cpt ++;
			}
			return baseName + " (" + cpt + ")" + suffix;
		}
		return originalName;
	}

	public static String getEscapeName(String name) {
		String escapedName = name.replace(".", "\\.")
				.replace("?", "\\?")
				.replace("+", "\\+")
				.replace("(", "\\(")
				.replace(")", "\\)")
				.replace("[", "\\[")
				.replace("]", "\\]")
				.replace("{", "\\{")
				.replace("}", "\\}");
		return escapedName;
	}

}
