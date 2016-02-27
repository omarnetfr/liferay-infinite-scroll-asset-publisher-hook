<#assign liferay_ui = taglibLiferayHash["/WEB-INF/tld/liferay-ui.tld"] />

<#assign dateFormat = "dd/MM/yyyy" />

<#assign HttpUtil = staticUtil["com.liferay.portal.kernel.util.HttpUtil"] />

<div class="asset-container">
	<div class="asset-title"> 
		<h2> Les assets </h2> 
	</div>
	<div class="asset-content"> 
		<#if entries?has_content>
		<ul class="asset-list"> 
			<#list entries as entry>				
				<#assign entryTitle = htmlUtil.escape(entry.getTitle(locale)) />

				<#assign assetRenderer = entry.getAssetRenderer() />
				
				<#assign viewURL = assetPublisherHelper.getAssetViewURL(renderRequest, renderResponse, entry) />

				<#if assetLinkBehavior?? && assetLinkBehavior != "showFullContent">
					<#assign viewURL = assetRenderer.getURLViewInContext(renderRequest, renderResponse, viewURL) />
				
				</#if>
				
				<li class="asset-item">
					<div class="asset-item-body"> 
						<strong>${entryTitle}</strong> <br> 
						<small class="asset-item-date">${dateUtil.getDate(entry.getPublishDate(), dateFormat, locale)}</small> 
						<div class="well asset-item-summary"> 
							${htmlUtil.escape(stringUtil.shorten(assetRenderer.getSummary(locale), 380))}
						</div>
						<div class="pull-right"> 
							<a class="btn btn-xs btn-white" href="${viewURL}"> <@liferay.language key="read-more" /><span class="hide-accessible"><@liferay.language key="about" />${entryTitle}</span> &raquo; </a> 
						</div>
					</div>
				</li> 
			</#list>
		</ul> 
		</#if>
	</div> 
</div>