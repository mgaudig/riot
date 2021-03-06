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
package org.riotfamily.pages.dao;

import java.util.List;
import java.util.Locale;

import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageAlias;
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.dao.RiotDaoException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

/**
 * DAO interface for {@link Page pages} and {@link PageAlias aliases}.
 * <p>
 * Implementors should extend {@link AbstractPageDao} instead of implementing
 * this interface directly.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface PageDao {

	/**
	 * Loads the Page with the given id.
	 */
	public Page loadPage(Long id);

	/**
	 * Loads the PageNode with the given id.
	 */
	public PageNode loadPageNode(Long id);

	public Site findSiteByLocale(Locale locale);
	
	public Site findSite(String hostName, String path);
	
	public Site findSiteWithProperty(String name, Object value);
		
	/**
	 * Returns the Page with the given site and path, or <code>null</code> if
	 * no such page exists.
	 */
	public Page findPage(Site site, String path);

	/**
	 * Returns the PageAlias with the given site and path, or <code>null</code> if
	 * no such alias exists.
	 */
	public PageAlias findPageAlias(Site site, String path);

	/**
	 * Returns the PageNode with the given pageType, or <code>null</code> if
	 * no such node exists.
	 * @throws IncorrectResultSizeDataAccessException if more than one node
	 * 		   exists with the given pageType
	 */
	public PageNode findNodeOfType(String pageType);

	/**
	 * Returns the Page with the given pageType and site,
	 * or <code>null</code> if no such page exists.
	 * @throws IncorrectResultSizeDataAccessException if more than one page
	 * 		   exists with the given pageType
	 */
	public Page findPageOfType(String pageType, Site site);

	/**
	 * Returns all pages with the given pageType and site,
	 * or an empty list if no page is found.
	 */
	public List<Page> findPagesOfType(String pageType, Site site);

	/**
	 * 
	 */
	public List<String> getWildcardPaths(Site site);
	
	/**
	 * Returns the root node.
	 */
	public PageNode getRootNode();

	public void saveNode(PageNode node);

	public void savePage(Site site, Page page);

	public void savePage(Page parent, Page child);

	public Page addTranslation(Page page, Site site);
	
	public Page addTranslation(PageNode node, Site site, String pathComponent);

	public void refreshPageIfDetached(Page page);

	public Page updatePage(Page page);

	public void publishPage(Page page);
	
	public void unpublishPage(Page page);
	
	public void publishPageProperties(Page page);
	
	public void discardPageProperties(Page page);
	
	public void deletePage(Page page);

	public void deleteAlias(Page page);
	
	public void updateNode(PageNode node);

	public void moveNode(PageNode node, PageNode newParent);

	public Site loadSite(Long id);

	/**
	 * Returns the first site returned by listSites().
	 */
	public Site getDefaultSite();

	/**
	 * Returns all sites.
	 */
	public List<Site> listSites();
	
	public void saveSite(Site site);

	public void refreshSiteIfDetached(Site site);

	public Site updateSite(Site site);

	public void deleteSite(Site site);
	
	/**
	 * Checks that the page is valid for the given parent and throws a
	 * RiotDaoException otherwise.
	 */
	public void validate(PageNode parentNode, Page page) throws RiotDaoException;

}
