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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ContactList;

import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;
import org.linagora.linshare.core.domain.entities.ContactListContact;

@XmlRootElement(name = "ContactList")
@XmlAccessorType(XmlAccessType.FIELD)
@Schema(name = "ContactList", description = "Contact list")
public class ContactListDto {

    @Schema(description = "Name")
    private String name;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "IsPublic")
    private boolean isPublic;

    @Schema(description = "Owner")
    private GenericUserDto owner;

    @Schema(description = "Uuid")
    private String uuid;

    @Schema(description = "Domain")
    private CommonDomainLightDto domain;

    @Schema(description = "Creation Date")
    protected Date creationDate;

    @Schema(description = "Modification Date")
    protected Date modificationDate;

    @Schema(description = "can_view_contact_list_members")
    private Boolean canViewContactListMembers;

    @Schema(description = "Contact list members")
    private List<ContactListContactDto> contacts;

    public ContactListDto() {
        super();
        this.contacts = new ArrayList<>();
    }

    public ContactListDto(final ContactList list) {
        this();
        this.uuid = list.getUuid();
        this.name = list.getIdentifier();
        this.description = list.getDescription();
        this.isPublic = list.isPublic();

        if (list.getOwner() != null) {
            this.owner = new GenericUserDto(list.getOwner());
        }
        if (list.getDomain() != null) {
            this.domain = new CommonDomainLightDto(list.getDomain());
        }
        this.creationDate = list.getCreationDate();
        this.modificationDate = list.getModificationDate();
        if (list.getContactListContacts() != null) {
            for (final ContactListContact contact : list.getContactListContacts()) {
                this.contacts.add(new ContactListContactDto(contact));
            }
        }
    }

    public ContactList toObject() {
        final ContactList list = new ContactList();
        list.setUuid(getUuid());
        list.setIdentifier(getName());
        list.setDescription(getDescription());
        list.setPublic(isPublic());
        return list;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public void setPublic(final boolean isPublic) {
        this.isPublic = isPublic;
    }

    public GenericUserDto getOwner() {
        return this.owner;
    }

    public void setOwner(final GenericUserDto owner) {
        this.owner = owner;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public CommonDomainLightDto getDomain() {
        return this.domain;
    }

    public void setDomain(final CommonDomainLightDto domain) {
        this.domain = domain;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return this.modificationDate;
    }

    public void setModificationDate(final Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Boolean getCanViewContactListMembers() {
        return this.canViewContactListMembers;
    }

    public void setCanViewContactListMembers(final Boolean canViewContactListMembers) {
        this.canViewContactListMembers = canViewContactListMembers;
    }

    public List<ContactListContactDto> getContacts() {
        return this.contacts;
    }

    public void setContacts(final List<ContactListContactDto> contacts) {
        this.contacts = contacts;
    }

    /*
     * Transformers
     */

    public static Function<ContactList, ContactListDto> toDto() {
        return new Function<ContactList, ContactListDto>() {
            @Override
            public ContactListDto apply(ContactList arg0) {
                return new ContactListDto(arg0);
            }
        };
    }
}
