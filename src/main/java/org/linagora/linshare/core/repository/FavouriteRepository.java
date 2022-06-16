/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.repository;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;

/**
 * A DAO which permits to interact with favourites.
 * @author ngapaillard
 *
 * @param <T> the type of element which is concerned by the weight.
 * @param <U> the type the owner of a favourite.
 * @param <V> the entity class concern by the favourite implementation used.
 */
public interface FavouriteRepository<T,U,V> extends AbstractRepository<V>{

	/**
	 * tell if a favourite exists for a given owner.
	 * @param owner the owner concerned.
	 * @param element the element to check for the owner.
	 * @return true if a favourite exists with owner AND element in the entity.
	 */
	boolean existFavourite(U owner,T element);
	
	/**
	 * Increment the weight of an element.
	 * @param element the element concerned by the increase of weight.
	 */
	void incAndCreate(U owner, T element, Date expirationDate, boolean isExternal);
	
	/**
	 * Increment the weight of an element.
	 * @param elements the elements concerned by the increase of weight.
	 */
	void inc(List<T> elements, U owner, Date expirationDate) throws LinShareNotSuchElementException,BusinessException;
	
	/**
	 * Increment the weight of several elements, if they don't exist, there are created before.
	 * @param owner
	 * @param elements
	 * @param expirationDate TODO
	 * @throws LinShareNotSuchElementException
	 * @throws BusinessException
	 */
	void incAndCreate(U owner, List<T> elements, Date expirationDate) throws LinShareNotSuchElementException,BusinessException;
	
	/**
	 * reorder elements by weight in desc order.
	 * @param elements
	 * @param owner
	 * @throws LinShareNotSuchElementException
	 */
	List<String> reorderElementsByWeightDesc(List<T> elements,U owner);
	
	/**
	 * Return the weight associated to an element.
	 * @param element the element we have to retrieve the weight.
	 * @return the weight of the element.
	 */
	Long getWeight(T element,U owner) throws LinShareNotSuchElementException;
	
	/**
	 * Give the element that have the max weight.
	 * @return the element having the max height.
	 */
	T getElementWithMaxWeight(U owner) throws LinShareNotSuchElementException;
	
	/**
	 * Return a list of all elements ordering ascendant by there weight. 
	 * @return list a list of all elements ordering ascendant by there weight. If there is no elements return an empty list.
	 */
	List<T> getElementsOrderByWeight(U owner);
	
	/**
	 * Return a list of all elements ordering descendant by there weight. 
	 * @return list a list of all elements ordering descendant by there weight.  If there is no elements return an empty list.
	 */
	List<T> getElementsOrderByWeightDesc(U owner);
	
	
	/**
	 * find in favorite repository, elements which start with match pattern.
	 * @param matchStartWith pattern
	 * @param owner of the favorite
	 * @return list a list of all elements ordering descendant by their weight.
	 */
	List<T> findMatchElementsOrderByWeight(T matchStartWith,U owner);
	
	List<V> findMatchElementsOrderByWeight(T matchStartWith,U owner, int limit);
	
	/**
	 * delete all the elements of the owner
	 * @param owner
	 */
	void deleteFavoritesOfUser(U owner) throws IllegalArgumentException, BusinessException;

	void deleteOneFavoriteOfUser(U owner, T recipient) throws IllegalArgumentException, BusinessException;
}
