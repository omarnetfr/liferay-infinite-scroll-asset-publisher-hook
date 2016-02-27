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
package ma.omarh.liferay.portlet.assetpublisher.helper;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.ClassResolverUtil;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.PortalClassInvoker;
import com.liferay.portlet.asset.model.AssetEntry;

public class AssetPublisherHelperUtil {
	
	public static String getAssetViewURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, AssetEntry assetEntry,
			boolean viewInContext) throws Exception {
		
		return (String) PortalClassInvoker.invoke(false, _getAssetViewURL, liferayPortletRequest, liferayPortletResponse, assetEntry, viewInContext);
	}

	private final static String _CLASS_NAME = "com.liferay.portlet.assetpublisher.util.AssetPublisherHelperImpl";

	private static MethodKey _getAssetViewURL = new MethodKey(
			ClassResolverUtil.resolveByPortalClassLoader(_CLASS_NAME),
			"getAssetViewURL", LiferayPortletRequest.class, LiferayPortletResponse.class, AssetEntry.class, boolean.class); 

}
