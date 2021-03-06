/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.view;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.filter.ResourceStamper;
import org.riotfamily.common.web.mapping.HandlerUrlResolver;
import org.riotfamily.common.web.view.MacroHelperFactory;
import org.riotfamily.website.hyphenate.RiotHyphenator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CommonMacroHelperFactory implements MacroHelperFactory, 
		ApplicationContextAware, ServletContextAware {

	private ApplicationContext applicationContext;

	private ServletContext servletContext;
	
	private ResourceStamper stamper;
	
	private HandlerUrlResolver handlerUrlResolver;
	
	private RiotHyphenator hyphenator;
	
	private boolean compressResources = false;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public void setStamper(ResourceStamper stamper) {
		this.stamper = stamper;
	}
	
	public void setHandlerUrlResolver(HandlerUrlResolver handlerUrlResolver) {
		this.handlerUrlResolver = handlerUrlResolver;
	}
	
	public void setHyphenator(RiotHyphenator hyphenator) {
		this.hyphenator = hyphenator;
	}

	public void setCompressResources(boolean compressResources) {
		this.compressResources = compressResources;
	}
	
	public Object createMacroHelper(HttpServletRequest request, 
			HttpServletResponse response, Map<String, ?> model) {
		
		return new CommonMacroHelper(applicationContext, request, response, 
				servletContext, stamper, handlerUrlResolver, hyphenator, 
				compressResources);
	}

}
