/* ListitemDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 6 2007, Created by Jeff.Liu
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Listitem;

/**
 * {@link Listitem}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ListitemDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Listitem self = (Listitem)comp;
		
		if(self.getMold().equals("select")){
			wh.write("<option id=\"").write(self.getUuid()).write('"')
				.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
			new Out(self.getLabel()).setMaxlength(self.getMaxlength()).render(out);
			wh.write("</option>");
		}else{
			wh.write("<tr id=\"").write(self.getUuid()).write("\" z.type=\"Lit\"")
				.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
			wh.writeChildren(self);
			wh.writeln("</tr>");
		}
	}
}
