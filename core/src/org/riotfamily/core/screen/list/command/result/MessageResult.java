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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Carsten Woelk [cwoelk at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.core.screen.list.command.result;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteMethod;

@DataTransferObject
public class MessageResult implements CommandResult {
	
	String message;
	
	public MessageResult(String message) {
		this.message = message;
	}

	@RemoteMethod
	public String getAction() {
		return "message";
	}
	
	@RemoteMethod
	public String getMessage() {
		return message;
	}
	
}
