<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="text/javascript">
    function ShowHideLayer(boxID) {
	/* Obtain reference for the selected boxID layer and its button */
	var box = document.getElementById("collapseBox"+boxID);

	/* If the selected box is currently invisible, show it */
	if(box.style.display == "none" || box.style.display=="") {
		box.style.display = "block";
	}
	/* otherwise hide it */
	else {
		box.style.display = "none";
	}
}
</script>


        <td rowspan="2"><jcr:nodeProperty var="picture" node="${currentNode}" name="picture"/>
    <c:if test="${not empty picture}">
        <div class="peoplePhoto">
            <jcr:nodeProperty node="${currentNode}" name="lastname" var="lastname"/>
            <img src="${picture.node.thumbnailUrls['thumbnail']}" alt="${lastname} picture" width="85" height="85"></div>
    </c:if></td>
        <td><jcr:nodeProperty node="${currentNode}" name="peopleFirstname"/></td>
        <td><jcr:nodeProperty node="${currentNode}" name="lastname"/></td>
        <td><jcr:nodeProperty node="${currentNode}" name="function"/> (<jcr:nodeProperty node="${currentNode}" name="businessUnit"/>)</td>
        <td><jcr:nodeProperty node="${currentNode}" name="email" var="email"/><a href='mailto:${email.string}'>${email.string}</a></td>
    </tr>
    <tr><td colspan="3"><jcr:nodeProperty node="${currentNode}" name="biography"/></td></tr>


        
