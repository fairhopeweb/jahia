package org.jahia.ajax.gwt.client.widget.edit.mainarea;

import com.extjs.gxt.ui.client.widget.Header;
import com.google.gwt.user.client.ui.HTML;
import org.jahia.ajax.gwt.client.messages.Messages;

/**
 * Created by IntelliJ IDEA.
 * User: toto
 * Date: Aug 18, 2009
 * Time: 7:25:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class AreaModule extends SimpleModule {

    public AreaModule(String id, String path, String s, String template, String scriptInfo, String nodeTypes, boolean locked, boolean shared, boolean deployed, MainModule mainModule) {
        super(id, path, template, scriptInfo, nodeTypes, locked, shared, deployed, mainModule);
        hasDragDrop = false;
        head = new Header();
        add(head);

        if (path.contains("/")) {
            head.setText(Messages.getResource("em_area") + " : " + path.substring(path.lastIndexOf('/') + 1));
        } else {
            head.setText(Messages.getResource("em_area")+" : "+ path);
        }
        setBorders(false);
//        setBodyBorder(false);
        head.addStyleName("x-panel-header");
        head.addStyleName("x-panel-header-areamodule");
        if (locked) {
            head.addStyleName("x-panel-header-lockedmodule");
        }
        html = new HTML(s);
        add(html);
    }
}