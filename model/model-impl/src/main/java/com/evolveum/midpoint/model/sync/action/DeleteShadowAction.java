/**
 * Copyright (c) 2013 Evolveum
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
package com.evolveum.midpoint.model.sync.action;

import java.util.Map;

import javax.xml.namespace.QName;

import com.evolveum.midpoint.model.lens.LensContext;
import com.evolveum.midpoint.model.lens.LensProjectionContext;
import com.evolveum.midpoint.model.lens.SynchronizationIntent;
import com.evolveum.midpoint.model.sync.Action;
import com.evolveum.midpoint.model.sync.SynchronizationSituation;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.xml.ns._public.common.common_3.FocusType;

/**
 * @author semancik
 *
 */
public class DeleteShadowAction implements Action {

	/* (non-Javadoc)
	 * @see com.evolveum.midpoint.model.sync.Action#handle(com.evolveum.midpoint.model.lens.LensContext, com.evolveum.midpoint.model.sync.SynchronizationSituation, java.util.Map, com.evolveum.midpoint.task.api.Task, com.evolveum.midpoint.schema.result.OperationResult)
	 */
	@Override
	public <F extends FocusType> void handle(LensContext<F> context, SynchronizationSituation<F> situation,
			Map<QName, Object> parameters, Task task, OperationResult parentResult) {
		LensProjectionContext projectionContext = context.getProjectionContextsIterator().next();
		projectionContext.setSynchronizationIntent(SynchronizationIntent.DELETE);
	}

}
