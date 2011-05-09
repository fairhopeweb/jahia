<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="facet" uri="http://www.jahia.org/tags/facetLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<template:addResources type="javascript" resources="jquery.js"/>
<template:addResources type="css" resources="listsites.css"/>

    <jcr:sql var="result" sql="select * from [jnt:virtualsite] as site where isdescendantnode(site,'/sites') order by site.[jcr:created] desc" limit="${currentNode.properties['numberMaxOfSitesDisplayed'].string}"/>
    <ul class="list-sites">
        <c:forEach items="${result.nodes}" var="node">

            <c:choose>
                <c:when test="${currentNode.properties.typeOfContent.string eq 'contents'}">
                    <c:set var="page" value="/contents"/>
                </c:when>
                <c:when test="${currentNode.properties.typeOfContent.string eq 'files'}">
                    <c:set var="page" value="/files"/>
                </c:when>
                <c:otherwise>
                    <c:set var="page" value="/${node.home.name}"/>
                </c:otherwise>
            </c:choose>

            <c:if test="${not empty node.home and (jcr:hasPermission(node.home,'editModeAccess') || jcr:hasPermission(node.home,'contributeModeAccess') || node.home.properties['j:published'].boolean)}">
                <li class="listsiteicon">${node.displayableName}
                    <c:set var="siteId" value="${node.properties['j:siteId'].long}"/>
                    <c:if test="${currentNode.properties.edit.boolean && jcr:hasPermission(node.home,'administrationAccess')}">
                        <img src="<c:url value='/icons/admin.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><a href="<c:url value='/administration/?do=change&changesite=${siteId}#sites'/>"><fmt:message key="label.administration"/></a>
                    </c:if>
                    <c:if test="${currentNode.properties.edit.boolean && jcr:hasPermission(node.home,'editModeAccess')}">
                        <img src="<c:url value='/icons/editMode.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><a href="<c:url value='${url.baseEdit}${node.path}${page}.html'/>"><fmt:message key="label.editMode"/></a>
                    </c:if>
                    <c:if test="${currentNode.properties.contribute.boolean  && jcr:hasPermission(node.home,'contributeModeAccess')}">
                        <c:url value='/icons/contribute.png' var="icon"/>
                        <c:if test="${currentNode.properties.typeOfContent.string eq 'contents'}">
                            <c:url value='/icons/content-manager-1616.png' var="icon"/>
                        </c:if>
                        <img src="${icon}" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><a href="<c:url value='${url.baseContribute}${node.path}${page}.html'/>"><fmt:message key="label.contribute"/></a>
                    </c:if>
                    <c:if test="${currentNode.properties.preview.boolean && jcr:hasPermission(node.home,'jcr:read_default')}">
                        <img src="<c:url value='/icons/preview.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><a href="<c:url value='${url.basePreview}${node.path}${page}.html'/>"><fmt:message key="label.preview"/></a>
                    </c:if>
                    <c:if test="${currentNode.properties.live.boolean && node.home.properties['j:published'].boolean}">
                        <img src="<c:url value='/icons/live.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><a href="<c:url value='${url.baseLive}${node.path}${page}.html'/>"><fmt:message key="label.live"/></a>
                    </c:if>
                </li>
            </c:if>
        </c:forEach>
    </ul>
