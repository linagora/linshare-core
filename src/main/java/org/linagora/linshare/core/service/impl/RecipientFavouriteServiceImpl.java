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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.service.RecipientFavouriteService;

public class RecipientFavouriteServiceImpl implements RecipientFavouriteService{

	private FavouriteRepository<String, User, RecipientFavourite> repo;
	
	
	public RecipientFavouriteServiceImpl(FavouriteRepository<String, User, RecipientFavourite> repo){
		this.repo=repo;
	}
	
	public void increment(User owner, List<String> recipients) throws LinShareNotSuchElementException, BusinessException {
		repo.incAndCreate(recipients, owner);
	}

	public List<String> recipientsOrderedByWeightDesc(User owner){
		return repo.getElementsOrderByWeightDesc(owner);
	}

	public List<String> reorderRecipientsByWeightDesc(List<String> recipients,
			User owner) {
		return repo.reorderElementsByWeightDesc(recipients, owner);
	}

	public List<String> findRecipientFavorite(String matchEmail, User owner) {
		return repo.findMatchElementsOrderByWeight(matchEmail, owner);
	}
	
	public void deleteFavoritesOfUser(User owner) throws IllegalArgumentException, BusinessException {
		repo.deleteFavoritesOfUser(owner);
	}


}
