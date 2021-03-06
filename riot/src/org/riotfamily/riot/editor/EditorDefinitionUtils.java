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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.editor;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.riot.dao.ParentChildDao;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.ui.EditorReference;
import org.springframework.util.Assert;

/**
 *
 */
public final class EditorDefinitionUtils {

	private EditorDefinitionUtils() {
	}
	
	public static ListDefinition getListDefinition(
			EditorRepository repository, String editorId) {
		
		EditorDefinition def = repository.getEditorDefinition(editorId);
		return getListDefinition(def);
	}
	
	public static ListDefinition getListDefinition(EditorDefinition def) {
		if (def instanceof ListDefinition) {
			return (ListDefinition) def;
		}
		def = def.getParentEditorDefinition();
		while (def != null) {
			if (def instanceof ListDefinition) {
				return (ListDefinition) def;
			}
			def = def.getParentEditorDefinition();
		}
		return null;
	}
		
	public static ListDefinition getParentListDefinition(EditorDefinition def) {
		if (def == null) {
			return null;
		}
		EditorDefinition parentDef = getListDefinition(def).getParentEditorDefinition();
		while (parentDef != null) {
			if (parentDef instanceof ListDefinition) {
				return (ListDefinition) parentDef;
			}
			parentDef = parentDef.getParentEditorDefinition();
		}
		return null;
	}
	
	public static ListDefinition getRootListDefinition(EditorDefinition def) {
		ListDefinition list = getListDefinition(def);
		ListDefinition parent = list;
		while (parent != null) {
			list = parent;
			parent = getParentListDefinition(parent);
		}
		return list;
	}
	
	public static ListDefinition getNextListDefinition(
			ListDefinition start, ListDefinition destination) {
		
		ListDefinition def = destination;
		ListDefinition parent = getParentListDefinition(def); 
		while (parent != start && parent != null) {
			def = parent;
			parent = getParentListDefinition(def);
		}
		return def;
	}

	public static String getObjectId(EditorDefinition def, Object item) {
		if (item == null) {
			return null;
		}
		ListDefinition listDef = getListDefinition(def);
		if (listDef == null) {
			return null;
		}
		return listDef.getDao().getObjectId(item);
	}
	
	public static Object loadBean(EditorDefinition def, String objectId) {
		ListDefinition listDef = getListDefinition(def);
		return listDef.getDao().load(objectId);
	}

	public static Object getParent(EditorDefinition def, Object bean) {
		ListDefinition listDef = getListDefinition(def);
		RiotDao dao = listDef.getDao();
		if (dao instanceof ParentChildDao) {
			ParentChildDao parentChildDao = (ParentChildDao) dao;
			return parentChildDao.getParent(bean);
		}
		return null;
	}
	
	public static String getParentId(EditorDefinition def, Object bean) {
		Object parent = getParent(def, bean);
		if (parent != null) {
			ListDefinition listDef = getListDefinition(def);
			if (listDef instanceof TreeDefinition) {
				TreeDefinition tree = (TreeDefinition) listDef;
				if (tree.isNode(parent)) {
					return getObjectId(listDef, parent);
				}
			}
			EditorDefinition parentDef = getParentListDefinition(def);
			if (parentDef != null) {
				return getObjectId(parentDef, parent);
			}
		}
		return null;
	}
	
	public static ListDefinition getParentListDefinition(
			EditorDefinition def, String objectId) {
		
		Object parent = getParent(def, loadBean(def, objectId));
		if (parent != null) {
			ListDefinition listDef = getListDefinition(def);
			if (listDef instanceof TreeDefinition) {
				TreeDefinition tree = (TreeDefinition) listDef;
				if (tree.isNode(parent)) {
					return listDef;
				}
			}
		}
		return getParentListDefinition(def);
	}
	
	public static Object loadParent(EditorDefinition def, String parentId) {
		if (parentId != null) {
			ListDefinition listDef = getParentListDefinition(def);
			Assert.notNull(listDef, "No parent ListDefinition found for editor "
					+ def.getId());
			
			return loadBean(listDef, parentId);
		}
		return null;
	}
	
	public static String getListUrl(EditorDefinition def, Object bean, 
			MessageResolver messageResolver) {
		
		EditorReference ref = def.createEditorPath(bean, messageResolver);
		while (!ref.getEditorType().equals("list")) {
			ref = ref.getParent();
		}
		return ref.getEditorUrl();
	}
	
	public static String getListUrl(EditorDefinition def, String objectId,
			String parentId, String parentEditorId, 
			MessageResolver messageResolver) {
		
		EditorReference ref = def.createEditorPath(objectId, parentId, 
				parentEditorId, messageResolver);
		
		while (!ref.getEditorType().equals("list")) {
			ref = ref.getParent();
		}
		return ref.getEditorUrl();
	}
}
