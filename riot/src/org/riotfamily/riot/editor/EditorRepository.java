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

import java.util.Map;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.riot.list.ListRepository;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.riotfamily.riot.runtime.RiotRuntimeAware;
import org.springframework.util.Assert;

/**
 *
 */
public class EditorRepository implements RiotRuntimeAware {

	private RiotLog log = RiotLog.get(EditorRepository.class);

	private Map<String, EditorDefinition> editorDefinitions = Generics.newHashMap();

	private GroupDefinition rootGroupDefinition;

	private ListRepository listRepository;

	private FormRepository formRepository;

	private RiotRuntime runtime;
	
	public void setRiotRuntime(RiotRuntime runtime) {
		this.runtime = runtime;
	}
	
	public RiotRuntime getRiotRuntime() {
		return runtime;
	}

	public String getRiotServletPrefix() {
		return runtime.getServletPrefix();
	}
	
	public GroupDefinition getRootGroupDefinition() {
		return this.rootGroupDefinition;
	}

	public void setRootGroupDefinition(GroupDefinition rootGroupDefinition) {
		this.rootGroupDefinition = rootGroupDefinition;
	}

	protected Map<String, EditorDefinition> getEditorDefinitions() {
		return this.editorDefinitions;
	}

	public void addEditorDefinition(EditorDefinition editorDefinition) {
		String id = editorDefinition.getId();
		Assert.notNull(id, "Editor must have an id.");
		EditorDefinition existingEditor = getEditorDefinition(id);
		if (existingEditor != null) {
			log.info("Overwriting editor " + id);
			if (existingEditor.getParentEditorDefinition() instanceof GroupDefinition) {
				GroupDefinition oldGroup = (GroupDefinition) existingEditor.getParentEditorDefinition();
				oldGroup.getChildEditorDefinitions().remove(existingEditor);
			}
		}
		editorDefinitions.put(id, editorDefinition);
	}

	public EditorDefinition getEditorDefinition(String editorId) {
		if (editorId == null) {
			return rootGroupDefinition;
		}
		return editorDefinitions.get(editorId);
	}

	public ListDefinition getListDefinition(String editorId) {
		return (ListDefinition) getEditorDefinition(editorId);
	}

	public FormDefinition getFormDefinition(String editorId) {
		return (FormDefinition) getEditorDefinition(editorId);
	}

	public GroupDefinition getGroupDefinition(String editorId) {
		return (GroupDefinition) getEditorDefinition(editorId);
	}

	public ListRepository getListRepository() {
		return listRepository;
	}

	public void setListRepository(ListRepository listRepository) {
		this.listRepository = listRepository;
	}

	public FormRepository getFormRepository() {
		return this.formRepository;
	}

	public void setFormRepository(FormRepository formRepository) {
		this.formRepository = formRepository;
	}

	/**
	 * Subclasses may overwrite this method to allow the registration of
	 * {@link ConfigurationEventListener ConfigurationEventListeners}.
	 * The default implementation does nothing.
	 * @since 6.5
	 */
	public void addListener(ConfigurationEventListener listener) {
	}

}
