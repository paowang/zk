/* BindingListModelSet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jan 29 21:07:15     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zul.ListModelSet;

import java.util.Set;

/**
 * <p>This is the {@link BindingListModel} as a {@link java.util.Set} to be used with {@link Listbox} 
 * and {@link DataBinder}.
 * Add or remove the contents of this model as a Set would cause the associated Listbox to change accordingly.</p> 
 *
 * @author Henri Chen
 * @see ListModel
 * @see ListModelSet
 */
public class BindingListModelSet extends ListModelSet implements BindingListModel {
	/* package */ BindingListModelSet(Set set) {
		super(set);
	}
	
	public int indexOf(Object elm) {
		return _list.indexOf(elm);
	}
}

