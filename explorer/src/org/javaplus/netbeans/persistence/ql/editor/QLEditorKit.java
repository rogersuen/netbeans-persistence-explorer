package org.javaplus.netbeans.persistence.ql.editor;

import org.netbeans.modules.editor.NbEditorKit;
import org.netbeans.editor.ext.ExtKit;
import org.netbeans.editor.BaseKit;


/**
 *
 * @author Roger Suen
 */
public class QLEditorKit extends NbEditorKit {
    
    public static final String CONTENT_TYPE = "text/x-ql";

    public QLEditorKit() {
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }



}
