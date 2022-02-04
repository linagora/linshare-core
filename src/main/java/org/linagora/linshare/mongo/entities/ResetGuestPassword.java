/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
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
package org.linagora.linshare.mongo.entities;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.core.domain.constants.ResetTokenKind;
import org.linagora.linshare.core.domain.entities.Guest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author FMartin
 *
 */
@XmlRootElement(name = "ResetGuestPassword")
@Document(collection = "reset_guest_password")
public class ResetGuestPassword {

	@JsonIgnore
	@Id @GeneratedValue
	protected String id;

	@Schema(description = "Uuid")
	protected String uuid;

	@Schema(description = "Mail")
	protected String mail;

	@Schema(description = "FirstName")
	protected String firstName;

	@Schema(description = "LastName")
	protected String lastName;

	@Schema(description = "CreationDate of this token")
	protected Date creationDate;

	@Schema(description = "Expiration date for this token")
	protected Date expirationDate;

	@JsonIgnore
	protected Boolean alreadyUsed;

	@Schema(description = "New password.")
	protected String password;

	@Schema(description = "Reset password or new password.")
	protected ResetTokenKind kind;

	@JsonIgnore
	protected String guestUuid;

	@JsonIgnore
	protected String domainUuid;

	public ResetGuestPassword(Guest guest) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.mail = guest.getMail();
		this.firstName = guest.getFirstName();
		this.lastName = guest.getLastName();
		this.creationDate = new Date();
		this.alreadyUsed = false;
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.HOUR, 1);
		this.expirationDate = instance.getTime();
		this.guestUuid = guest.getLsUuid();
		this.domainUuid = guest.getDomain().getUuid();
	}

	public ResetGuestPassword() {
		super();
	}

	@XmlTransient
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@XmlTransient
	public String getGuestUuid() {
		return guestUuid;
	}

	public void setGuestUuid(String guestUuid) {
		this.guestUuid = guestUuid;
	}

	@XmlTransient
	public String getDomainUuid() {
		return domainUuid;
	}

	public void setDomainUuid(String domainUuid) {
		this.domainUuid = domainUuid;
	}

	@XmlTransient
	public Boolean getAlreadyUsed() {
		return alreadyUsed;
	}

	public void setAlreadyUsed(Boolean alreadyUsed) {
		this.alreadyUsed = alreadyUsed;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ResetTokenKind getKind() {
		return kind;
	}

	public void setKind(ResetTokenKind kind) {
		this.kind = kind;
	}

	@Override
	public String toString() {
		return "ResetGuestPassword [uuid=" + uuid + ", mail=" + mail + ", firstName=" + firstName + ", lastName="
				+ lastName + ", creationDate=" + creationDate + ", expirationDate=" + expirationDate + ", alreadyUsed="
				+ alreadyUsed + ", guestUuid=" + guestUuid + ", domainUuid=" + domainUuid + "]";
	}

}
