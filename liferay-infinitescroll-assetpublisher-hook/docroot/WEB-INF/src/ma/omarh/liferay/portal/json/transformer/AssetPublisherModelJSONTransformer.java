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
package ma.omarh.liferay.portal.json.transformer;

import com.liferay.portal.kernel.json.JSONTransformer;
import com.liferay.portlet.asset.model.AssetEntry;

import flexjson.JSONContext;
import flexjson.transformer.ObjectTransformer;

/**
 * @author Omar HADDOUCHI
 */
public class AssetPublisherModelJSONTransformer extends ObjectTransformer implements JSONTransformer {

	@Override
	public void transform(Object object) {
		AssetEntry assetPublisherModel = (AssetEntry)object;

		JSONContext jsonContext = getContext();
		

		jsonContext.transform(assetPublisherModel);
	}

}