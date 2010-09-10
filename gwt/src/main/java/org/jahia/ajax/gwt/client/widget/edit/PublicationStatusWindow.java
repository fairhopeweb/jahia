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

package org.jahia.ajax.gwt.client.widget.edit;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import org.jahia.ajax.gwt.client.core.BaseAsyncCallback;
import org.jahia.ajax.gwt.client.core.JahiaGWTParameters;
import org.jahia.ajax.gwt.client.data.node.GWTJahiaNode;
import org.jahia.ajax.gwt.client.data.publication.GWTJahiaPublicationInfo;
import org.jahia.ajax.gwt.client.data.workflow.GWTJahiaWorkflowDefinition;
import org.jahia.ajax.gwt.client.messages.Messages;
import org.jahia.ajax.gwt.client.service.content.JahiaContentManagementService;
import org.jahia.ajax.gwt.client.util.security.PermissionsUtils;
import org.jahia.ajax.gwt.client.widget.Linker;
import org.jahia.ajax.gwt.client.widget.edit.workflow.dialog.WorkflowActionDialog;
import org.jahia.ajax.gwt.client.widget.toolbar.action.WorkInProgressActionItem;

import java.util.List;

/**
 * Window, displaying the current publication status.
 * User: toto
 * Date: Jan 28, 2010
 * Time: 2:44:46 PM
 */
class PublicationStatusWindow extends Window {
    protected Linker linker;
    protected Button ok;
    protected Button noWorkflow;
    protected Button cancel;
    protected boolean allSubTree;

    PublicationStatusWindow(final Linker linker, final List<String> uuids, final List<GWTJahiaPublicationInfo> infos,
                            boolean allSubTree) {
        setLayout(new FitLayout());

        this.linker = linker;
        this.allSubTree = allSubTree;
        setScrollMode(Style.Scroll.NONE);
        setHeading("Publish");
        setSize(800, 500);
        setResizable(false);

        setModal(true);

        TableData d = new TableData(Style.HorizontalAlignment.CENTER, Style.VerticalAlignment.MIDDLE);
        d.setMargin(5);

        GroupingStore<GWTJahiaPublicationInfo> store = new GroupingStore<GWTJahiaPublicationInfo>();
        store.add(infos);

        final Grid<GWTJahiaPublicationInfo> grid = new PublicationStatusGrid(store);
        add(grid);

        cancel = new Button(Messages.get("label.cancel"), new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent event) {
                hide();
            }
        });

        setButtonAlign(Style.HorizontalAlignment.CENTER);


        ok = new Button(Messages.get("label.publish"));
        GWTJahiaNode selectedNode = linker.getSelectedNode();
        if (selectedNode == null) {
            selectedNode = linker.getMainNode();
        }
        if (selectedNode.getWorkflowInfo().getPossibleWorkflows().get(0) != null) {
            ok.addSelectionListener(new ButtonEventSelectionListener(uuids, selectedNode,
                    selectedNode.getWorkflowInfo().getPossibleWorkflows().get(0),infos));
            addButton(ok);
        }
        if (PermissionsUtils.isPermitted("edit-mode/publication", JahiaGWTParameters.getSiteKey())) {
            noWorkflow = new Button(Messages.get("label.bypassWorkflow", "Bypass workflow"));
            noWorkflow.addSelectionListener(new ButtonEventSelectionListener(uuids, null, null, null));
            addButton(noWorkflow);
        }

        addButton(cancel);
    }

    private class ButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        private List<String> uuids;
        private final GWTJahiaNode selectedNode;
        private final GWTJahiaWorkflowDefinition gwtJahiaWorkflowDefinition;
        private final List<GWTJahiaPublicationInfo> infos;
        protected boolean workflow;

        public ButtonEventSelectionListener(List<String> uuids, GWTJahiaNode selectedNode,
                                            GWTJahiaWorkflowDefinition gwtJahiaWorkflowDefinition,
                                            List<GWTJahiaPublicationInfo> infos) {
            this.uuids = uuids;
            this.selectedNode = selectedNode;
            this.gwtJahiaWorkflowDefinition = gwtJahiaWorkflowDefinition;
            this.infos = infos;
        }

        public void componentSelected(ButtonEvent event) {
            ok.setEnabled(false);
            if (noWorkflow != null) {
                noWorkflow.setEnabled(false);
            }
            cancel.setEnabled(false);
            if (gwtJahiaWorkflowDefinition == null) {
                hide();
                Info.display("Publishing content",
                        "Publishing content");
                final String status = "Publishing content ...";
                WorkInProgressActionItem.setStatus(status);
                JahiaContentManagementService.App.getInstance()
                        .publish(uuids, allSubTree, false, false, null,null,
                                new BaseAsyncCallback() {
                                    public void onApplicationFailure(Throwable caught) {
                                        WorkInProgressActionItem.removeStatus(status);
                                        Info.display("Cannot publish",
                                                "Cannot publish");
                                        Log.error("Cannot publish", caught);
                                    }

                                    public void onSuccess(Object result) {
                                        WorkInProgressActionItem.removeStatus(status);
                                        Info.display(Messages.get("message.content.published"),
                                                Messages.get("message.content.published"));
                                        linker.refresh(Linker.REFRESH_ALL);
                                    }
                                });
            } else {
                hide();
                // Start Publication workflow
                WorkflowActionDialog wad = new WorkflowActionDialog(selectedNode, linker);
                wad.setCustom(new PublicationWorkflow(infos, uuids, allSubTree, selectedNode.getLanguageCode()));
                wad.initStartWorkflowDialog(gwtJahiaWorkflowDefinition);
                wad.show();
                
            }
        }
    }
}
