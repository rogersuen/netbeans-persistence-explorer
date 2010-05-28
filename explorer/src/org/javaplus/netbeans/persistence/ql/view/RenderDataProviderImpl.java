package org.javaplus.netbeans.persistence.ql.view;

import java.awt.Color;
import javax.swing.Icon;
import org.javaplus.netbeans.persistence.ql.view.model.DataDescriptor;
import org.netbeans.swing.outline.RenderDataProvider;

/**
 *
 * @author Roger Suen
 */
public class RenderDataProviderImpl implements RenderDataProvider {

    public String getDisplayName(Object o) {
        return ((DataDescriptor)o).getDisplayName();
    }

    public boolean isHtmlDisplayName(Object o) {
        return false;
    }

    public Color getBackground(Object o) {
        return null;
    }

    public Color getForeground(Object o) {
        return null;
    }

    public String getTooltipText(Object o) {
        return ((DataDescriptor)o).getShortDescription();
    }

    public Icon getIcon(Object o) {
        return ((DataDescriptor)o).getIcon();
    }



}
