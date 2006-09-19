/* RedrawCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 11 16:34:56     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.au.impl;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used only by {@link AuRequest} to implement the redraw command.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class RedrawCommand extends Command {
	public RedrawCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp != null) comp.invalidate();
	}
}
