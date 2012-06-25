package org.linagora.linshare.core.service;

public interface PasswordService {


	/**
     * generate the password of a guest or password for a secure URL (SHA1PRNG algorithm)
     * @return password in plain text
     */
    public String generatePassword();

}