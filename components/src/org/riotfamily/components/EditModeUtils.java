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
package org.riotfamily.components;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.riotfamily.riot.security.AccessController;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class EditModeUtils {

	public static final String LIVE_MODE_ATTRIBUTE = 
			EditModeUtils.class.getName() + ".liveMode";
	
	private EditModeUtils() {
	}
	
	public static boolean isEditMode(HttpServletRequest request) {
		return AccessController.isAuthenticatedUser() && !isLiveMode(request);
	}
	
	public static boolean isLiveMode(HttpServletRequest request) {
		Boolean liveMode = (Boolean) request.getAttribute(LIVE_MODE_ATTRIBUTE);
		if (liveMode == null) {
			liveMode = Boolean.valueOf(request.getParameter(LIVE_MODE_ATTRIBUTE));
		}
		return liveMode == Boolean.TRUE;
	}
	
	public static boolean isLiveModePreview(HttpServletRequest request) {
		return Boolean.parseBoolean(request.getParameter(LIVE_MODE_ATTRIBUTE));
	}
	
	public static void setLiveMode(HttpServletRequest request, boolean liveMode) {
		if (liveMode) {
			request.setAttribute(LIVE_MODE_ATTRIBUTE, Boolean.TRUE);
		}
		else {
			request.removeAttribute(LIVE_MODE_ATTRIBUTE);
		}
	}

	public static void setLiveMode(HttpSession session, boolean liveMode) {
		if (liveMode) {
			session.setAttribute(LIVE_MODE_ATTRIBUTE, Boolean.TRUE);
		}
		else {
			session.removeAttribute(LIVE_MODE_ATTRIBUTE);
		}
	}
	
	public static void include(HttpServletRequest request, 
			HttpServletResponse response, String url, boolean liveMode) 
			throws ServletException, IOException {
		
		boolean previouslyLive = isLiveMode(request);
		setLiveMode(request, liveMode);
		request.getRequestDispatcher(url).include(request, response);
		setLiveMode(request, previouslyLive);
	}
	
}
