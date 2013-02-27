/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

	/** The precompiled Javascript cache. */
	private Map<String, Script> cache;

	/** The local Rhino context. */
	private Context cx;
	
	private JndiServices jndiService;

	@SuppressWarnings("deprecation")
	public JScriptEvaluator() {
		cache = new HashMap<String, Script>();
		// When removing 1.5 compatibility prefer enterContext() method
		cx = new ContextFactory().enter();
	}
	
	public JndiServices getJndiService() {
		return jndiService;
	}

	public void setJndiService(JndiServices jndiService) {
		this.jndiService = jndiService;
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
	public String evalToString(String expression,
					Map<String, Object> params) {
		Object result = instanceEval(expression, params);

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
	public Map<String, List<String>> evalToEntryMap(String expression,
					Map<String, Object> params) {
		Object result = instanceEval(expression, params);
		try {
			return (Map<String, List<String>>) Context.jsToJava(result, Map.class);
		} catch (Exception e) {
			LOGGER.error(e.toString());
		} // try next approach
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<String> evalToStringList(String expression,
					Map<String, Object> params) {
		Object result = instanceEval(expression, params);

		// First try to convert to Array, else to List, and finally to String
		try {
			Object[] resultsRealArray = (Object[]) Context.jsToJava(result, Object[].class);
			List<String> resultsArray = new ArrayList<String>();
			for (Object resultValue : resultsRealArray) {
				resultsArray.add(resultValue.toString());
			}
			return resultsArray;
		} catch (Exception e) {
			LOGGER.debug(e.toString());
		} // try next approach

		try {
			return (List<String>) Context.jsToJava(result, List.class);
		} catch (Exception e) {
			LOGGER.debug(e.toString());
		} // try next approach

		List<String> resultsArray = new ArrayList<String>();
		String resultAsString = Context.toString(result);
		if (resultAsString != null && resultAsString.length() > 0) {
			resultsArray.add(resultAsString);
		}
		return resultsArray;
	}

	public Boolean evalToBoolean(String expression, Map<String, Object> params) {
		return Context.toBoolean(instanceEval(expression, params));
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
	private Object instanceEval(String expression, Map<String, Object> params) {
		Script script = null;
		Scriptable scope = cx.initStandardObjects();

		Map<String, Object> localParams = new HashMap<String, Object>();
		if (params != null) {
			localParams.putAll(params);
		}

		/* Allow to have shorter names for function in the package org.lsc.utils.directory */
		String expressionImport = "with (new JavaImporter(Packages.org.linagora.linshare.ldap)) {" + expression + "}";

		if (cache.containsKey(expressionImport)) {
			script = cache.get(expressionImport);
			LOGGER.debug("expressionImport (Cache size: " + cache.size() +"): USING CACHE: " + expressionImport);
		} else {
			script = cx.compileString(expressionImport, "<cmd>", 1, null);
			cache.put(expressionImport, script);
			LOGGER.debug("expressionImport (Cache size: " + cache.size() +"): CREATION: " + expressionImport);
		}
		

		// add LDAP interface for destination if necessary
		if (expression.contains("ldap.") && !localParams.containsKey("ldap")) {
			ScriptableJndiServices dstSjs = new ScriptableJndiServices();
			dstSjs.setJndiServices(jndiService);
			localParams.put("ldap", dstSjs);
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
}
