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
package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;
import org.riotfamily.statistics.dao.CachiusStatisticsDao;

public class InvalidateCachiusCacheCommand extends AbstractCommand {
	
	public CommandResult execute(CommandContext context) {
		CachiusStatisticsDao dao = (CachiusStatisticsDao) context.getDao();
		dao.getCachiusStatistics().invalidateAllItems();
		return new RefreshSiblingsResult(context);
	}

}
