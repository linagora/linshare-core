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


/**
 * this abstract represent a favourite.
 * A favourite contains a weight which will permits to determine the prefered elements.
 * 
 * @author ngapaillard
 * @param <T> the owner type.
 *
 */
public abstract class AbstractFavourite<T> {

	/** the weight of the element. **/
	private Long weight;
	/**
	 * the owner of the favourite.
	 */
	private T owner;
	

	/**
	 * Set the weight of the current element.
	 * @param weight
	 */
	public void setWeight(Long weight){
		this.weight=weight;
	}
	
	/**
	 * Retrieve the current element weight.
	 * @return the weight of the current element.
	 */
	public Long getWeight(){
		return this.weight;
	}
	
	/**
	 * increase the weight by one.
	 */
	public void inc(){
		this.weight++;
	}

	/**
	 * give the owner of the favorite.
	 * @return owner the owner of the favourite.
	 */
	public T getOwner() {
		return owner;
	}

	/**
	 * set the owner of the favorite.
	 * @param owner the owner of the favourite.
	 */
	public void setOwner(T owner) {
		this.owner = owner;
	}

}
