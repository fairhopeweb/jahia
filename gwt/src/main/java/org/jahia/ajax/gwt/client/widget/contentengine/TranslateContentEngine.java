/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2015 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ======================================================================================
 *
 *     IF YOU DECIDE TO CHOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     "This program is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation; either version 2
 *     of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 *     As a special exception to the terms and conditions of version 2.0 of
 *     the GPL (or any later version), you may redistribute this Program in connection
 *     with Free/Libre and Open Source Software ("FLOSS") applications as described
 *     in Jahia's FLOSS exception. You should have received a copy of the text
 *     describing the FLOSS exception, also available here:
 *     http://www.jahia.com/license"
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ======================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 *
 *
 * ==========================================================================================
 * =                                   ABOUT JAHIA                                          =
 * ==========================================================================================
 *
 *     Rooted in Open Source CMS, Jahia’s Digital Industrialization paradigm is about
 *     streamlining Enterprise digital projects across channels to truly control
 *     time-to-market and TCO, project after project.
 *     Putting an end to “the Tunnel effect”, the Jahia Studio enables IT and
 *     marketing teams to collaboratively and iteratively build cutting-edge
 *     online business solutions.
 *     These, in turn, are securely and easily deployed as modules and apps,
 *     reusable across any digital projects, thanks to the Jahia Private App Store Software.
 *     Each solution provided by Jahia stems from this overarching vision:
 *     Digital Factory, Workspace Factory, Portal Factory and eCommerce Factory.
 *     Founded in 2002 and headquartered in Geneva, Switzerland,
 *     Jahia Solutions Group has its North American headquarters in Washington DC,
 *     with offices in Chicago, Toronto and throughout Europe.
 *     Jahia counts hundreds of global brands and governmental organizations
 *     among its loyal customers, in more than 20 countries across the globe.
 *
 *     For more information, please visit http://www.jahia.com
 */
package org.jahia.ajax.gwt.client.widget.contentengine;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import org.jahia.ajax.gwt.client.core.BaseAsyncCallback;
import org.jahia.ajax.gwt.client.data.GWTJahiaLanguage;
import org.jahia.ajax.gwt.client.data.definition.GWTJahiaItemDefinition;
import org.jahia.ajax.gwt.client.data.definition.GWTJahiaNodeProperty;
import org.jahia.ajax.gwt.client.data.node.GWTJahiaNode;
import org.jahia.ajax.gwt.client.messages.Messages;
import org.jahia.ajax.gwt.client.service.content.JahiaContentManagementService;
import org.jahia.ajax.gwt.client.util.definition.FormFieldCreator;
import org.jahia.ajax.gwt.client.util.icons.StandardIconsProvider;
import org.jahia.ajax.gwt.client.widget.Linker;
import org.jahia.ajax.gwt.client.widget.definition.LangPropertiesEditor;
import org.jahia.ajax.gwt.client.widget.definition.PropertiesEditor;

import java.util.*;

/**
 * 
 * User: ktlili
 * Date: Jan 20, 2010
 * Time: 1:53:30 PM
 * 
 */
public class TranslateContentEngine extends Window {
    public static final int BUTTON_HEIGHT = 24;
    
    private GWTJahiaNode node;
    private final GWTJahiaLanguage srcLanguage;
    private final GWTJahiaLanguage destLanguage;
    private Linker linker = null;

    private Button ok;
    private CheckBox wipCheckbox;
    private LangPropertiesEditor sourceLangPropertiesEditor;
    private LangPropertiesEditor targetLangPropertiesEditor;
    protected ButtonBar buttonBar;


    /**
     * Initializes an instance of this class.
     *
     * @param node   the content object to be edited
     * @param linker the edit linker for refresh purpose
     * @param srcLanguage
     * @param destLanguage
     */
    public TranslateContentEngine(GWTJahiaNode node, Linker linker, GWTJahiaLanguage srcLanguage,
                                  GWTJahiaLanguage destLanguage) {
        this.linker = linker;
        this.node = node;
        this.srcLanguage = srcLanguage;
        this.destLanguage = destLanguage;

        init();
    }

    protected void init() {
        setLayout(new FitLayout());
        setBodyBorder(false);
        int windowHeight=com.google.gwt.user.client.Window.getClientHeight()-10;
        int windowWidth=com.google.gwt.user.client.Window.getClientWidth()-10;

        setSize(windowWidth, windowHeight);
        setClosable(true);
        setResizable(true);
        setModal(true);
        setMaximizable(true);
        setHeadingHtml(Messages.get("label.translate", "Translate") + ": " + node.getName());
        LayoutContainer panel = new LayoutContainer();
        panel.setLayout(new BorderLayout());

        sourceLangPropertiesEditor = new LangPropertiesEditor(node, Arrays.asList(GWTJahiaItemDefinition.CONTENT), false, srcLanguage, null);
        targetLangPropertiesEditor = new LangPropertiesEditor(node, Arrays.asList(GWTJahiaItemDefinition.CONTENT), true, destLanguage, sourceLangPropertiesEditor, new LangPropertiesEditor.CallBack() {
            @Override
            public void execute() {
                wipCheckbox.setValue(targetLangPropertiesEditor.isWip());
            }
        });

        Button copyButton = new Button(Messages.get("label.translate.copy", "Copy to other language"));
        sourceLangPropertiesEditor.getTopBar().add(copyButton);
        copyButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                PropertiesEditor sourcePropertiesEditor = sourceLangPropertiesEditor.getPropertiesEditorByLang(sourceLangPropertiesEditor.getDisplayedLocale().getLanguage());
                List<GWTJahiaNodeProperty> props = sourcePropertiesEditor.getProperties();
                Map<String,PropertiesEditor.PropertyAdapterField> fieldsMap = targetLangPropertiesEditor.getPropertiesEditorByLang(targetLangPropertiesEditor.getDisplayedLocale().getLanguage()).getFieldsMap();
                for (final GWTJahiaNodeProperty prop : props) {
                    if (sourcePropertiesEditor.getGWTJahiaItemDefinition(prop).isInternationalized()) {
                        final Field<?> f = fieldsMap.get(prop.getName()).getField();
                        FormFieldCreator.copyValue(prop, f);
                    }
                }
            }
        });

        panel.add(sourceLangPropertiesEditor, new BorderLayoutData(Style.LayoutRegion.WEST, windowWidth/2));
        panel.add(targetLangPropertiesEditor, new BorderLayoutData(Style.LayoutRegion.EAST, windowWidth/2));

        add(panel);

        LayoutContainer buttonsPanel = new LayoutContainer();
        buttonsPanel.setBorders(false);

        buttonBar = new ButtonBar();
        buttonBar.setAlignment(Style.HorizontalAlignment.CENTER);

        initFooter();

        buttonsPanel.add(buttonBar);

        setBottomComponent(buttonsPanel);
        setFooter(true);
        layout();
    }


    /**
     * init buttons
     */
    protected void initFooter() {
        wipCheckbox = new CheckBox();
        wipCheckbox.setBoxLabel(Messages.get("label.saveAsWIP", "Save as work in progress"));
        wipCheckbox.setToolTip(Messages.get("label.saveAsWIP.information", "If checked, this content will ne be part of publication process"));
        buttonBar.add(wipCheckbox);

        ok = new Button(Messages.get("label.save"));
        ok.setHeight(BUTTON_HEIGHT);
        ok.setIcon(StandardIconsProvider.STANDARD_ICONS.engineButtonOK());
        ok.addSelectionListener(new SaveSelectionListener());
        buttonBar.add(ok);

        Button cancel = new Button(Messages.get("label.cancel"));
        cancel.setHeight(BUTTON_HEIGHT);
        cancel.setIcon(StandardIconsProvider.STANDARD_ICONS.engineButtonCancel());
        cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                TranslateContentEngine.this.hide();
            }
        });
        buttonBar.add(cancel);
    }


    /**
     * Save selection listener
     */
    private class SaveSelectionListener extends SelectionListener<ButtonEvent> {
        public void componentSelected(ButtonEvent event) {
            // node
            final List<GWTJahiaNode> nodes = new ArrayList<GWTJahiaNode>();
            List<GWTJahiaNodeProperty> sharedProperties = new ArrayList<GWTJahiaNodeProperty>();
            nodes.add(node);
            sharedProperties.add(new GWTJahiaNodeProperty("j:workInProgress", wipCheckbox.getRawValue()));
            // Ajax call to update values
            JahiaContentManagementService.App.getInstance().savePropertiesAndACL(nodes, null, targetLangPropertiesEditor.getLangPropertiesMap(), sharedProperties, null, new BaseAsyncCallback<Object>() {
                public void onApplicationFailure(Throwable throwable) {
                    String message = throwable.getMessage();
                    if (message.contains("Invalid link")) {
                        message = Messages.get("label.error.invalidlink", "Invalid link") + " : " + message.substring(message.indexOf(":")+1);
                    }
                    com.google.gwt.user.client.Window.alert(Messages.get("failure.properties.save", "Properties save failed") + "\n\n"
                            + message);
                    Log.error("failed", throwable);
                }

                public void onSuccess(Object o) {
                    Info.display(Messages.get("label.information", "Information"), Messages.get("saved_prop", "Properties saved\n\n"));
                    TranslateContentEngine.this.hide();
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put(Linker.REFRESH_MAIN, true);
                    linker.refresh(data);
                }
            });
        }

    }

    @Override
    public void focus() {
        // do nothing to prevent menu to disappear when mouse over
    }

}

