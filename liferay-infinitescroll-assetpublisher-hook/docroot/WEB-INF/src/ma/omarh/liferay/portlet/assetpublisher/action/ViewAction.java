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
package ma.omarh.liferay.portlet.assetpublisher.action;

import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ValidatorException;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletResponse;

import ma.omarh.liferay.portlet.assetpublisher.ADTThreadLocal;
import ma.omarh.liferay.portlet.assetpublisher.helper.AssetPublisherHelperUtil;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.BaseStrutsPortletAction;
import com.liferay.portal.kernel.struts.StrutsPortletAction;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.AssetRendererFactoryRegistryUtil;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.AssetTag;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.assetpublisher.util.AssetPublisherUtil;
import com.liferay.portlet.dynamicdatamapping.NoSuchTemplateException;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplate;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants;
import com.liferay.portlet.dynamicdatamapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.portlet.portletdisplaytemplate.util.PortletDisplayTemplate;

/**
 * @author Omar HADDOUCHI
 */
public class ViewAction extends BaseStrutsPortletAction {

	private static final String DEFAULT_TEMPLATE_KEY = "infinitescroll-assetpublisher-sample.ftl";

	public void processAction(
            StrutsPortletAction originalStrutsPortletAction,
            PortletConfig portletConfig, ActionRequest actionRequest,
            ActionResponse actionResponse)
        throws Exception {

        originalStrutsPortletAction.processAction(
            originalStrutsPortletAction, portletConfig, actionRequest,
            actionResponse);
    }

    public String render(
            StrutsPortletAction originalStrutsPortletAction,
            PortletConfig portletConfig, RenderRequest renderRequest,
            RenderResponse renderResponse)
        throws Exception {
    	
    	try {
    		addDefaultADT(renderRequest);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

        return originalStrutsPortletAction.render(
            null, portletConfig, renderRequest, renderResponse);

    }

	public void serveResource(
            StrutsPortletAction originalStrutsPortletAction,
            PortletConfig portletConfig, ResourceRequest resourceRequest,
            ResourceResponse resourceResponse)
        throws Exception {
		
		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		Format dateFormatDate = FastDateFormatFactoryUtil.getDate(themeDisplay.getLocale(), themeDisplay.getTimeZone());
		
		PortletPreferences portletPreferences = resourceRequest.getPreferences();
		
		int delta = GetterUtil.getInteger(portletPreferences.getValue("pageDelta", "5"));
		String assetLinkBehavior = GetterUtil.getString(portletPreferences.getValue("assetLinkBehavior", "showFullContent"));
		String linkWindowState = GetterUtil.getString(portletPreferences.getValue("linkWindowState", null), WindowState.NORMAL.toString());
		String[] metadataFields = StringUtil.split(portletPreferences.getValue("metadataFields", StringPool.BLANK));
		
		int start = ParamUtil.getInteger(resourceRequest, "start", 0);
        int end = start + delta;
		
		List<AssetEntry> entries = getAssetEntries(portletPreferences, themeDisplay.getLayout(), themeDisplay.getScopeGroupId(), start, end, true);
		
		JSONArray jsonItems = JSONFactoryUtil.createJSONArray();
		
		for (AssetEntry assetEntry : entries) {
			
			AssetRenderer assetRenderer = assetEntry.getAssetRenderer();
			
			String viewURL = AssetPublisherHelperUtil.getAssetViewURL(PortalUtil.getLiferayPortletRequest(resourceRequest), PortalUtil.getLiferayPortletResponse(resourceResponse), assetEntry, false);
			
			if (assetLinkBehavior != "showFullContent") {
				viewURL = assetRenderer.getURLViewInContext(PortalUtil.getLiferayPortletRequest(resourceRequest), PortalUtil.getLiferayPortletResponse(resourceResponse), viewURL);
			}

			viewURL = StringUtil.insert(viewURL, "/" + linkWindowState, viewURL.indexOf("?"));
			
			JSONObject jsonItem = JSONFactoryUtil.createJSONObject();
			
			for (int m = 0; m < metadataFields.length; m++) {
				Object value = null;

				if (metadataFields[m].equals("create-date")) {
					
					value = dateFormatDate.format(assetEntry.getCreateDate());
					
				} else if (metadataFields[m].equals("modified-date")) {
					
					value = dateFormatDate.format(assetEntry.getModifiedDate());
					
				} else if (metadataFields[m].equals("publish-date")) {
					
					if (assetEntry.getPublishDate() == null) {
						value = StringPool.BLANK;
					} else {
						value = dateFormatDate.format(assetEntry.getPublishDate());
					}
					
				} else if (metadataFields[m].equals("expiration-date")) {
					if (assetEntry.getExpirationDate() == null) {
						value = StringPool.BLANK;
					} else {
						value = dateFormatDate.format(assetEntry.getExpirationDate());
					}
					
				} else if (metadataFields[m].equals("priority")) {
					
					value = String.valueOf(assetEntry.getPriority());
					
				} else if (metadataFields[m].equals("author")) {
					
					String userName = PortalUtil.getUserName(assetRenderer.getUserId(), assetRenderer.getUserName());

					value = HtmlUtil.escape(userName);
					
				} else if (metadataFields[m].equals("view-count")) {
					
					value = String.valueOf(assetEntry.getViewCount());
					
				} else if (metadataFields[m].equals("categories")) {
					List<AssetCategory> assetCategories = AssetCategoryLocalServiceUtil.getAssetEntryAssetCategories(assetEntry.getEntryId());
					
					JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
					
					for (AssetCategory assetCategory : assetCategories) {
						jsonArray.put(assetCategory.getTitle(themeDisplay.getLocale()));
					}
					
					value = jsonArray;
					
				} else if (metadataFields[m].equals("tags")) {
					List<AssetTag> assetTags = AssetTagLocalServiceUtil.getAssetEntryAssetTags(assetEntry.getEntryId());
					
					JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
					
					for (AssetTag assetTag : assetTags) {
						jsonArray.put(assetTag.getName());
					}
					
					value = jsonArray;
				}
				
				if (value instanceof String) {
					jsonItem.put(CamelCaseUtil.toCamelCase(metadataFields[m]), String.valueOf(value));
				} else if (value instanceof JSONArray)  {
					jsonItem.put(CamelCaseUtil.toCamelCase(metadataFields[m]), (JSONArray)value);
				}
			}

			jsonItem.put("entryTitle", HtmlUtil.escape(assetEntry.getTitle(themeDisplay.getLocale())));
			jsonItem.put("summary", HtmlUtil.escape(StringUtil.shorten(assetRenderer.getSummary(themeDisplay.getLocale()), 500)));
			jsonItem.put("viewURL", viewURL);
			
			jsonItems.put(jsonItem);
		}
		
		JSONObject jsonResponse = JSONFactoryUtil.createJSONObject();
		
		jsonResponse.put("items", jsonItems);
		jsonResponse.put("allContentLoaded", entries.size() < delta);
		
		writeJSON(resourceRequest, resourceResponse, jsonResponse);

        originalStrutsPortletAction.serveResource(
            originalStrutsPortletAction, portletConfig, resourceRequest,
            resourceResponse);

    }
	
	private List<AssetEntry> getAssetEntries(
			PortletPreferences portletPreferences, Layout layout,
			long scopeGroupId, int start, int end, boolean checkPermission)
		throws PortalException, SystemException {

		long[] groupIds = new long[]{scopeGroupId};

		AssetEntryQuery assetEntryQuery = AssetPublisherUtil.getAssetEntryQuery(
			portletPreferences, groupIds);

		assetEntryQuery.setGroupIds(groupIds);

		boolean anyAssetType = GetterUtil.getBoolean(
			portletPreferences.getValue("anyAssetType", null), true);

		if (!anyAssetType) {
			long[] availableClassNameIds =
				AssetRendererFactoryRegistryUtil.getClassNameIds(
					layout.getCompanyId());

			long[] classNameIds = AssetPublisherUtil.getClassNameIds(
				portletPreferences, availableClassNameIds);

			assetEntryQuery.setClassNameIds(classNameIds);
		}

		long[] classTypeIds = GetterUtil.getLongValues(
			portletPreferences.getValues("classTypeIds", null));

		assetEntryQuery.setClassTypeIds(classTypeIds);

		boolean enablePermissions = GetterUtil.getBoolean(
			portletPreferences.getValue("enablePermissions", null));

		assetEntryQuery.setEnablePermissions(enablePermissions);

		assetEntryQuery.setStart(start);
		assetEntryQuery.setEnd(end);

		boolean excludeZeroViewCount = GetterUtil.getBoolean(
			portletPreferences.getValue("excludeZeroViewCount", null));

		assetEntryQuery.setExcludeZeroViewCount(excludeZeroViewCount);

		boolean showOnlyLayoutAssets = GetterUtil.getBoolean(
			portletPreferences.getValue("showOnlyLayoutAssets", null));

		if (showOnlyLayoutAssets) {
			assetEntryQuery.setLayout(layout);
		}

		String orderByColumn1 = GetterUtil.getString(
			portletPreferences.getValue("orderByColumn1", "modifiedDate"));

		assetEntryQuery.setOrderByCol1(orderByColumn1);

		String orderByColumn2 = GetterUtil.getString(
			portletPreferences.getValue("orderByColumn2", "title"));

		assetEntryQuery.setOrderByCol2(orderByColumn2);

		String orderByType1 = GetterUtil.getString(
			portletPreferences.getValue("orderByType1", "DESC"));

		assetEntryQuery.setOrderByType1(orderByType1);

		String orderByType2 = GetterUtil.getString(
			portletPreferences.getValue("orderByType2", "ASC"));

		assetEntryQuery.setOrderByType2(orderByType2);

		if (checkPermission) {
			return AssetEntryServiceUtil.getEntries(assetEntryQuery);
		}
		else {
			return AssetEntryLocalServiceUtil.getEntries(assetEntryQuery);
		}
	}
	
	@SuppressWarnings("serial")
	private void addDefaultADT(RenderRequest renderRequest) throws PortalException, SystemException, ReadOnlyException, ValidatorException, IOException {

		if (ADTThreadLocal.isImported()) {
		
			ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
			
			long classNameId = ClassNameLocalServiceUtil.getClassNameId(AssetEntry.class);
			long scopeGroupId = themeDisplay.getScopeGroupId();
	
	    	// adding adt template
			DDMTemplate ddmTemplate = null;
	    	
			try {
				ddmTemplate = DDMTemplateLocalServiceUtil.getTemplate(scopeGroupId, classNameId, DEFAULT_TEMPLATE_KEY);
			} catch (NoSuchTemplateException nste) {
	
				ServiceContext serviceContext = ServiceContextFactory.getInstance(
						DDMTemplate.class.getName(), renderRequest);
				
				Map<Locale, String> nameMap = new HashMap<Locale, String>() {
					{
						put(LocaleUtil.getSiteDefault(), "Infinite Scroll Template");
					}
				};
	
				Map<Locale, String> descriptionMap = new HashMap<Locale, String>() {
					{
						put(LocaleUtil.getSiteDefault(), "Infinite Scroll Template");
					}
				};;
				
				InputStream inputStream = getClass().getResourceAsStream("dependencies/infinitescroll-assetpublisher-sample.ftl");
				
				String script = new String(FileUtil.getBytes(inputStream));
				
				ddmTemplate = DDMTemplateLocalServiceUtil.addTemplate(themeDisplay.getUserId(),
						themeDisplay.getScopeGroupId(), classNameId, 0, DEFAULT_TEMPLATE_KEY, nameMap,
					descriptionMap, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, StringPool.BLANK, "ftl", script, false,
					false, StringPool.BLANK, null, serviceContext);
				
				PortletPreferences portletPreferences = renderRequest.getPreferences();
				
				portletPreferences.setValue( "displayStyle", String.valueOf(PortletDisplayTemplate.DISPLAY_STYLE_PREFIX + ddmTemplate.getUuid()));
				portletPreferences.store();
				
				ADTThreadLocal.setImported(true);
			}
		}
	}
	
	protected void writeJSON(
			PortletRequest portletRequest, PortletResponse portletResponse,
			Object json)
		throws IOException {

		HttpServletResponse response = PortalUtil.getHttpServletResponse(
				portletResponse);

		response.setContentType(ContentTypes.APPLICATION_JSON);

		ServletResponseUtil.write(response, json.toString());

		response.flushBuffer();
	}

	@Override
	protected void finalize() throws Throwable {
		
		ADTThreadLocal.setImported(false);
		
		super.finalize();
	}
	
	
	
}
