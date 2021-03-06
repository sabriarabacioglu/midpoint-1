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

package com.evolveum.midpoint.notifications.api.events;

import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.LightweightIdentifier;
import com.evolveum.midpoint.xml.ns._public.common.common_3.*;

import javax.xml.namespace.QName;

import java.util.Map;

/**
 * @author mederly
 */
public interface Event {

    LightweightIdentifier getId();

    boolean isStatusType(EventStatusType eventStatusType);
    boolean isOperationType(EventOperationType eventOperationType);
    boolean isCategoryType(EventCategoryType eventCategoryType);

    boolean isAccountRelated();

    boolean isUserRelated();

    boolean isWorkItemRelated();

    boolean isWorkflowProcessRelated();

    boolean isWorkflowRelated();

    boolean isAdd();

    boolean isModify();

    boolean isDelete();

    boolean isSuccess();

    boolean isAlsoSuccess();

    boolean isFailure();

    boolean isOnlyFailure();

    boolean isInProgress();

    // requester

    SimpleObjectRef getRequester();

    String getRequesterOid();

    void setRequester(SimpleObjectRef requester);

    // requestee

    SimpleObjectRef getRequestee();

    String getRequesteeOid();

    void setRequestee(SimpleObjectRef requestee);

    void createExpressionVariables(Map<QName, Object> variables, OperationResult result);
}
