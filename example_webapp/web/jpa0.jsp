<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
    page contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"
    import="java.util.*,
		javax.persistence.*,
		es.claro.persistence.PersistenceManager,
		es.claro.persistence.ScopedEntityManager,
		test.*
		"
%><%!

// example of jsp-level helper function
private String getAttr(ScopedEntityManager em, String key) {
	Object oval = em.getAttribute(key);
	if(oval instanceof String) return (String)oval;
	else return oval.toString() +" hashCode "+oval.hashCode();
}

%><%
//if (request.getCharacterEncoding()==null)
//	request.setCharacterEncoding("UTF-8");
System.out.println("------");
ScopedEntityManager em = (ScopedEntityManager)PersistenceManager.getInstance().getEntityManager();
em.setAttribute("test.key1", "This is value1 " + System.currentTimeMillis());
em.setAttribute("test.key2", "This is value2");

List<OrderHeader> list;
List<OrderHeader2> list2;
if ("1".equals(request.getParameter("option"))) {
	Query q = em.createQuery("SELECT bean FROM OrderHeader bean ORDER BY bean.custId DESC, bean.updated ASC");
	list = (List<OrderHeader>)q.getResultList();
	q = em.createQuery("SELECT bean FROM OrderHeader2 bean ORDER BY bean.custId DESC, bean.updated ASC");
	list2 = (List<OrderHeader2>)q.getResultList();	
} else {
	list = JPAUtils.findAll(em, OrderHeader.class);
	list2= JPAUtils.findAll(em, OrderHeader2.class);
}

%><!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
  <title>JPAExample2</title>
</head>
<body>

<p>
EM attributes hashCode=<%= em.hashCode() %><br/>
test.key1 = <%= em.getAttribute("test.key1") %><br/>
<c:forEach var="key" items="<%= em.getAttributeNames() %>">
<jsp:useBean id="key" type="java.lang.String" />
${key} = <%= getAttr(em, key) %><br/>
</c:forEach>
</p>

<b>List Items</b>
<table cellspacing="0" border="1">
  <tr>
    <th>Id</th>
    <th>CustId</th>
    <th>Comment</th>
    <th>Updated</th>
    <th>Updated(em)</th>
    <th>Updated(emutc)</th>
    <th>Rows</th>
  </tr>
  
  <c:forEach var="item" items="<%= list %>">
  <jsp:useBean id="item" type="test.OrderHeader" />
  <tr>
  	<td>${item.id}</td>
  	<td>${item.custId}</td>
  	<td><%= JPAUtils.HTMLEncode(item.getComment()) %></td>
  	<td><%= JPAUtils.formatDateTime(item.getUpdated()) %></td>
  	<td><%= JPAUtils.formatDateTime(em, item.getUpdated()) %></td>
  	<td><%= JPAUtils.formatUTCDateTime(em, item.getUpdated()) %></td>
	<td>
  		<c:forEach var="itemRow" items="${item.rows}">
  		${itemRow.id}/${itemRow.headerId} ${itemRow.comment},
  		</c:forEach>
  	</td>	
  </tr>
  </c:forEach>
 
  <tr>
    <td colspan="10">OrderHeader2 items</td>
  </tr>
  <c:forEach var="item2" items="<%= list2 %>">
  <jsp:useBean id="item2" type="test.OrderHeader2" />
  <tr>
  	<td>${item2.id}</td>
  	<td>${item2.custId}</td>
  	<td><%= JPAUtils.HTMLEncode(item2.getComment()) %></td>
  	<td><%= JPAUtils.formatDateTime(item2.getUpdated()) %></td>
  	<td><%= JPAUtils.formatDateTime(em, item2.getUpdated()) %></td>
  	<td><%= JPAUtils.formatUTCDateTime(em, item2.getUpdated()) %></td>
	<td>
  		<c:forEach var="itemRow" items="${item2.rows}">
  		${itemRow.id}/${itemRow.headerId} ${itemRow.comment},
  		</c:forEach>
  	</td>	
  </tr>
  </c:forEach>
  
  
  
</table>

</body>
</html>