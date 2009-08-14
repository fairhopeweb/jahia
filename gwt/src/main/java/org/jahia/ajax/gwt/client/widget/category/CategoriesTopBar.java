/**
 * This file is part of Jahia: An integrated WCM, DMS and Portal Solution
 * Copyright (C) 2002-2009 Jahia Solutions Group SA. All rights reserved.
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
package org.jahia.ajax.gwt.client.widget.category;

import java.util.ArrayList;
import java.util.List;

import org.jahia.ajax.gwt.client.util.Formatter;
import org.jahia.ajax.gwt.client.messages.Messages;
import org.jahia.ajax.gwt.client.util.category.CategoriesManagerActions;
import org.jahia.ajax.gwt.client.data.category.GWTJahiaCategoryNode;
import org.jahia.ajax.gwt.client.widget.tripanel.TopBar;

import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * User: ktlili
 * Date: 19 sept. 2008
 * Time: 16:09:29
 */
public class CategoriesTopBar extends TopBar {

    private ToolBar m_component;


    private List<Button> topTableSingleSelectionButtons = new ArrayList<Button>();
    private List<Button> topTableMultipleSelectionButtons = new ArrayList<Button>();
    private Button paste;

    public CategoriesTopBar(final String exportUrl, final String importUrl) {
        m_component = new ToolBar();
        m_component.setHeight(21);
        Button cut = new Button();
        Button remove = new Button();
        paste = new Button();
        Formatter.disableButton(paste);
        Button exportCategories = new Button();
        Button importCategories = new Button();
        Button newCategory = new Button();
        Button updateInfo = new Button();
        Button updateACL = new Button();

        // new category
        newCategory.setIconStyle("fm-newfolder");
        newCategory.setText(getResource("cat_create"));
        newCategory.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent event) {
                CategoriesManagerActions.createCategory(getLinker());
            }
        });
        m_component.add(newCategory);

        // remove
        remove.setIconStyle("fm-remove");
        remove.setText(getResource("cat_remove"));
        remove.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent event) {
                CategoriesManagerActions.remove(getLinker());
            }
        });
        m_component.add(remove);

        // update
        updateInfo.setIconStyle("fm-rename");
        updateInfo.setText(getResource("cat_update"));
        updateInfo.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent event) {
                CategoriesManagerActions.updateInfo(getLinker());
            }
        });
        m_component.add(updateInfo);
        m_component.add(new SeparatorToolItem());

        // cut
        cut.setIconStyle("fm-cut");
        cut.setText(getResource("cat_cut"));
        cut.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent event) {
                CategoriesManagerActions.cut(getLinker());
                paste.setEnabled(true);
            }
        });
        m_component.add(cut);

        // paste
        paste.setIconStyle("fm-paste");
        paste.setText(getResource("cat_paste"));
        paste.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent event) {
                CategoriesManagerActions.paste(getLinker());
            }
        });
        m_component.add(paste);
        m_component.add(new SeparatorToolItem());
        if (exportUrl != null) {
            exportCategories.setIconStyle("fm-download");
            exportCategories.setText(getResource("cat_export"));
            exportCategories.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent event) {
                    CategoriesManagerActions.exportCategories(getLinker(), exportUrl);
                }
            });
            m_component.add(exportCategories);
        }
        if (importUrl != null) {
            importCategories.setIconStyle("fm-upload");
            importCategories.setText(getResource("cat_import"));
            importCategories.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent event) {
                    CategoriesManagerActions.importCategories(getLinker(), importUrl);
                }
            });
            m_component.add(importCategories);
        }
        topTableMultipleSelectionButtons.add(cut);
        topTableMultipleSelectionButtons.add(remove);

        topTableSingleSelectionButtons.add(updateInfo);
        topTableSingleSelectionButtons.add(updateACL);

        // nothing is selected at init
        handleNewSelection(null,null);
    }

    /**
     * Handle new selection
     *
     * @param leftTreeSelection
     * @param topTableSelectionEl
     */
    public void handleNewSelection(Object leftTreeSelection, Object topTableSelectionEl) {
        List<GWTJahiaCategoryNode> topTableSelection = (List<GWTJahiaCategoryNode>) topTableSelectionEl;
        if (topTableSelection != null) {
            // first activate all buttons
            for (Button ti : topTableSingleSelectionButtons) {
                Formatter.enableButton(ti);
            }
            for (Button ti : topTableMultipleSelectionButtons) {
                Formatter.enableButton(ti);
            }

            // handle multiple selection
            if (topTableSelection.size() > 1) {
                for (Button ti : topTableMultipleSelectionButtons) {
                    Formatter.enableButton(ti);
                }

                for (Button ti : topTableSingleSelectionButtons) {
                    Formatter.disableButton(ti);
                }
            }
            // handle single selection
            else if (topTableSelection.size() == 1) {
                for (Button ti : topTableMultipleSelectionButtons) {
                    Formatter.enableButton(ti);
                }
                for (Button ti : topTableSingleSelectionButtons) {
                    Formatter.enableButton(ti);
                }
            }

            // check if one of the selected categories is only read access
            for (GWTJahiaCategoryNode gwtJahiaCategoryNode : topTableSelection) {
                if (!gwtJahiaCategoryNode.isWriteable()) {
                    for (Button ti : topTableSingleSelectionButtons) {
                        Formatter.disableButton(ti);
                    }
                    for (Button ti : topTableMultipleSelectionButtons) {
                        Formatter.disableButton(ti);
                    }
                    break;
                }
            }

        } else if (leftTreeSelection != null) {
            for (Button ti : topTableMultipleSelectionButtons) {
                Formatter.disableButton(ti);
            }

            for (Button ti : topTableSingleSelectionButtons) {
                Formatter.disableButton(ti);
            }

            // nothing is selected
        } else {
            for (Button ti : topTableMultipleSelectionButtons) {
                Formatter.disableButton(ti);
            }

            for (Button ti : topTableSingleSelectionButtons) {
                Formatter.disableButton(ti);
            }
        }
    }

    /**
     * Get main componet
     *
     * @return
     */
    public Component getComponent() {
        return m_component;
    }

    private String getResource(String key) {
        return Messages.getResource(key);
    }
}
