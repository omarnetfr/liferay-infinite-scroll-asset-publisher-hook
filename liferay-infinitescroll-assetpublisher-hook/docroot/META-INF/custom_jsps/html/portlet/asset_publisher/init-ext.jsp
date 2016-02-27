<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
--%>
<%@page import="javax.portlet.WindowState"%>
<%
	String defaultTemplateItem = "<li class=\"asset-item\">\n<div class=\"asset-item-body\">\n<strong>{entryTitle}</strong> <br>\n <small class=\"asset-item-date\">{publishDate}</small>\n<div class=\"well asset-item-summary\">{summary}</div>\n<div class=\"pull-right\">\n<a class=\"btn btn-xs btn-white\" href=\"{viewURL}\"> {[Liferay.Language.get('read-more')]}<span class=\"hide-accessible\">{[Liferay.Language.get('about')]} {entryTitle}</span> &raquo; </a>\n</div>\n</div>\n</li>";

	boolean enableInfiniteScroll = GetterUtil.getBoolean(portletPreferences.getValue("enableInfiniteScroll", null), true);
	
	String containerHeight = GetterUtil.getString(portletPreferences.getValue("containerHeight", null), "400");
	
	String containerSelector = GetterUtil.getString(portletPreferences.getValue("containerSelector", null), ".asset-list");
	
	boolean enableDebug = GetterUtil.getBoolean(portletPreferences.getValue("enableDebug", null), false);
	
	String linkWindowState = GetterUtil.getString(portletPreferences.getValue("linkWindowState", null), WindowState.NORMAL.toString());
	
	//String styles = GetterUtil.getString(portletPreferences.getValue("styles", null), "{}");
	
	String templateItem = GetterUtil.getString(portletPreferences.getValue("templateItem", null), defaultTemplateItem);
%>
