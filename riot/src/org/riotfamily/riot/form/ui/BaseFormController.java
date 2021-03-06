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
package org.riotfamily.riot.form.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.ButtonFactory;
import org.riotfamily.forms.controller.FormSubmissionHandler;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.forms.factory.RepositoryFormController;
import org.riotfamily.riot.dao.InvalidPropertyValueException;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.dao.RiotDaoException;
import org.riotfamily.riot.editor.EditorConstants;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.FormReference;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ObjectEditorDefinition;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 */
public abstract class BaseFormController extends RepositoryFormController
		implements FormSubmissionHandler {

	protected static final String EDITOR_DEFINITION_ATTR =
			FormController.class.getName() + ".editorDefinition";

	private static final DefaultTransactionDefinition TRANSACTION_DEFINITION =
			new DefaultTransactionDefinition(
			TransactionDefinition.PROPAGATION_REQUIRED);
	
	private EditorRepository editorRepository;

	private PlatformTransactionManager transactionManager;

	private String viewName = ResourceUtils.getPath(
			BaseFormController.class, "FormView.ftl");

	public BaseFormController(EditorRepository editorRepository,
			FormRepository formRepository,
			PlatformTransactionManager transactionManager) {

		super(formRepository);
		this.editorRepository = editorRepository;
		this.transactionManager = transactionManager;
		ButtonFactory buttonFactory = new ButtonFactory(this);
		buttonFactory.setLabelKey("label.form.button.save");
		buttonFactory.setCssClass("button button-save");
		addButton(buttonFactory);
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * Returns the name of the attribute under which the {@link Form} is
	 * stored in the HTTP session. This implementation returns the
	 * requestURI with the controller's class name as prefix. 
	 */
	protected String getSessionAttribute(HttpServletRequest request) {
		return getClass().getName() + ":" + request.getRequestURI();
	}

	/**
	 * Returns the EditorDefinition for the given request.
	 */
	protected ObjectEditorDefinition getObjectEditorDefinition(HttpServletRequest request) {
		ObjectEditorDefinition editorDefinition = (ObjectEditorDefinition)
				request.getAttribute(EDITOR_DEFINITION_ATTR);

		if (editorDefinition == null) {
			String editorId = (String) request.getAttribute(EditorConstants.EDITOR_ID);
			Assert.notNull(editorId, "An editorId attribute must be set");
			editorDefinition = (ObjectEditorDefinition) editorRepository.getEditorDefinition(editorId);
			Assert.notNull(editorDefinition, "No such editor: " + editorId);
			request.setAttribute(EDITOR_DEFINITION_ATTR, editorDefinition);
		}
		return editorDefinition;
	}

	/**
	 * Returns the id of the object to be edited. 
	 */
	protected String getObjectId(HttpServletRequest request) {
		return (String) request.getAttribute(EditorConstants.OBJECT_ID);
	}

	/**
	 * Returns the id of the parent object. Note: The method returns the
	 * request parameter <em>parentId</em> which is only set when a new object
	 * is created, i.e. {@link #getObjectId(HttpServletRequest)} 
	 * returns <code>null</code>.
	 */
	protected String getParentId(HttpServletRequest request) {
		return request.getParameter(EditorConstants.PARENT_ID);
	}
	
	/**
	 * Returns the parent editor. Note: The method uses the request parameter 
	 * <em>parentEditorId</em> which is only set when a new object is created, 
	 * i.e. {@link #getObjectId(HttpServletRequest)} returns <code>null</code>.
	 */
	protected EditorDefinition getParentEditor(HttpServletRequest request) {
		String id = request.getParameter(EditorConstants.PARENT_EDITOR_ID);
		if (id != null) {
			return editorRepository.getEditorDefinition(id);
		}
		return null;
	}

	/**
	 * Returns the id of the form to be used. This implementation returns
	 * the formId specified by the 
	 * {@link #getObjectEditorDefinition(HttpServletRequest) FormDefinition}.
	 */
	protected String getFormId(HttpServletRequest request) {
		FormReference ref = (FormReference) getObjectEditorDefinition(request);
		return ref.getFormId();
	}
	
	protected Form createForm(HttpServletRequest request) {
		Form form = super.createForm(request);
		FormUtils.setObjectId(form, getObjectId(request));
		FormUtils.setParentId(form, getParentId(request));
		FormUtils.setParentEditor(form, getParentEditor(request));
		FormUtils.setEditorDefinition(form, getObjectEditorDefinition(request));
		return form;
	}

	/**
	 * @see org.riotfamily.forms.factory.RepositoryFormController#getFormBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object getFormBackingObject(HttpServletRequest request) {
		ObjectEditorDefinition editorDefinition = getObjectEditorDefinition(request);
		String objectId = getObjectId(request);
		if (objectId == null) {
			return null;
		}
		return EditorDefinitionUtils.loadBean(editorDefinition, objectId);
	}

	protected Map<String, Object> createModel(Form form, ObjectEditorDefinition formDefinition,
			HttpServletRequest request, HttpServletResponse response) {

		HashMap<String, Object> model = Generics.newHashMap();
		model.put(EditorConstants.EDITOR_ID, formDefinition.getId());
		model.put(EditorConstants.PARENT_EDITOR_ID, FormUtils.getParentEditorId(form));
		model.put(EditorConstants.PARENT_ID, FormUtils.getParentId(form));
		model.put(EditorConstants.OBJECT_ID, FormUtils.getObjectId(form));
		model.put("formId", form.getId());
		return model;
	}

	/**
	 * @see org.riotfamily.forms.controller.AbstractFormController#showForm(org.riotfamily.forms.Form, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected ModelAndView showForm(Form form, HttpServletRequest request,
			HttpServletResponse response) {

		StringWriter sw = new StringWriter();
		renderForm(form, new PrintWriter(sw));
		Map<String, Object> model = createModel(form, getObjectEditorDefinition(request),
				request, response);

		model.put("form", sw.toString());
		model.put("formId", form.getId());
		return new ModelAndView(viewName, model);
	}


	public final ModelAndView handleFormSubmission(Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {
			return handleFormSubmissionInternal(form, request, response);
		}
		catch (InvalidPropertyValueException e) {
			form.getErrors().rejectValue(e.getField(), e.getCode(),
					e.getArguments(), e.getMessage());

			return showForm(form, request, response);
		}
		catch (RiotDaoException e) {
			form.getErrors().reject(e.getCode(), e.getArguments(), e.getMessage());
			return showForm(form, request, response);
		}
	}

	protected ModelAndView handleFormSubmissionInternal(Form form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		ObjectEditorDefinition editorDef = getObjectEditorDefinition(request);
		boolean save = form.isNew();
		Object bean = saveOrUpdate(form, editorDef);
		removeFormFromSession(request);
		if (save) {
			return afterSave(form, bean, editorDef, request, response);
		}
		return afterUpdate(form, bean, editorDef, request, response);
	}
		
	protected Object saveOrUpdate(Form form, ObjectEditorDefinition editor)
			throws Exception {
		
		ListDefinition listDef = EditorDefinitionUtils.getListDefinition(editor);
		RiotDao dao = listDef.getDao();
		Object result;
		
		TransactionStatus status = transactionManager.getTransaction(TRANSACTION_DEFINITION);
		try {
			if (form.isNew()) {
				log.debug("Saving entity ...");
				Object parent = null;
				String parentId = FormUtils.getParentId(form);
				if (parentId != null) {
					String parentEditorId = FormUtils.getParentEditorId(form);
					if (parentEditorId != null) {
						EditorDefinition parentDef = editorRepository.getEditorDefinition(parentEditorId);
						parent = EditorDefinitionUtils.loadBean(parentDef, parentId);
					}
					else {
						parent = EditorDefinitionUtils.loadParent(editor, parentId);
					}
				}
				Object bean = form.populateBackingObject();				
				dao.save(bean, parent);
				FormUtils.setObjectId(form, dao.getObjectId(bean));
				result = bean;
			}
			else {
				log.debug("Updating entity ...");
				Object bean = form.populateBackingObject();
				result = dao.update(bean); 
			}
		}
		catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		}
		transactionManager.commit(status);
		return result;
	}
	
	protected abstract ModelAndView afterSave(Form form, Object bean, ObjectEditorDefinition editorDefinition,
			HttpServletRequest request, HttpServletResponse response);

	protected abstract ModelAndView afterUpdate(Form form, Object bean, ObjectEditorDefinition editorDefinition,
			HttpServletRequest request, HttpServletResponse response);

}
