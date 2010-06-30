package org.jahia.ajax.gwt.client.widget.edit.contentengine;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jahia.ajax.gwt.client.core.BaseAsyncCallback;
import org.jahia.ajax.gwt.client.data.GWTJahiaCreateEngineInitBean;
import org.jahia.ajax.gwt.client.data.GWTJahiaLanguage;
import org.jahia.ajax.gwt.client.data.acl.GWTJahiaNodeACL;
import org.jahia.ajax.gwt.client.data.definition.GWTJahiaNodeProperty;
import org.jahia.ajax.gwt.client.data.definition.GWTJahiaNodeType;
import org.jahia.ajax.gwt.client.data.node.GWTJahiaNode;
import org.jahia.ajax.gwt.client.data.toolbar.GWTConfiguration;
import org.jahia.ajax.gwt.client.data.toolbar.GWTEngine;
import org.jahia.ajax.gwt.client.messages.Messages;
import org.jahia.ajax.gwt.client.service.content.JahiaContentManagementService;
import org.jahia.ajax.gwt.client.util.acleditor.AclEditor;
import org.jahia.ajax.gwt.client.util.icons.StandardIconsProvider;
import org.jahia.ajax.gwt.client.widget.Linker;
import org.jahia.ajax.gwt.client.widget.definition.PropertiesEditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: toto
 * Date: Jan 7, 2010
 * Time: 1:55:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateContentEngine extends AbstractContentEngine {

    protected GWTJahiaNodeType type = null;
    protected String targetName = null;
    protected boolean createInParentAndMoveBefore = false;


    /**
     * Open Edit content engine for a new node creation
     *
     * @param linker
     * @param parent
     * @param type
     * @param targetName
     */
    public CreateContentEngine(Linker linker, GWTJahiaNode parent, GWTJahiaNodeType type, String targetName) {
        this(linker, parent, type, targetName, false);

    }

    /**
     * Open Edit content engine for a new node creation
     *
     * @param linker                      The linker
     * @param parent                      The parent node where to create the new node - if createInParentAndMoveBefore, the node is sibling
     * @param type                        The selected node type of the new node
     * @param targetName                  The name of the new node, or null if automatically defined
     * @param createInParentAndMoveBefore
     */
    public CreateContentEngine(Linker linker, GWTJahiaNode parent, GWTJahiaNodeType type, String targetName, boolean createInParentAndMoveBefore) {
        this(linker, parent, type, new HashMap<String, GWTJahiaNodeProperty>(), targetName, createInParentAndMoveBefore);
    }

    /**
     * Open Edit content engine for a new node creation
     *
     * @param linker                      The linker
     * @param parent                      The parent node where to create the new node - if createInParentAndMoveBefore, the node is sibling
     * @param type                        The selected node type of the new node
     * @param props                       initial values for properties
     * @param targetName                  The name of the new node, or null if automatically defined
     * @param createInParentAndMoveBefore
     */
    public CreateContentEngine(Linker linker, GWTJahiaNode parent, GWTJahiaNodeType type, Map<String, GWTJahiaNodeProperty> props, String targetName, boolean createInParentAndMoveBefore) {
        super(getCreateConfig(type, linker.getConfig()), linker);
        this.existingNode = false;
        this.parentNode = parent;
        this.type = type;
        if (!"*".equals(targetName)) {
            this.targetName = targetName;
        }
        this.createInParentAndMoveBefore = createInParentAndMoveBefore;

        nodeTypes = new ArrayList<GWTJahiaNodeType>(1);
        nodeTypes.add(type);
        properties = new HashMap<String, GWTJahiaNodeProperty>(props);
        heading = "Create " + type.getLabel();
        loadEngine();

        init();
    }

    public static GWTEngine getCreateConfig(GWTJahiaNodeType type, GWTConfiguration config) {
        for (GWTEngine engine : config.getCreateEngines()) {
            if (type.getName().equals(engine.getNodeType()) || type.getSuperTypes().contains(engine.getNodeType())) {
                return engine;
            }
        }
        return null;
    }


    /**
     * init buttons
     */
    protected void initFooter() {
        Button ok = new Button(Messages.getResource("label.save"));
        ok.setHeight(BUTTON_HEIGHT);
        ok.setEnabled(true);
        ok.setIcon(StandardIconsProvider.STANDARD_ICONS.engineButtonOK());
        ok.addSelectionListener(new CreateSelectionListener());

        buttonBar.add(ok);

        Button okAndNew = new Button(Messages.getResource("org.jahia.engines.filemanager.Filemanager_Engine.properties.saveAndNew.label"));
        okAndNew.setHeight(BUTTON_HEIGHT);
        okAndNew.setIcon(StandardIconsProvider.STANDARD_ICONS.engineButtonOK());

        okAndNew.addSelectionListener(new CreateAndAddNewSelectionListener());
        buttonBar.add(okAndNew);

        Button cancel = new Button(Messages.getResource("label.cancel"));
        cancel.setHeight(BUTTON_HEIGHT);
        cancel.setIcon(StandardIconsProvider.STANDARD_ICONS.engineButtonCancel());
        cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                CreateContentEngine.this.hide();
            }
        });
        buttonBar.add(cancel);
    }

    /**
     * on language chnage, fill currentAzble
     */
    protected void onLanguageChange() {
        fillCurrentTab();
    }


    /**
     * load mixin
     */
    private void loadEngine() {
        contentService.initializeCreateEngine(nodeTypes.iterator().next().getName(), parentNode.getPath(), new BaseAsyncCallback<GWTJahiaCreateEngineInitBean>() {
            public void onSuccess(GWTJahiaCreateEngineInitBean result) {
                mixin = result.getMixin();
                initializersValues = result.getInitializersValues();

                final List<GWTJahiaLanguage> languages = result.getLanguages();
                if (languages != null && !languages.isEmpty()) {
                    for (GWTJahiaLanguage gwtJahiaLanguage : languages) {
                        if (gwtJahiaLanguage.isCurrent()) {
                            defaultLanguageBean = gwtJahiaLanguage;
                            break;
                        }
                    }
                    setAvailableLanguages(languages);
                }
                fillCurrentTab();

            }

            public void onApplicationFailure(Throwable caught) {
                Log.error("Unable to load avalibale mixin", caught);
            }
        });
    }


    protected class CreateSelectionListener extends SelectionListener<ButtonEvent> {
        public void componentSelected(ButtonEvent event) {
            save(true);
        }
    }

    protected class CreateAndAddNewSelectionListener extends SelectionListener<ButtonEvent> {
        public void componentSelected(ButtonEvent event) {
            save(false);
        }
    }

    protected void save(final boolean closeAfterSave) {
        String nodeName = targetName;
        final List<GWTJahiaNodeProperty> props = new ArrayList<GWTJahiaNodeProperty>();
        final Map<String, List<GWTJahiaNodeProperty>> langCodeProperties = new HashMap<String, List<GWTJahiaNodeProperty>>();
        final List<String> mixin = new ArrayList<String>();

        // new acl
        GWTJahiaNodeACL newNodeACL = null;

        for (TabItem item : tabs.getItems()) {
            if (item instanceof PropertiesTabItem) {
                PropertiesTabItem propertiesTabItem = (PropertiesTabItem) item;
                PropertiesEditor pe = ((PropertiesTabItem) item).getPropertiesEditor();
                if (pe != null) {
                    // props.addAll(pe.getProperties());
                    mixin.addAll(pe.getAddedTypes());
                    mixin.addAll(pe.getTemplateTypes());
                }

                // handle multilang
                if (propertiesTabItem.isMultiLang()) {
                    // for now only contentTabItem  has multilang. properties
                    langCodeProperties.putAll(propertiesTabItem.getLangPropertiesMap(false));
                    if (pe != null) {
                        props.addAll(pe.getProperties(false, true, false));
                    }
                } else {
                    if (pe != null) {
                        props.addAll(pe.getProperties());
                    }
                }
                if (item instanceof ContentTabItem) {
                    if (((ContentTabItem) item).isNodeNameFieldDisplayed()) {
                        String nodeNameValue = ((ContentTabItem) item).getName().getValue();
                        nodeName = "Automatically Created (you can type your name here if you want)".equals(nodeNameValue) ? targetName : nodeNameValue;
                    }
                }
            } else if (item instanceof RightsTabItem) {
                AclEditor acl = ((RightsTabItem) item).getRightsEditor();
                if (acl != null) {
                    newNodeACL = acl.getAcl();
                }
            } else if (item instanceof CategoriesTabItem) {
                ((CategoriesTabItem) item).updateProperties(((CategoriesTabItem) item).getCategoriesEditor(), props, mixin);
            } else if (item instanceof TagsTabItem) {
                ((TagsTabItem) item).updateProperties(((TagsTabItem) item).getTagsEditor(), props, mixin);
            }
        }

        doSave(nodeName, props, langCodeProperties, mixin, newNodeACL, closeAfterSave);
    }

    protected void doSave(String nodeName, List<GWTJahiaNodeProperty> props, Map<String, List<GWTJahiaNodeProperty>> langCodeProperties, List<String> mixin, GWTJahiaNodeACL newNodeACL, final boolean closeAfterSave) {
        final AsyncCallback<GWTJahiaNode> callback = new BaseAsyncCallback<GWTJahiaNode>() {
            public void onApplicationFailure(Throwable throwable) {
                com.google.gwt.user.client.Window.alert("Properties save failed\n\n" + throwable.getLocalizedMessage());
                Log.error("failed", throwable);
            }

            public void onSuccess(GWTJahiaNode node) {
                if (closeAfterSave) {
                    Info.display("", "Node " + node.getName() + " created");
                    CreateContentEngine.this.hide();
                } else {
                    CreateContentEngine.this.tabs.removeAll();
                    CreateContentEngine.this.initTabs();
                    CreateContentEngine.this.layout(true);
                }


                if (node.isPage()) {
                    linker.refresh(Linker.REFRESH_MAIN + Linker.REFRESH_PAGES);
                } else {
                    linker.refresh(Linker.REFRESH_MAIN);
                }
            }
        };
        if (createInParentAndMoveBefore) {
            JahiaContentManagementService.App.getInstance().createNodeAndMoveBefore(parentNode.getPath(), nodeName, type.getName(), mixin, newNodeACL, props, langCodeProperties, callback);
        } else {
            JahiaContentManagementService.App.getInstance().createNode(parentNode.getPath(), nodeName, type.getName(), mixin, newNodeACL, props, langCodeProperties, callback);
        }
    }

}
