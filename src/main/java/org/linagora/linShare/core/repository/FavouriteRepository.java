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
package org.linagora.linShare.core.repository;

import java.util.List;

import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.LinShareNotSuchElementException;

/**
 * A DAO which permits to interact with favourites.
 * @author ngapaillard
 *
 * @param <T> the type of element which is concerned by the weight.
 * @param <T> the type the owner of a favourite.
 * @param <V> the entity class concern by the favourite implementation used.
 */
public interface FavouriteRepository<T,U,V> extends AbstractRepository<V>{

	/**
	 * tell if a favourite exists for a given owner.
	 * @param owner the owner concerned.
	 * @param element the element to check for the owner.
	 * @return true if a favourite exists with owner AND element in the entity.
	 */
	public boolean existFavourite(U owner,T element);
	
	/**
	 * Increment the weight of an element.
	 * @param element the element concerned by the increase of weight.
	 */
	public void inc(T element,U owner) throws LinShareNotSuchElementException,BusinessException;
	
	/**
	 * Increment the weight of an element.
	 * @param elements the elements concerned by the increase of weight.
	 */
	public void inc(List<T> elements,U owner) throws LinShareNotSuchElementException,BusinessException;
	
	/**
	 * Increment the weight of several elements, if they don't exist, there are created before.
	 * @param elements
	 * @param owner
	 * @throws LinShareNotSuchElementException
	 * @throws BusinessException
	 */
	public void incAndCreate(List<T> elements,U owner) throws LinShareNotSuchElementException,BusinessException;
	
	/**
	 * reorder elements by weight in desc order.
	 * @param elements
	 * @param owner
	 * @throws LinShareNotSuchElementException
	 */
	public List<String> reorderElementsByWeightDesc(List<T> elements,U owner);
	
	/**
	 * Return the weight associated to an element.
	 * @param element the element we have to retrieve the weight.
	 * @return the weight of the element.
	 */
	public Long getWeight(T element,U owner) throws LinShareNotSuchElementException;
	
	/**
	 * Give the element that have the max weight.
	 * @return the element having the max height.
	 */
	public T getElementWithMaxWeight(U owner) throws LinShareNotSuchElementException;
	
	/**
	 * Return a list of all elements ordering ascendant by there weight. 
	 * @return list a list of all elements ordering ascendant by there weight. If there is no elements return an empty list.
	 */
	public List<T> getElementsOrderByWeight(U owner);
	
	/**
	 * Return a list of all elements ordering descendant by there weight. 
	 * @return list a list of all elements ordering descendant by there weight.  If there is no elements return an empty list.
	 */
	public List<T> getElementsOrderByWeightDesc(U owner);
	
	
	/**
	 * find in favorite repository, elements which start with match pattern.
	 * @param matchStartWith pattern
	 * @param owner of the favorite
	 * @return list a list of all elements ordering descendant by their weight.
	 */
	public List<T> findMatchElementsOrderByWeight(T matchStartWith,U owner);
	
	/**
	 * delete all the elements of the owner
	 * @param owner
	 */
	public void deleteFavoritesOfUser(U owner) throws IllegalArgumentException, BusinessException;
}
