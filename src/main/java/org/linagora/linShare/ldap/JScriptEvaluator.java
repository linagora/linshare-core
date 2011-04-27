/*
 ****************************************************************************
 * Ldap Synchronization Connector provides tools to synchronize
 * electronic identities from a list of data sources including
 * any database with a JDBC connector, another LDAP directory,
 * flat files...
 *
 *                  ==LICENSE NOTICE==
 * 
 * Copyright (c) 2008 - 2011 LSC Project 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:

 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of the LSC Project nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *                  ==LICENSE NOTICE==
 *
 *               (c) 2008 - 2011 LSC Project
 *         Sebastien Bahloul <seb@lsc-project.org>
 *         Thomas Chemineau <thomas@lsc-project.org>
 *         Jonathan Clarke <jon@lsc-project.org>
 *         Remy-Christophe Schermesser <rcs@lsc-project.org>
 ****************************************************************************
 */
package org.linagora.linShare.ldap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.NamingException;

import org.linagora.linShare.core.domain.entities.Domain;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the Rhino Java Script evaluation context.
 * 
 * @author Sebastien Bahloul &lt;seb@lsc-project.org&gt;
 */
public final class JScriptEvaluator {

	// Logger
	private static final Logger LOGGER = LoggerFactory.getLogger(JScriptEvaluator.class);

	/** The private unique instance. */
	private static Map<String,JScriptEvaluator> instances = new HashMap<String,JScriptEvaluator>();

	/** The precompiled Javascript cache. */
	private Map<String, Script> cache;

	/** The local Rhino context. */
	private Context cx;
	
	private String domainId;
	private Domain domain;

	/**
	 * Default private constructor.
	 *
	 * @see #getInstance()
	 */
	private JScriptEvaluator(Domain domain) {
		this.domain = domain;
		this.domainId = domain.getIdentifier();
		cache = new HashMap<String, Script>();
		// When removing 1.5 compatibility prefer enterContext() method
		cx = new ContextFactory().enter();
	}

	/**
	 * Local instance getter.
	 *
	 * @return the instance
	 */
	public static JScriptEvaluator getInstance(Domain domain) {
		String threadName = Thread.currentThread().getName();
		if(instances.get(threadName) == null) {
			instances.put(threadName, new JScriptEvaluator(domain));
		}
		return instances.get(threadName);
	}

	/**
	 * Evaluate your Ecma script expression (manage pre-compiled expressions
	 * cache).
	 *
	 * @param domainId the domain identifer
	 * @param expression
	 *                the expression to eval
	 * @param params
	 *                the keys are the name used in the
	 * @return the evaluation result
	 */
	public static String evalToString(final Domain domain, final String expression,
					final Map<String, Object> params) {
		Object result = getInstance(domain).instanceEval(expression, domain, params);

		if (result == null) {
			return null;
		}

		return Context.toString(result);
	}

	/**
	 * This method will be used when an entry is requested.
	 * 
	 * For example: dn=>["uid=jdoe,ou=Users,dc=foo,dc=bar"],cn=>["J. Doe", "John Doe"], 
	 * sn=>["Doe"], givenName=>["John"],uid=>["jdoe"]
	 * @param domainId the domain identifer
	 * @param expression the javascript expression
	 * @param params context parameters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, List<String>> evalToEntryMap(final Domain domain, final String expression,
					final Map<String, Object> params) {
		Object result = getInstance(domain).instanceEval(expression, domain, params);
		System.out.println(result);
		try {
			return (Map<String, List<String>>) Context.jsToJava(result, Map.class);
		} catch (Exception e) {
		} // try next approach
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<String> evalToStringList(final Domain domain, final String expression,
					final Map<String, Object> params) {
		Object result = getInstance(domain).instanceEval(expression, domain, params);

		// First try to convert to Array, else to List, and finally to String
		try {
			Object[] resultsRealArray = (Object[]) Context.jsToJava(result, Object[].class);
			List<String> resultsArray = new ArrayList<String>();
			for (Object resultValue : resultsRealArray) {
				resultsArray.add(resultValue.toString());
			}
			return resultsArray;
		} catch (Exception e) {
		} // try next approach

		try {
			return (List<String>) Context.jsToJava(result, List.class);
		} catch (Exception e) {
		} // try next approach

		List<String> resultsArray = new ArrayList<String>();
		String resultAsString = Context.toString(result);
		if (resultAsString != null && resultAsString.length() > 0) {
			resultsArray.add(resultAsString);
		}
		return resultsArray;
	}

	public static Boolean evalToBoolean(final Domain domain, final String expression, final Map<String, Object> params) {
		return Context.toBoolean(getInstance(domain).instanceEval(expression, domain, params));
	}

	/**
	 * Local instance evaluation.
	 *
	 * @param expression
	 *                the expression to eval
	 * @param params
	 *                the keys are the name used in the
	 * @return the evaluation result
	 */
	private Object instanceEval(final String expression, final Domain domain,
					final Map<String, Object> params) {
		Script script = null;
		Scriptable scope = cx.initStandardObjects();

		Map<String, Object> localParams = new HashMap<String, Object>();
		if (params != null) {
			localParams.putAll(params);
		}

		/* Allow to have shorter names for function in the package org.lsc.utils.directory */
		String expressionImport =
						"with (new JavaImporter(Packages.org.linagora.linShare.ldap)) {" + expression + "}";

		if (cache.containsKey(expressionImport)) {
			script = cache.get(expressionImport);
		} else {
			script = cx.compileString(expressionImport, "<cmd>", 1, null);
			cache.put(expressionImport, script);
		}

		// add LDAP interface for destination if necessary
		if (expression.contains("ldap.") && !localParams.containsKey("ldap")) {
			ScriptableJndiServices dstSjs = new ScriptableJndiServices();
//			dstSjs.setJndiServices(JndiServices.getInstance(DomainService.getLdapProperties(domainId)));
			try {
				dstSjs.setJndiServices(JndiServices.getInstance(domain.getLdapConnection().toLdapProperties()));
				localParams.put("ldap", dstSjs);
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (Entry<String, Object> entry : localParams.entrySet()) {
			Object jsObj = Context.javaToJS(entry.getValue(), scope);
			ScriptableObject.putProperty(scope, entry.getKey(), jsObj);
		}

		Object ret = null;
		try {
			ret = script.exec(cx, scope);
		} catch (EcmaError e) {
			LOGGER.error(e.toString());
			LOGGER.debug(e.toString(), e);
			return null;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.toString());
			LOGGER.debug(e.toString(), e);
			return null;
		}

		return ret;
	}
	
	public static final boolean auth(final String password, final String dn, final Domain domain) throws NamingException, IOException {
		return JndiServices.getInstance(domain.getLdapConnection().toLdapProperties()).auth(password, dn);
	}
}
