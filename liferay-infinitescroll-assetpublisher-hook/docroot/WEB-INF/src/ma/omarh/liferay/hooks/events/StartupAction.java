/**
 * Copyright (c) 2016 Omar HADDOUCHI All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package ma.omarh.liferay.hooks.events;

import java.util.List;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * @author Omar HADDOUCHI
 */
public class StartupAction extends SimpleAction {

	private static final String LIFERAY_INFINITE_SCROLL_PATH = "/html/portlet/asset_publisher/js/liferay-infinite-scroll/liferay-infinite-scroll.js";

	@Override
	public void run(String[] ids) throws ActionException {

		try {
			
			Portlet portlet = PortletLocalServiceUtil.getPortletByStrutsPath(PortalUtil.getDefaultCompanyId(), "asset_publisher");
			
			// add of javascript
			List<String> javascripts = portlet.getHeaderPortletJavaScript();
			javascripts.add(LIFERAY_INFINITE_SCROLL_PATH);
			portlet.setHeaderPortletJavaScript(javascripts);

		} catch (SystemException e) {
			if (_log.isErrorEnabled()) {
				_log.error(e.getMessage());
			}
		}
	}
	
	private static Log _log = LogFactoryUtil.getLog(StartupAction.class);

}
