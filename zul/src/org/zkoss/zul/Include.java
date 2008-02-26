/* Include.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Sep 28 18:01:03     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.HashMap;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Exceptions;
import org.zkoss.mesg.Messages;
import org.zkoss.util.logging.Log;

import org.zkoss.web.Attributes;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Clients;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.mesg.MZul;

/**
 * Includes the result generated by any servlet.
 *
 * <p>Non-XUL extension.
 *
 * <p>If the servlet is eventually another ZUL page, the page will be
 * added to the current desktop when this element is added to
 * the current desktop.
 *
 * @author tomyeh
 */
public class Include extends XulElement {
	private static final Log log = Log.lookup(Include.class);
	protected String _src;
	private boolean _localized;
	private boolean _progressing;
	/** 0: not yet handled, 1: wait for echoEvent, 2: done. */
	private byte _progressStatus;

	public Include() {
	}
	public Include(String src) {
		setSrc(src);
	}

	/**
	 * Sets whether to show the {@link MZul#PLEASE_WAIT} message before a long operation.
	 * This implementation will automatically use an echo event like {@link Events#echoEvent(String, org.zkoss.zk.ui.Component, String)} 
	 * to suspend the including progress before using the {@link Clients#showBusy(String, boolean)} 
	 * method to show the {@link MZul#PLEASE_WAIT} message at client side. 
	 * 
	 * <p>Default: false.
	 * @since 3.0.4
	 */
	public void setProgressing(boolean progressing) {
		if (_progressing != progressing) {
			_progressing = progressing;
			checkProgressing();
		}
	}
	/**
	 * Returns whether to show the {@link MZul#PLEASE_WAIT} message before a long operation.
	 * <p>Default: false.
	 * @since 3.0.4
	 */
	public boolean getProgressing() {
		return _progressing;
	}
	/**
	 * Internal use only.
	 *@since 3.0.4
	 */
	public void onEchoInclude() {
		Clients.showBusy(null , false);
		super.invalidate();
	}
	/** Returns the src.
	 * <p>Default: null.
	 */
	public String getSrc() {
		return _src;
	}
	/** Sets the src.
	 *
	 * <p>If src is changed, the whole component is invalidate.
	 * Thus, you want to smart-update, you have to override this method.
	 *
	 * @param src the source URI. If null or empty, nothing is included.
	 * You can specify the source URI with the query string and they
	 * will become param ({@link Execution#getParameter}).
	 * For example, if "/a.zul?b=c" is specified, you can access
	 * the parameter with ${b} in a.zul.
	 */
	public void setSrc(String src) throws WrongValueException {
		if (src != null && src.length() == 0) src = null;

		if (!Objects.equals(_src, src)) {
			_src = src;
			invalidate();
		}
	}
	
	/** Returns whether the source depends on the current Locale.
	 * If true, it will search xxx_en_US.yyy, xxx_en.yyy and xxx.yyy
	 * for the proper content, where src is assumed to be xxx.yyy.
	 *
	 * <p>Default: false;
	 */
	public final boolean isLocalized() {
		return _localized;
	}
	/** Sets whether the source depends on the current Locale.
	 */
	public final void setLocalized(boolean localized) {
		if (_localized != localized) {
			_localized = localized;
			invalidate();
		}
	}

	//-- Component --//
	public void invalidate() {
		super.invalidate();

		if (_progressStatus >= 2) _progressStatus = 0;
		checkProgressing();
	}
	/** Checks if _progressingg is defined.
	 */
	private void checkProgressing() {
		if(_progressing && _progressStatus == 0) {
			_progressStatus = 1;
			Clients.showBusy(Messages.get(MZul.PLEASE_WAIT), true);
			Events.echoEvent("onEchoInclude", this, null);
		}
	}

	/** Default: not childable.
	 */
	public boolean isChildable() {
		return false;
	}
	public void redraw(Writer out) throws IOException {
		final UiEngine ueng =
			((WebAppCtrl)getDesktop().getWebApp()).getUiEngine();
		ueng.pushOwner(this);
		try {
			out.write("<div id=\"");
			out.write(getUuid());
			out.write('"');
			out.write(getOuterAttrs());
			out.write(getInnerAttrs());
			out.write(">\n");
			if (_progressStatus == 1) {
				_progressStatus = 2;
			} else if (_src != null && _src.length() > 0) {
				include(out);
			}
			out.write("\n</div>");
		} finally {
			ueng.popOwner();
		}
	}
	private void include(Writer out) throws IOException {
		final Desktop desktop = getDesktop();
		final Execution exec = desktop.getExecution();
		final String src = exec.toAbsoluteURI(_src, false);
		try {
			exec.include(out, src, null, Execution.PASS_THRU_ATTR);
		} catch (Throwable err) {
		//though DHtmlLayoutServlet handles exception, we still have to
		//handle it because src might not be ZUML
			final String errpg =
				desktop.getWebApp().getConfiguration().getErrorPage(
					desktop.getDeviceType(), err);
			if (errpg != null) {
				try {
					exec.setAttribute("javax.servlet.error.message", Exceptions.getMessage(err));
					exec.setAttribute("javax.servlet.error.exception", err);
					exec.setAttribute("javax.servlet.error.exception_type", err.getClass());
					exec.setAttribute("javax.servlet.error.status_code", new Integer(500));
					exec.include(out, errpg, null, 0);
					return; //done
				} catch (IOException ex) { //eat it (connection off)
				} catch (Throwable ex) {
					log.warning("Failed to load the error page: "+errpg, ex);
				}
			}

			final String msg = Messages.get(MZk.PAGE_FAILED,
				new Object[] {src, Exceptions.getMessage(err),
					Exceptions.formatStackTrace(null, err, null, 6)});
			final HashMap attrs = new HashMap();
			attrs.put(Attributes.ALERT_TYPE, "error");
			attrs.put(Attributes.ALERT, msg);
			exec.include(out,
				"~./html/alert.dsp", attrs, Execution.PASS_THRU_ATTR);
		}
	}
}
