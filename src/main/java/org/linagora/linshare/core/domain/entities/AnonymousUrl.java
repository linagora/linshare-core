package org.linagora.linshare.core.domain.entities;

import java.util.Set;
import java.util.HashSet;

/**
 * @author fred
 *
 */
/**
 * @author fred
 *
 */
public class AnonymousUrl {

	private Long id;
	
	private String urlPath;
	
	private String uuid;
	
	private String password;
	
	private String temporaryPlainTextPassword;
	
	private Set<AnonymousShareEntry> anonymousShareEntries = new HashSet<AnonymousShareEntry>();
	
	public AnonymousUrl() {
	}
	
	public AnonymousUrl(String urlPath) {
		super();
		this.urlPath = urlPath;
		this.password = null;
		this.temporaryPlainTextPassword = null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<AnonymousShareEntry> getAnonymousShareEntries() {
		return anonymousShareEntries;
	}

	public void setAnonymousShareEntries(Set<AnonymousShareEntry> anonymousShareEntries) {
		this.anonymousShareEntries = anonymousShareEntries;
	}

	public String getTemporaryPlainTextPassword() {
		return temporaryPlainTextPassword;
	}

	public void setTemporaryPlainTextPassword(String temporaryPlainTextPassword) {
		this.temporaryPlainTextPassword = temporaryPlainTextPassword;
	}
	
	public String getFullUrl(String baseUrl) {
		//compose the secured url to give in mail
		StringBuffer httpUrlBase = new StringBuffer();
		httpUrlBase.append(baseUrl);
		if(!baseUrl.endsWith("/")) httpUrlBase.append("/");
		httpUrlBase.append(getUrlPath());
		if(!getUrlPath().endsWith("/")) httpUrlBase.append("/");
		httpUrlBase.append(getUuid());
		return httpUrlBase.toString();
	}
	
}
