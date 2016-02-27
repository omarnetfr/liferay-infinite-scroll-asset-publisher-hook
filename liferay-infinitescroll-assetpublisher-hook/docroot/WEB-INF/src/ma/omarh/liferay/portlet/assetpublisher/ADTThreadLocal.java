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
package ma.omarh.liferay.portlet.assetpublisher;

import com.liferay.portal.kernel.util.AutoResetThreadLocal;

/**
 * @author Omar HADDOUCHI
 */
public class ADTThreadLocal {

	public static boolean isImported() {
		return _imported.get();
	}

	public static void setImported(boolean enabled) {
		_imported.set(enabled);
	}

	private static ThreadLocal<Boolean> _imported =
		new AutoResetThreadLocal<Boolean>(
			ADTThreadLocal.class + "._imported", true);

}