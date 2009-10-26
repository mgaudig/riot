/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.pages.mapping;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.pages.config.SystemPageType;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public final class PageResolver {
	
	public static final String SITE_ATTRIBUTE = PageResolver.class.getName() + ".site";

	public static final String PAGE_ATTRIBUTE = PageResolver.class.getName() + ".page";

	private static final Object NOT_FOUND = new Object();
	
	private PageResolver() {
	}
	
	/**
	 * Returns the first Site that matches the given request. The PathCompleter
	 * is used to strip the servlet mapping from the request URI.
	 * @return The first matching Site, or <code>null</code> if no match is found
	 */
	public static Site getSite(HttpServletRequest request) {
		Object site = request.getAttribute(SITE_ATTRIBUTE);
		if (site == null) {
			site = resolveSite(request);
			exposeSite((Site) site, request);
		}
		if (site == null || site == NOT_FOUND) {
			return null;
		}
		Site result = (Site) site;
		result.refreshIfDetached();
		return result; 
	}

	protected static void exposeSite(Site site, HttpServletRequest request) {
		expose(site, request, SITE_ATTRIBUTE);
	}
	
	/**
	 * Returns the Page for the given request.
	 */
	public static Page getPage(HttpServletRequest request) {
		Object page = request.getAttribute(PAGE_ATTRIBUTE);
		if (page == null) {
			page = resolvePage(request);
			exposePage((Page) page, request);
		}
		if (page == null || page == NOT_FOUND) {
			return null;
		}
		return (Page) page;
	}
	
	public static Page getVirtualPage(Site site, String type, Object arg) {
		return site.getSchema().getVirtualPageType(type).resolve(site, arg);
	}
	
	protected static void exposePage(Page page, HttpServletRequest request) {
		expose(page, request, PAGE_ATTRIBUTE);
	}
	
	/**
	 * Returns the previously resolved Page for the given request.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getPage(HttpServletRequest)} has been invoked before. 
	 */
	public static Page getResolvedPage(HttpServletRequest request) {
		Object page = request.getAttribute(PAGE_ATTRIBUTE);
		return page != NOT_FOUND ? (Page) page : null;
	}
	
	/**
	 * Returns the previously resolved Site for the given request.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getSite(HttpServletRequest)} has been invoked before. 
	 */
	public static Site getResolvedSite(HttpServletRequest request) {
		Object site = request.getAttribute(SITE_ATTRIBUTE);
		return site != NOT_FOUND ? (Site) site : null; 
	}

	private static Site resolveSite(HttpServletRequest request) {
		String hostName = request.getServerName();
		return Site.loadByHostName(hostName);
	}

	private static Page resolvePage(HttpServletRequest request) {
		Site site = getSite(request);
		if (site == null) {
			return null;
		}
		String path = FormatUtils.stripTrailingSlash(ServletUtils.getPathWithinApplication(request));
		String lookupPath = getLookupPath(path);
		Page page = ContentPage.loadBySiteAndPath(site, lookupPath);
		if (page == null) {
			page = resolveVirtualChildPage(site, lookupPath);
		}
		if (page == null 
				|| !(page.isPublished() || AccessController.isAuthenticatedUser())
				|| !site.getSchema().suffixMatches(page, path)) {
			
			return null;
		}
		return page;
	}
	
	private static Page resolveVirtualChildPage(Site site, String lookupPath) {
		for (ContentPage parent : ContentPage.findByTypesAndSite(site.getSchema().getVirtualParents(), site)) {
			if (lookupPath.startsWith(parent.getPath())) {
				SystemPageType parentType = (SystemPageType) site.getSchema().getPageType(parent);
				String tail = lookupPath.substring(parent.getPath().length());
				return parentType.getVirtualChildType().resolve(parent, tail);
			}
		}
		return null;
	}

	public static String getLookupPath(HttpServletRequest request) {
		return getLookupPath(ServletUtils.getPathWithinApplication(request));
	}
	
	public static String getLookupPath(String path) {
		return FormatUtils.stripExtension(path);
	}
	
	private static void expose(Object object, HttpServletRequest request,
			String attributeName) {
		
		if (object == null) {
			object = NOT_FOUND;
		}
		request.setAttribute(attributeName, object);
	}
	
	/**
	 * Resets all internally used attributes.
	 * @param request
	 */
	public static void resetAttributes(HttpServletRequest request) {
		request.removeAttribute(SITE_ATTRIBUTE);
		request.removeAttribute(PAGE_ATTRIBUTE);
	}
}
