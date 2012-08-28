package org.linagora.linshare.core.domain.entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagFilterByDomain extends TagFilterRule {

	@Override
	public Boolean isTrue(Account actor) {
//		Pattern pattern = Pattern.compile(regexp);
//		Matcher matcher = pattern.matcher(actor.getDomain().getIdentifier());
//		return matcher.matches();
		if(actor.getDomain().getIdentifier().equals(regexp)){
			return true;
		}
		return false;
	}

}
