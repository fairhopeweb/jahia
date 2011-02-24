<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<template:addResources type="css" resources="searchresults.css"/>

<c:if test="${renderContext.editMode}">
	<fieldset>
		<legend>${fn:escapeXml(jcr:label(currentNode.primaryNodeType,currentResource.locale))}</legend>
</c:if>
<s:results var="resultsHits">
	<jcr:nodeProperty name="jcr:title" node="${currentNode}" var="title"/>
	<jcr:nodeProperty name="autoSuggest" node="${currentNode}" var="autoSuggest"/>
	<div id="${currentNode.UUID}">
	<div class="resultsList">
        <c:if test="${moduleMap.begin == 0 && autoSuggest.boolean}">
        	<%-- spelling auto suggestions are enabled --%>
        	<jcr:nodeProperty name="autoSuggestMinimumHitCount" node="${currentNode}" var="autoSuggestMinimumHitCount"/>
        	<jcr:nodeProperty name="autoSuggestHitCount" node="${currentNode}" var="autoSuggestHitCount"/>
        	<c:if test="${count <= functions:default(autoSuggestMinimumHitCount.long, 2)}">
        		<%-- the number of original results is less than the configured threshold, we can start auto-suggest  --%>
	        	<s:suggestions>
	        		<%-- we have a suggestion --%>
		        	<c:if test="${suggestedCount > count}">
		        		<%-- found more hits for the suggestion than the original query brings --%>
						<h4>
							<fmt:message key="search.results.didYouMean" />:&nbsp;<a href="<s:suggestedSearchUrl/>"><em>${fn:escapeXml(suggestion.suggestedQuery)}</em></a>.&nbsp;
							<fmt:message key="search.results.didYouMean.topResults"><fmt:param value="${functions:min(functions:default(autoSuggestHitCount.long, 2), suggestedCount)}" /></fmt:message>
						</h4>
						<ol>
							<s:resultIterator begin="0" end="${functions:default(autoSuggestHitCount.long, 2) - 1}">
								<li><%@ include file="searchHit.jspf" %></li>
							</s:resultIterator>
						</ol>
						<hr/>
						<h4><fmt:message key="search.results.didYouMean.resultsFor"/>:&nbsp;<strong>${fn:escapeXml(suggestion.originalQuery)}</strong></h4>
			        </c:if>
	        	</s:suggestions>
        	</c:if>
        </c:if>
		<c:if test="${count > 0}">
            <c:set var="termKey" value="src_terms[0].term"/>
	    	<h3><fmt:message key="search.results.found"><fmt:param value="${fn:escapeXml(param[termKey])}"/><fmt:param value="${count}"/></fmt:message></h3>
            <c:set target="${moduleMap}" property="listTotalSize" value="${count}" />
            <c:set var="beginName" value="begin_${currentNode.identifier}"/>
            <c:set var="endName" value="end_${currentNode.identifier}"/>
            <c:if test="${not empty requestScope[beginName]}">
                <c:set target="${moduleMap}" property="begin" value="${requestScope[beginName]}"/>
            </c:if>
            <c:if test="${not empty requestScope[endName]}">
                <c:set target="${moduleMap}" property="end" value="${requestScope[endName]}"/>
            </c:if>
        	<ol start="${moduleMap.begin+1}">
				<s:resultIterator begin="${moduleMap.begin}" end="${moduleMap.end}" varStatus="status">
					<li><%--<span>${status.index+1}.</span>--%><%@ include file="searchHit.jspf" %></li>
				</s:resultIterator>
	        </ol>
            <div class="clear"></div>
		</c:if>
        <c:if test="${count == 0}">
        	<h4><fmt:message key="search.results.no.results"/></h4>
		</c:if>
    </div>
    </div>
</s:results>
<c:if test="${renderContext.editMode}">
	</fieldset>
</c:if>
