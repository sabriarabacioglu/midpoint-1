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

import java.util.HashMap;
import java.util.Map;

import com.evolveum.midpoint.util.DebugDumpable;
import com.evolveum.midpoint.util.DebugUtil;
import com.evolveum.midpoint.util.Dumpable;
import com.evolveum.prism.xml.ns._public.types_2.PolyStringType;

/**
 * A generic instruction to start a workflow process.
 * May be subclassed in order to add further information.
 *
 * @author mederly
 */
public class StartProcessInstruction implements DebugDumpable {

    private Map<String,Object> processVariables = new HashMap<String,Object>();
    private String processName;
    private PolyStringType taskName;
    private boolean simple;
    private boolean noProcess;            // no wf process, only direct execution of specified deltas
    private boolean executeImmediately;     // executes as soon as possible, i.e. usually directly after approval

    public boolean isSimple() {
        return simple;
    }

    public void setSimple(boolean simple) {
        this.simple = simple;
    }

    public void setProcessName(String name) {
        processName = name;
    }

    public String getProcessName() {
        return processName;
    }

    public Map<String, Object> getProcessVariables() {
        return processVariables;
    }

    public void addProcessVariable(String name, Object value) {
        processVariables.put(name, value);
    }

    public PolyStringType getTaskName() {
        return taskName;
    }

    public void setTaskName(PolyStringType taskName) {
        this.taskName = taskName;
    }

    public boolean isExecuteImmediately() {
        return executeImmediately;
    }

    public void setExecuteImmediately(boolean executeImmediately) {
        this.executeImmediately = executeImmediately;
    }

    public boolean isNoProcess() {
        return noProcess;
    }

    public boolean startsWorkflowProcess() {
        return !noProcess;
    }

    public void setNoProcess(boolean noProcess) {
        this.noProcess = noProcess;
    }

    public String toString() {
        return "StartProcessInstruction: processName = " + processName + ", simple: " + simple + ", variables: " + processVariables;
    }

    @Override
    public String debugDump() {
        return debugDump(0);
    }

    @Override
    public String debugDump(int indent) {
        StringBuilder sb = new StringBuilder();

        DebugUtil.indentDebugDump(sb, indent);
        sb.append("StartProcessInstruction: process: " + processName + " (" +
                (simple ? "simple" : "smart") + ", " +
                (executeImmediately ? "execute-immediately" : "execute-at-end") + ", " +
                (noProcess ? "no-process" : "with-process") +
                "), task = " + taskName + "\n");

        DebugUtil.indentDebugDump(sb, indent);
        sb.append("Process variables:\n");

        for (Map.Entry<String, Object> entry : processVariables.entrySet()) {
            DebugUtil.indentDebugDump(sb, indent);
            sb.append(" - " + entry.getKey() + " = ");
            Object value = entry.getValue();
            if (value instanceof DebugDumpable) {
                sb.append("\n" + ((DebugDumpable) value).debugDump(indent+1));
            } else if (value instanceof Dumpable) {
                sb.append("\n" + ((Dumpable) value).dump());
            } else {
                sb.append(value != null ? value.toString() : "null");
            }
            sb.append("\n");
        }
        return sb.toString();

    }
}