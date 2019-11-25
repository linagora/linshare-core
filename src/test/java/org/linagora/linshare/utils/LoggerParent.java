package org.linagora.linshare.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is temporary class to avoid write logger manually on all class after
 * remove inheritence from JunitAbstractClass. It will be removed and replaced
 * by a custom class that allows to log before and after each test
 *
 */
public abstract class LoggerParent {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

}
