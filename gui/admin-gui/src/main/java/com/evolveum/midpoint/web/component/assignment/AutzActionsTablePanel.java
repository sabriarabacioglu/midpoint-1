/*
 * Copyright (c) 2010-2013 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package com.evolveum.midpoint.web.component.assignment;

import com.evolveum.midpoint.prism.*;
import com.evolveum.midpoint.prism.delta.ContainerDelta;
import com.evolveum.midpoint.prism.delta.ItemDelta;
import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.prism.path.ItemPathSegment;
import com.evolveum.midpoint.prism.query.InOidFilter;
import com.evolveum.midpoint.prism.query.NotFilter;
import com.evolveum.midpoint.prism.query.ObjectFilter;
import com.evolveum.midpoint.prism.query.ObjectQuery;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.util.logging.LoggingUtils;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.web.component.data.TablePanel;
import com.evolveum.midpoint.web.component.data.column.CheckBoxHeaderColumn;
import com.evolveum.midpoint.web.component.data.column.LinkColumn;
import com.evolveum.midpoint.web.component.dialog.ConfirmationDialog;
import com.evolveum.midpoint.web.component.menu.cog.InlineMenu;
import com.evolveum.midpoint.web.component.menu.cog.InlineMenuItem;
import com.evolveum.midpoint.web.component.menu.cog.InlineMenuItemAction;
import com.evolveum.midpoint.web.component.util.ListDataProvider;
import com.evolveum.midpoint.web.component.util.LoadableModel;
import com.evolveum.midpoint.web.component.util.SimplePanel;
import com.evolveum.midpoint.web.component.assignment.AssignableAuthActionsPopup;
import com.evolveum.midpoint.web.page.admin.users.dto.UserDtoStatus;
import com.evolveum.midpoint.web.page.admin.users.dto.UserListItemDto;
import com.evolveum.midpoint.xml.ns._public.common.common_3.*;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.xml.namespace.QName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *  @author arda
 * */
public class AutzActionsTablePanel extends SimplePanel<List<AutzActionsTableDto>>{

    private static final Trace LOGGER = TraceManager.getTrace(AutzActionsTablePanel.class);

    private static final String DOT_CLASS = AutzActionsTablePanel.class.getName() + ".";
    private static final String OPERATION_LOAD_AUTZACTIONS = DOT_CLASS + "loadAutzActions";
    private static final String OPERATION_LOAD_AUTZACTION = DOT_CLASS + "loadAutzAction";

    private static final String ID_AUTZACTIONS = "autzActions";
    // private static final String ID_CHECK_ALL = "autzActionsCheckAll";
    private static final String ID_HEADER = "autzActionsHeader";
    private static final String ID_MENU = "autzActionsMenu";
    private static final String ID_LIST = "autzActionList";
    // private static final String ID_ROW = "autzActionEditor";
    private static final String ID_MODAL_ACTIONS_ASSIGN = "autzActionsPopup";
    private static final String ID_MODAL_DELETE_AUTHORIZATION = "deleteAutzPopup";
 

    public AutzActionsTablePanel(String id, IModel<List<AutzActionsTableDto>> model){
        super(id, model);

        initPanelLayout();
    }


    public String getExcludeOid(){
        return null;
    }

//    private PrismObject getReference(ObjectReferenceType ref, OperationResult result) {
//        OperationResult subResult = result.createSubresult(OPERATION_LOAD_ASSIGNMENT);
//        subResult.addParam("targetRef", ref.getOid());
//        PrismObject target = null;
//        try {
//            Task task = getPageBase().createSimpleTask(OPERATION_LOAD_ASSIGNMENT);
//            Class type = ObjectType.class;
//            if (ref.getType() != null) {
//                type = getPageBase().getPrismContext().getSchemaRegistry().determineCompileTimeClass(ref.getType());
//            }
//            target = getPageBase().getModelService().getObject(type, ref.getOid(), null, task, subResult);
//            subResult.recordSuccess();
//        } catch (Exception ex) {
//            LoggingUtils.logException(LOGGER, "Couldn't get assignment target ref", ex);
//            subResult.recordFatalError("Couldn't get assignment target ref.", ex);
//        }
//
//        return target;
//    }


    private void initPanelLayout(){
    	ListDataProvider lp = new ListDataProvider(this,getModel());
    	
    	List<InlineMenuItem> items = new ArrayList<InlineMenuItem>();
    	
    	initModalWindows();
    	
    	InlineMenu authorizationsMenu = new InlineMenu(ID_MENU, new Model((Serializable)items));
    	this.add(authorizationsMenu);
    	
    	InlineMenuItem item = new InlineMenuItem(createStringResource("AutzActionsTablePanel.menu.assign"),
                 new InlineMenuItemAction(){

                     @Override
                     public void onClick(AjaxRequestTarget target){
                    	 showAssignableAuthorizationPopupPerformed(target);
                         
                     }
                 });
         items.add(item);
         
         items.add(new InlineMenuItem());
         
         item = new InlineMenuItem(createStringResource("AutzActionsTablePanel.menu.unassign"),
                 new InlineMenuItemAction(){

                     @Override
                     public void onClick(AjaxRequestTarget target){
                         showAuthorizationPopupPerformed(target);
                     }
                 });
         items.add(item);
    	
    	
    	List<IColumn<AutzActionsTableDto, String>> columns = new ArrayList<IColumn<AutzActionsTableDto, String>>();
    	
    	columns.add(new CheckBoxHeaderColumn());		
    	columns.add(new PropertyColumn<AutzActionsTableDto,String>(createStringResource("AutzActionsTablePanel.column.aname"),"authName"));
    	columns.add(new PropertyColumn<AutzActionsTableDto,String>(createStringResource("AutzActionsTablePanel.column.aURI"),"authURI"));
    		
    	TablePanel roleAuthorizations = new TablePanel(ID_AUTZACTIONS,lp,columns);
    	roleAuthorizations.setOutputMarkupId(true); //AJAX refresh 
    	add(roleAuthorizations);	
    	
    }
    
    private void initModalWindows(){
    	ModalWindow assignActionsWindow = createModalWindow(ID_MODAL_ACTIONS_ASSIGN,
                createStringResource("AutzActionsTablePanel.modal.title.selectActions"), 1100, 520);
    	assignActionsWindow.setContent(new AssignableAuthActionsPopup(assignActionsWindow.getContentId()){ 
    		@Override
   			protected void addPerformed(AjaxRequestTarget target, List<AutzActionsTableDto> selected){
    				addSelectedAssignableActionsPerformed(target, selected);
			}
       		       	 	 
        });
    	add(assignActionsWindow);
    	
    	ModalWindow deleteDialog = new ConfirmationDialog(ID_MODAL_DELETE_AUTHORIZATION,
                createStringResource("AutzActionsTablePanel.modal.title.confirmDeletion"),
                new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return createStringResource("AutzActionsTablePanel.modal.message.delete",
                                getSelectedAuthorizations().size()).getString();
                    }
                }) {

            		@Override
            		public void yesPerformed(AjaxRequestTarget target){
            		close(target);
            		deleteAuthorizationConfirmedPerformed(target, getSelectedAuthorizations());
            }
        };
        add(deleteDialog);
    }
    
    
     
   
    private List<AutzActionsTableDto> getSelectedAuthorizations(){
        List<AutzActionsTableDto> selected = new ArrayList<AutzActionsTableDto>();

        List<AutzActionsTableDto> all = getModel().getObject();

        for(AutzActionsTableDto dto: all){
            if(dto.isSelected()){
                selected.add(dto);
            }
        }

        return selected;
    }
    
    private void deleteAuthorizationConfirmedPerformed(AjaxRequestTarget target, List<AutzActionsTableDto> toDelete){
        List<AutzActionsTableDto> auths = getModel().getObject();
        auths.removeAll(toDelete);
        
        
        target.add(getPageBase().getFeedbackPanel(), get(ID_AUTZACTIONS));
    }
    
    private void showAuthorizationPopupPerformed(AjaxRequestTarget target){
    	ModalWindow mod = (ModalWindow)get(ID_MODAL_DELETE_AUTHORIZATION);
    	mod.show(target);
    	
    }
    
  private void showModalWindow(String id, AjaxRequestTarget target){
	  ModalWindow window = (ModalWindow) get(id);
	  window.show(target);
  }
    
  
  private void addSelectedAssignableActionsPerformed(AjaxRequestTarget target, List<AutzActionsTableDto> toAdd){
      ModalWindow window = (ModalWindow) get(ID_MODAL_ACTIONS_ASSIGN);
      window.close(target);

      if(toAdd.isEmpty()){
          warn(getString("AutzActionsTablePanel.message.noAssignmentSelected"));
          target.add(getPageBase().getFeedbackPanel());
          return;
      }
      else{
    	  List<AutzActionsTableDto> authActions = getModel().getObject();
    	  authActions.addAll(toAdd);
    	  target.add(getPageBase().getFeedbackPanel(), get(ID_AUTZACTIONS));
      }
      
  }
  
    
  private void showAssignableAuthorizationPopupPerformed(AjaxRequestTarget target){
      ModalWindow modal = (ModalWindow) get(ID_MODAL_ACTIONS_ASSIGN);
      AssignableAuthActionsPopup  content = (AssignableAuthActionsPopup)modal.get(modal.getContentId());
      showModalWindow(ID_MODAL_ACTIONS_ASSIGN, target);
  }

  
  public PrismContainer createAuthorizationsContainer(PrismContext prismContext) throws SchemaException {
	  List<AutzActionsTableDto> list = getModel().getObject();
	  
	  //PrismContainer c = PrismContainer.newInstance(prismContext, AuthorizationType.COMPLEX_TYPE);
	  PrismObjectDefinition objDef = prismContext.getSchemaRegistry().findObjectDefinitionByCompileTimeClass(RoleType.class);
      PrismContainerDefinition cDef = objDef.findContainerDefinition(RoleType.F_AUTHORIZATION);
      PrismContainer c = cDef.instantiate();
	  
	  for (AutzActionsTableDto dto : list) {
		  PrismContainerValue value = c.createNewValue();
		  PrismProperty action = value.createProperty(AuthorizationType.F_ACTION);
		  action.addRealValue(dto.getAuthURI());
	  }
	  
	  return c;
  }
}
    
  
//        final WebMarkupContainer assignments = new WebMarkupContainer(ID_ASSIGNMENTS);
//        assignments.setOutputMarkupId(true);
//        add(assignments);
//
//        Label label = new Label(ID_HEADER, labelModel);
//        assignments.add(label);
//
//        InlineMenu assignmentMenu = new InlineMenu(ID_MENU, new Model((Serializable) createAssignmentMenu()));
//        assignments.add(assignmentMenu);
//
//        ListView<AssignmentEditorDto> list = new ListView<AssignmentEditorDto>(ID_LIST, assignmentModel) {
//
//            @Override
//                protected void populateItem(ListItem<AssignmentEditorDto> item) {
//                AssignmentEditorPanel editor = new AssignmentEditorPanel(ID_ROW, item.getModel());
//                item.add(editor);
//            }
//        };
//        list.setOutputMarkupId(true);
//        assignments.add(list);
//
//        AjaxCheckBox checkAll = new AjaxCheckBox(ID_CHECK_ALL, new Model()) {
//
//            @Override
//            protected void onUpdate(AjaxRequestTarget target) {
//                List<AssignmentEditorDto> assignmentEditors = assignmentModel.getObject();
//
//                for(AssignmentEditorDto dto: assignmentEditors){
//                    dto.setSelected(this.getModelObject());
//                }
//
//                target.add(assignments);
//            }
//        };
//        assignments.add(checkAll);
//
//        initModalWindows();
    

//    private void initModalWindows(){
//        ModalWindow assignWindow = createModalWindow(ID_MODAL_ASSIGN,
//                createStringResource("AssignmentTablePanel.modal.title.selectAssignment"), 1100, 520);
//        assignWindow.setContent(new AssignablePopupContent(assignWindow.getContentId()){
//
//            @Override
//            protected void addPerformed(AjaxRequestTarget target, List<ObjectType> selected){
//                addSelectedAssignablePerformed(target, selected);
//            }
//
//            @Override
//            public ObjectQuery getProviderQuery(){
//                if(getExcludeOid() == null){
//                    return null;
//                } else {
//                    ObjectQuery query = new ObjectQuery();
//                    List<String> oids = new ArrayList<String>();
//                    oids.add(getExcludeOid());
//
//                    ObjectFilter oidFilter = InOidFilter.createInOid(oids);
//                    query.setFilter(NotFilter.createNot(oidFilter));
//                    return query;
//                }
//            }
//        });
//        add(assignWindow);
//
//        ModalWindow deleteDialog = new ConfirmationDialog(ID_MODAL_DELETE_ASSIGNMENT,
//                createStringResource("AssignmentTablePanel.modal.title.confirmDeletion"),
//                new AbstractReadOnlyModel<String>() {
//
//                    @Override
//                    public String getObject() {
//                        return createStringResource("AssignmentTablePanel.modal.message.delete",
//                                getSelectedAssignments().size()).getString();
//                    }
//                }) {
//
//            @Override
//            public void yesPerformed(AjaxRequestTarget target){
//                close(target);
//                deleteAssignmentConfirmedPerformed(target, getSelectedAssignments());
//            }
//        };
//        add(deleteDialog);
//    }
//
//    private List<InlineMenuItem> createAssignmentMenu(){
//        List<InlineMenuItem> items = new ArrayList<InlineMenuItem>();
//
//        InlineMenuItem item = new InlineMenuItem(createStringResource("AssignmentTablePanel.menu.assign"),
//                new InlineMenuItemAction(){
//
//                    @Override
//                    public void onClick(AjaxRequestTarget target){
//                        showAssignablePopupPerformed(target, ResourceType.class);
//                    }
//                });
//        items.add(item);
//
//        item = new InlineMenuItem(createStringResource("AssignmentTablePanel.menu.assignRole"),
//                new InlineMenuItemAction(){
//
//                    @Override
//                    public void onClick(AjaxRequestTarget target){
//                        showAssignablePopupPerformed(target, RoleType.class);
//                    }
//                });
//        items.add(item);
//
//        item = new InlineMenuItem(createStringResource("AssignmentTablePanel.menu.assignOrg"),
//                new InlineMenuItemAction(){
//
//                    @Override
//                    public void onClick(AjaxRequestTarget target){
//                        showAssignablePopupPerformed(target, OrgType.class);
//                    }
//                });
//        items.add(item);
//
//        items.add(new InlineMenuItem());
//
//        item = new InlineMenuItem(createStringResource("AssignmentTablePanel.menu.unassign"), new InlineMenuItemAction() {
//
//            @Override
//            public void onClick(AjaxRequestTarget target) {
//                deleteAssignmentPerformed(target);
//            }
//        });
//        items.add(item);
//
//        return items;
//    }

//    private List<AssignmentEditorDto> getSelectedAssignments(){
//        List<AssignmentEditorDto> selected = new ArrayList<AssignmentEditorDto>();
//
//        List<AssignmentEditorDto> all = assignmentModel.getObject();
//
//        for(AssignmentEditorDto dto: all){
//            if(dto.isSelected()){
//                selected.add(dto);
//            }
//        }
//
//        return selected;
//    }
//
//    private void showModalWindow(String id, AjaxRequestTarget target){
//        ModalWindow window = (ModalWindow) get(id);
//        window.show(target);
//    }
//
//    private void showAssignablePopupPerformed(AjaxRequestTarget target, Class<? extends ObjectType> type){
//        ModalWindow modal = (ModalWindow) get(ID_MODAL_ASSIGN);
//        AssignablePopupContent content = (AssignablePopupContent)modal.get(modal.getContentId());
//        content.setType(type);
//        showModalWindow(ID_MODAL_ASSIGN, target);
//    }
//
//    private void deleteAssignmentPerformed(AjaxRequestTarget target){
//        List<AssignmentEditorDto> selected = getSelectedAssignments();
//
//        if(selected.isEmpty()){
//            warn(getString("AssignmentTablePanel.message.noAssignmentSelected"));
//            target.add(getPageBase().getFeedbackPanel());
//            return;
//        }
//
//        showModalWindow(ID_MODAL_DELETE_ASSIGNMENT, target);
//    }
//
//    private void deleteAssignmentConfirmedPerformed(AjaxRequestTarget target, List<AssignmentEditorDto> toDelete){
//        List<AssignmentEditorDto> assignments = assignmentModel.getObject();
//
//        for(AssignmentEditorDto assignment: toDelete){
//            if(UserDtoStatus.ADD.equals(assignment.getStatus())){
//                assignments.remove(assignment);
//            } else {
//                assignment.setStatus(UserDtoStatus.DELETE);
//                assignment.setSelected(false);
//            }
//        }
//
//        target.add(getPageBase().getFeedbackPanel(), get(ID_ASSIGNMENTS));
//    }
//
//    private void addSelectedAssignablePerformed(AjaxRequestTarget target, List<ObjectType> newAssignments){
//        ModalWindow window = (ModalWindow) get(ID_MODAL_ASSIGN);
//        window.close(target);
//
//        if(newAssignments.isEmpty()){
//            warn(getString("AssignmentTablePanel.message.noAssignmentSelected"));
//            target.add(getPageBase().getFeedbackPanel());
//            return;
//        }
//
//        List<AssignmentEditorDto> assignments = assignmentModel.getObject();
//
//        for(ObjectType object: newAssignments){
//            try {
//
//                if(object instanceof ResourceType){
//                    addSelectedResourceAssignPerformed((ResourceType) object);
//                    continue;
//                }
//
//                AssignmentEditorDtoType aType = AssignmentEditorDtoType.getType(object.getClass());
//
//                ObjectReferenceType targetRef = new ObjectReferenceType();
//                targetRef.setOid(object.getOid());
//                targetRef.setType(aType.getQname());
//
//                AssignmentType assignment = new AssignmentType();
//                assignment.setTargetRef(targetRef);
//
//                AssignmentEditorDto dto = new AssignmentEditorDto(object, aType, UserDtoStatus.ADD, assignment);
//                dto.setMinimized(false);
//                dto.setShowEmpty(true);
//
//                assignments.add(dto);
//            } catch (Exception e){
//                error(getString("AssignmentTablePanel.message.couldntAssignObject", object.getName(), e.getMessage()));
//                LoggingUtils.logException(LOGGER, "Couldn't assign object", e);
//            }
//        }
//
//        target.add(getPageBase().getFeedbackPanel(), get(ID_ASSIGNMENTS));
//    }
//
//    private void addSelectedResourceAssignPerformed(ResourceType resource) {
//        AssignmentType assignment = new AssignmentType();
//        ConstructionType construction = new ConstructionType();
//        assignment.setConstruction(construction);
//
//        try {
//            getPageBase().getPrismContext().adopt(assignment, UserType.class, new ItemPath(UserType.F_ASSIGNMENT));
//        } catch (SchemaException e) {
//            error(getString("Could not create assignment", resource.getName(), e.getMessage()));
//            LoggingUtils.logException(LOGGER, "Couldn't create assignment", e);
//            return;
//        }
//
//        construction.setResource(resource);
//
//        List<AssignmentEditorDto> assignments = assignmentModel.getObject();
//        AssignmentEditorDto dto = new AssignmentEditorDto(resource, AssignmentEditorDtoType.ACCOUNT_CONSTRUCTION,
//                UserDtoStatus.ADD, assignment);
//        assignments.add(dto);
//
//        dto.setMinimized(false);
//        dto.setShowEmpty(true);
//    }
//
//    public void handleAssignmentsWhenAdd(PrismObject<T> object, PrismContainerDefinition assignmentDef,
//                                          List<AssignmentType> objectAssignments) throws SchemaException{
//
//        List<AssignmentEditorDto> assignments = assignmentModel.getObject();
//        for (AssignmentEditorDto assDto : assignments) {
//            if (!UserDtoStatus.ADD.equals(assDto.getStatus())) {
//                warn(getString("AssignmentTablePanel.message.illegalAssignmentState", assDto.getStatus()));
//                continue;
//            }
//
//            AssignmentType assignment = new AssignmentType();
//            PrismContainerValue value = assDto.getNewValue();
//            assignment.setupContainerValue(value);
//            value.applyDefinition(assignmentDef, false);
//            objectAssignments.add(assignment.clone());
//
//            // todo remove this block [lazyman] after model is updated - it has
//            // to remove resource from accountConstruction
//            removeResourceFromAccConstruction(assignment);
//        }
//    }
//
//    public ContainerDelta handleAssignmentDeltas(ObjectDelta<T> userDelta, PrismContainerDefinition def, QName assignmentPath)
//            throws SchemaException {
//        ContainerDelta assDelta = new ContainerDelta(new ItemPath(), assignmentPath, def);
//
//        //PrismObject<OrgType> org = (PrismObject<OrgType>)getModel().getObject().getAssignmentParent();
//        //PrismObjectDefinition orgDef = org.getDefinition();
//        //PrismContainerDefinition assignmentDef = def.findContainerDefinition(assignmentPath);
//
//        List<AssignmentEditorDto> assignments = assignmentModel.getObject();
//        for (AssignmentEditorDto assDto : assignments) {
//            PrismContainerValue newValue = assDto.getNewValue();
//            switch (assDto.getStatus()) {
//                case ADD:
//                    newValue.applyDefinition(def, false);
//                    assDelta.addValueToAdd(newValue.clone());
//                    break;
//                case DELETE:
//                    PrismContainerValue oldValue = assDto.getOldValue();
//                    oldValue.applyDefinition(def);
//                    assDelta.addValueToDelete(oldValue.clone());
//                    break;
//                case MODIFY:
//                    if (!assDto.isModified()) {
//                        LOGGER.trace("Assignment '{}' not modified.", new Object[]{assDto.getName()});
//                        continue;
//                    }
//
//                    handleModifyAssignmentDelta(assDto, def, newValue, userDelta);
//                    break;
//                default:
//                    warn(getString("pageUser.message.illegalAssignmentState", assDto.getStatus()));
//            }
//        }
//
//        if (!assDelta.isEmpty()) {
//            userDelta.addModification(assDelta);
//        }
//
//        // todo remove this block [lazyman] after model is updated - it has to
//        // remove resource from accountConstruction
//        Collection<PrismContainerValue> values = assDelta.getValues(PrismContainerValue.class);
//        for (PrismContainerValue value : values) {
//            AssignmentType ass = new AssignmentType();
//            ass.setupContainerValue(value);
//            removeResourceFromAccConstruction(ass);
//        }
//
//        return assDelta;
//    }
//
//    private void handleModifyAssignmentDelta(AssignmentEditorDto assDto, PrismContainerDefinition assignmentDef,
//                                             PrismContainerValue newValue, ObjectDelta<T> userDelta) throws SchemaException {
//        LOGGER.debug("Handling modified assignment '{}', computing delta.", new Object[]{assDto.getName()});
//
//        PrismValue oldValue = assDto.getOldValue();
//        Collection<? extends ItemDelta> deltas = oldValue.diff(newValue);
//
//        for (ItemDelta delta : deltas) {
//            ItemPath deltaPath = delta.getPath().rest();
//            ItemDefinition deltaDef = assignmentDef.findItemDefinition(deltaPath);
//
//            delta.setParentPath(joinPath(oldValue.getPath(), delta.getPath().allExceptLast()));
//            delta.applyDefinition(deltaDef);
//
//            userDelta.addModification(delta);
//        }
//    }
//
//    private ItemPath joinPath(ItemPath path, ItemPath deltaPath) {
//        List<ItemPathSegment> newPath = new ArrayList<ItemPathSegment>();
//
//        ItemPathSegment firstDeltaSegment = deltaPath != null ? deltaPath.first() : null;
//        if (path != null) {
//            for (ItemPathSegment seg : path.getSegments()) {
//                if (seg.equals(firstDeltaSegment)) {
//                    break;
//                }
//                newPath.add(seg);
//            }
//        }
//        if (deltaPath != null) {
//            newPath.addAll(deltaPath.getSegments());
//        }
//
//        return new ItemPath(newPath);
//    }
//
//    /**
//     * remove this method after model is updated - it has to remove resource
//     * from accountConstruction
//     */
//    @Deprecated
//    private void removeResourceFromAccConstruction(AssignmentType assignment) {
//        ConstructionType accConstruction = assignment.getConstruction();
//        if (accConstruction == null || accConstruction.getResource() == null) {
//            return;
//        }
//
//        ObjectReferenceType ref = new ObjectReferenceType();
//        ref.setOid(assignment.getConstruction().getResource().getOid());
//        ref.setType(ResourceType.COMPLEX_TYPE);
//        assignment.getConstruction().setResourceRef(ref);
//        assignment.getConstruction().setResource(null);
//    }

