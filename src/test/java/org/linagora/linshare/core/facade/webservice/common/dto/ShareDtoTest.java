package org.linagora.linshare.core.facade.webservice.common.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.linagora.linshare.core.domain.constants.LinShareTestConstants.TOP_DOMAIN;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;

import java.util.Date;
import java.util.stream.Stream;

public class ShareDtoTest {
	private static final String SHARE_UUID = "UUID";
	private static final String NAME = "name";
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String UID = "uid";
	private static final String MAIL = "mail";

	private static final String NAME_RECIPIENT = "name_recipient";
	private static final String MAIL_RECIPIENT = "mail_recipient";
	private static final String UID_RECIPIENT = "uid_recipient";

	private static final String UID_DOCUMENT = "uid_document";

	private static final DocumentDto DOCUMENT = new DocumentDto();
	private static final UserDto SENDER = new UserDto();
	private static final GenericUserDto RECIPIENT = new GenericUserDto();

	private static final Date DATE_CREATION = new Date();

	private static final Date DATE_MODIFICATION = new Date();

	private static final Date DATE_EXPIRATION = new Date();

	private static final ShareDto SHARE_DTO = new ShareDto();

	static {
		SENDER.setFirstName(FIRST_NAME);
		SENDER.setLastName(LAST_NAME);
		SENDER.setMail(MAIL);
		SENDER.setUuid(UID);
		SENDER.setDomain(TOP_DOMAIN);

		RECIPIENT.setFirstName(NAME_RECIPIENT);
		RECIPIENT.setMail(MAIL_RECIPIENT);
		RECIPIENT.setUuid(UID_RECIPIENT);
		RECIPIENT.setDomain(TOP_DOMAIN);

		DOCUMENT.setUuid(UID_DOCUMENT);
		DOCUMENT.setSize(123456L);

		SHARE_DTO.setUuid(SHARE_UUID);
		SHARE_DTO.setRecipient(RECIPIENT);
		SHARE_DTO.setDocument(DOCUMENT);
		SHARE_DTO.setSender(SENDER);
		SHARE_DTO.setDownloaded(0L);
		SHARE_DTO.setName(NAME);
		SHARE_DTO.setExpirationDate(DATE_EXPIRATION);
		SHARE_DTO.setCreationDate(DATE_CREATION);
		SHARE_DTO.setModificationDate(DATE_MODIFICATION);
	}

	@ParameterizedTest
	@MethodSource("jsonSerializationDeserializationArgs")
	void jsonSerialisationDeserialisation() throws Exception {
		final ObjectMapper objectMapper = new ObjectMapper();
		final String json = objectMapper.writeValueAsString(SHARE_DTO);
		final ShareDto deserializedShareDto = objectMapper.readValue(json, ShareDto.class);
		assertEquals(SHARE_DTO.getUuid(), deserializedShareDto.getUuid());
		assertEquals(SHARE_DTO.getName(), deserializedShareDto.getName());
		assertEquals(SHARE_DTO.getCreationDate(), deserializedShareDto.getCreationDate());
		assertEquals(SHARE_DTO.getModificationDate(), deserializedShareDto.getModificationDate());
		assertEquals(SHARE_DTO.getExpirationDate(), deserializedShareDto.getExpirationDate());
		assertEquals(SHARE_DTO.getSender().getUuid(), deserializedShareDto.getSender().getUuid());
		assertEquals(SHARE_DTO.getRecipient().getMail(), deserializedShareDto.getRecipient().getMail());
		assertEquals(SHARE_DTO.getDocument(), deserializedShareDto.getDocument());
	}

	private static Stream<Arguments> jsonSerializationDeserializationArgs() {

		final String expectedJson = "{\"uuid\":\"" + SHARE_UUID + "\",\"name\":\"" + NAME + "\"," + "\"creationDate\":"
				+ DATE_CREATION.getTime() + ",\"modificationDate\":" + DATE_MODIFICATION.getTime()
				+ ",\"expirationDate\":" + DATE_EXPIRATION.getTime() + ",\"sender\":{\"uuid\":\"" + UID
				+ "\",\"firstName\":\"" + FIRST_NAME + "\"," + "\"lastName\":\"" + LAST_NAME + "\",\"mail\":\"" + MAIL
				+ "\"}," + "\"recipient\":{\"uuid\":\"" + UID_RECIPIENT + "\",\"firstName\":\"" + NAME_RECIPIENT
				+ "\"}," + "\"document\":{\"uuid\":\"" + UID_DOCUMENT + "\"}}";

		return Stream.of(Arguments.of(SHARE_DTO, expectedJson));
	}
}
