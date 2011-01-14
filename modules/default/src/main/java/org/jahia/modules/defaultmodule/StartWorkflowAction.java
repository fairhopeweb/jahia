/**
 * This file is part of Jahia: An integrated WCM, DMS and Portal Solution
 * Copyright (C) 2002-2010 Jahia Solutions Group SA. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL (or any later version), you may redistribute this Program in connection
 * with Free/Libre and Open Source Software ("FLOSS") applications as described
 * in Jahia's FLOSS exception. You should have received a copy of the text
 * describing the FLOSS exception, and it is also available here:
 * http://www.jahia.com/license
 *
 * Commercial and Supported Versions of the program
 * Alternatively, commercial and supported versions of the program may be used
 * in accordance with the terms contained in a separate written agreement
 * between you and Jahia Solutions Group SA. If you are unsure which license is appropriate
 * for your use, please contact the sales department at sales@jahia.com.
 */

package org.jahia.modules.defaultmodule;

import org.apache.commons.lang.StringUtils;
import org.jahia.bin.ActionResult;
import org.jahia.bin.Action;
import org.jahia.services.content.JCRPublicationService;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.PublicationInfo;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.workflow.WorkflowService;
import org.jahia.services.workflow.WorkflowVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: toto
 * Date: Mar 18, 2010
 * Time: 12:16:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class StartWorkflowAction extends Action {
    private WorkflowService workflowService;
    private JCRPublicationService publicationService;

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    public void setPublicationService(JCRPublicationService publicationService) {
        this.publicationService = publicationService;
    }

    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
                                  JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        String process = parameters.get("process").get(0);
        String workflowDefinitionKey = StringUtils.substringAfter(process, ":");
        String providerKey = StringUtils.substringBefore(process, ":");

        Map<String, Object> map = getVariablesMap(parameters);
        final LinkedHashSet<String> languages = new LinkedHashSet<String>();
        languages.add(resource.getLocale().toString());
        final List<PublicationInfo> infoList = publicationService.getPublicationInfo(resource.getNode().getIdentifier(),
                                                                                     languages, true, true, false,
                                                                                     resource.getNode().getSession().getWorkspace().getName(),
                                                                                     "live");
        map.put("publicationInfos", infoList);
        workflowService.startProcess(resource.getNode(), workflowDefinitionKey, providerKey, map);
        return ActionResult.OK_JSON;
    }

    private HashMap<String, Object> getVariablesMap(Map<String, List<String>> properties) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (Map.Entry<String, List<String>> property : properties.entrySet()) {
            if (!"process".equals(property.getKey())) {
                List<String> propertyValues = property.getValue();
                List<WorkflowVariable> values = new ArrayList<WorkflowVariable>(propertyValues.size());
                boolean toBeAdded = false;
                for (String value : propertyValues) {
                    if (!"".equals(value.trim())) {
                        values.add(new WorkflowVariable(value, 1));
                        toBeAdded = true;
                    }
                }
                if (toBeAdded) {
                    map.put(property.getKey(), values);
                } else {
                    map.put(property.getKey(), new ArrayList<WorkflowVariable>());
                }
            }
        }
        return map;
    }
}
