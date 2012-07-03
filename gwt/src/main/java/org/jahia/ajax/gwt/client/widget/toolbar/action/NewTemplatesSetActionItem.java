/**
 * This file is part of Jahia, next-generation open source CMS:
 * Jahia's next-generation, open source CMS stems from a widely acknowledged vision
 * of enterprise application convergence - web, search, document, social and portal -
 * unified by the simplicity of web content management.
 *
 * For more information, please visit http://www.jahia.com.
 *
 * Copyright (C) 2002-2012 Jahia Solutions Group SA. All rights reserved.
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
 * Commercial and Supported Versions of the program (dual licensing):
 * alternatively, commercial and supported versions of the program may be used
 * in accordance with the terms and conditions contained in a separate
 * written agreement between you and Jahia Solutions Group SA.
 *
 * If you are unsure which license is appropriate for your use,
 * please contact the sales department at sales@jahia.com.
 */

package org.jahia.ajax.gwt.client.widget.toolbar.action;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import org.jahia.ajax.gwt.client.core.BaseAsyncCallback;
import org.jahia.ajax.gwt.client.core.JahiaGWTParameters;
import org.jahia.ajax.gwt.client.data.node.GWTJahiaNode;
import org.jahia.ajax.gwt.client.messages.Messages;
import org.jahia.ajax.gwt.client.service.content.JahiaContentManagementService;
import org.jahia.ajax.gwt.client.widget.edit.EditLinker;
import org.jahia.ajax.gwt.client.widget.form.CalendarField;

/**
 * Action item to create a new templates set
 */
public class NewTemplatesSetActionItem extends BaseActionItem {
    private String siteType;

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    @Override public void onComponentSelection() {
        final Window wnd = new Window();
        wnd.setWidth(550);
        wnd.setHeight(300);
        wnd.setModal(true);
        wnd.setBlinkModal(true);
        wnd.setHeading("New templates set");
        wnd.setLayout(new FitLayout());

        final FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setFrame(false);
        form.setLabelWidth(125);

        final TextField<String> name = new TextField<String>();
        name.setName("name");
        name.setAllowBlank(false);
        name.setFieldLabel(Messages.get("newPackageName.label", "New package name"));
        form.add(name);

        final TextField<String> sources = new TextField<String>();
        sources.setName("sources");
        sources.setFieldLabel(Messages.get("label.sources", "Sources folder"));
        form.add(sources);

        final RadioGroup scmType = new RadioGroup("scmType");
        scmType.setFieldLabel(Messages.get("label.scmType", "SCM type"));
        Radio git = new Radio();
        git.setFieldLabel(Messages.get("label.git", "GIT"));
        git.setValue(true);
        git.setValueAttribute("git");
        scmType.add(git);

        Radio svn = new Radio();
        svn.setFieldLabel(Messages.get("label.svn", "SVN"));
        svn.setValueAttribute("svn");
        scmType.add(svn);
        form.add(scmType);

        final TextField<String> uri = new TextField<String>();
        uri.setName("uri");
        uri.setFieldLabel(Messages.get("label.uri", "URI"));
        form.add(uri);

        sources.addListener(Events.Change, new Listener<FieldEvent>() {
            public void handleEvent(FieldEvent be) {
                if (sources.getValue().length() > 0) {
                    scmType.setEnabled(true);
                    uri.setEnabled(true);
                } else {
                    scmType.setEnabled(false);
                    uri.setEnabled(false);
                }
            }
        });

        Button btnSubmit = new Button(Messages.get("label.save", "Save"), new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent event) {
                wnd.hide();
                linker.loading("Creating template set...");
                JahiaContentManagementService.App.getInstance().createTemplateSet(name.getValue(), null, siteType, sources.getValue(), uri.getValue(),"git", new BaseAsyncCallback<GWTJahiaNode>() {
                    public void onSuccess(GWTJahiaNode result) {
                        linker.loaded();
                        Info.display(Messages.get("label.information", "Information"), Messages.get("message.templateSetCreated", "Templates set successfully created"));
                        JahiaGWTParameters.getSitesMap().put(result.getUUID(), result);
                        JahiaGWTParameters.setSite(result, linker);
                        if (((EditLinker) linker).getSidePanel() != null) {
                            ((EditLinker) linker).getSidePanel().refresh(EditLinker.REFRESH_ALL);
                        }
                        ((EditLinker) linker).onMainSelection(result.getPath(), null, null);
                        SiteSwitcherActionItem.refreshAllSitesList(linker);
                    }

                    public void onApplicationFailure(Throwable caught) {
                        linker.loaded();
                        Info.display(Messages.get("label.error", "Error"), Messages.get("message.templateSetCreationFailed", "Templates set creation failed"));
                    }
                });
            }
        });
        form.addButton(btnSubmit);

        Button btnCancel = new Button(Messages.get("label.cancel", "Cancel"), new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent event) {
                wnd.hide();
            }
        });
        form.addButton(btnCancel);
        form.setButtonAlign(Style.HorizontalAlignment.CENTER);

        wnd.add(form);
        wnd.layout();

        wnd.show();
    }
}
