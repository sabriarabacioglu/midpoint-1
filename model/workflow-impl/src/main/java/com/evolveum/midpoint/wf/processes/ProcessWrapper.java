/*
 * Copyright (c) 2012 Evolveum
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 *
 * Portions Copyrighted 2012 [name of copyright owner]
 */

package com.evolveum.midpoint.wf.processes;

import com.evolveum.midpoint.model.api.context.ModelContext;
import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.wf.messages.ProcessEvent;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.UserType;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * ProcessWrapper is an interface to (wrapper of) a specific kind of workflow process related
 * to user change approval. Examples of process wrappers:
 *  - AddRoleProcessWrapper
 *  - CreateUserProcessWrapper
 *  - ChangeAttributeXProcessWrapper (X is an attribute of a user)
 *  - ...
 *
 * It plays a role on these occasions:
 *  1) when a change arrives - process wrapper tries to recognize whether the change contains relevant
 *     delta(s); if so, it prepares instruction(s) to start related workflow approval process(es)
 *  2) when a process instance finishes - process wrapper modifies the delta(s) related to particular
 *     process instance and passes them along, to be executed
 *  3) when a user asks about the state of process instance(s) - it prepares that part of the answer
 *     that is specific to individual process
 *
 * @author mederly
 */
public interface ProcessWrapper {


    /**
     * Examines user change and determines whether there are pieces that require (change type specific)
     * approval, for example, if there are roles added.
     *
     * If yes, it takes these deltas out of the original change and prepares instruction(s) to start wf process(es).
     *
     * @param modelContext Original model context (e.g. to be able to get information about whole context of the operation)
     * @param change User change to be examined and modified (as a side effect!)
     * @param task
     * @param result
     * @return list of start process instructions
     */

    List<UserChangeStartProcessInstruction> prepareProcessesToStart(ModelContext<?,?> modelContext, ObjectDelta<UserType> change, Task task, OperationResult result);

    // TODO (after this mark)
    //-------------------------------------------------------------------------------------

    /**
     * Does a specific processing at the end of the workflow. E.g. removes roles that were not approved from the model context.
     *
     * @param context Current context of the model operation.
     * @param event Message from the WfMS.
     * @param task Enclosing task to which results of the process were (and are) written.
     * @param result
     * @return TODO xxxxxx
     */
    void finishProcess(ModelContext context, ProcessEvent event, Task task, OperationResult result);

    String getProcessSpecificDetailsForTask(String instanceId, Map<String, Object> vars);
    String getProcessSpecificDetails(ProcessInstance instance, Map<String, Object> vars, List<org.activiti.engine.task.Task> tasks);
    String getProcessSpecificDetails(HistoricProcessInstance instance, Map<String, Object> vars);


}