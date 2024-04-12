<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    page contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"
    import="java.util.*,
		javax.persistence.*,
		es.claro.persistence.PersistenceManager,
		test.*
		"
%><%!

private OrderHeader createTestBean(int custId) {
	OrderHeader bean = new OrderHeader();
	bean.setCustId(custId);
	bean.setComment("JSP Bean " + (1+Double.valueOf(Math.random()*(10-1)).intValue()) );
	bean.setUpdated(Calendar.getInstance());
	return bean;
}

private OrderHeader2 createTestBean2(int custId) {
	OrderHeader2 bean = new OrderHeader2();
	bean.setCustId(custId);
	bean.setComment("JSP Bean " + (1+Double.valueOf(Math.random()*(10-1)).intValue()) );
	bean.setUpdated(Calendar.getInstance());
	return bean;
}


public OrderRow createTestRow(int idx, int qty) {
	OrderRow bean = new OrderRow();
	bean.setComment("Row "+idx+" " + (1+Double.valueOf(Math.random()*(10-1)).intValue()) );
	bean.setQuantity(qty);
	bean.setUpdated(Calendar.getInstance());
	return bean;
}

%><%
EntityManager em = PersistenceManager.getInstance().getEntityManager();
em.getTransaction().begin();

OrderHeader ohnew = createTestBean(101);
em.persist(ohnew);

ohnew = createTestBean(201);
ohnew.setRows(new ArrayList<OrderRow>());
ohnew.getRows().add( createTestRow(1, 11) );
ohnew.getRows().add( createTestRow(2, 12) );
em.persist(ohnew);

ohnew = createTestBean(301);
ohnew.setComment(ohnew.getComment()
	+ " \uD83D\uDE00" // grinning face https://www.fileformat.info/info/unicode/char/1f600/index.htm
	+ " \uD834\uDD60" // musical symbol eight note
	+ " \uD83D\uDCEB" // closed mailbox with raised flag
);
ohnew.setRows(new ArrayList<OrderRow>());
ohnew.getRows().add( createTestRow(1, 1) );
ohnew.getRows().add( createTestRow(2, 2) );
ohnew.getRows().add( createTestRow(3, 3) );
em.persist(ohnew);

// OrderHeader2 test rows
em.persist( createTestBean2(101) );
em.persist( createTestBean2(201) );

OrderHeader2 ohnew2 = createTestBean2(301);
ohnew2.setComment(ohnew2.getComment()
	+ " \uD83D\uDE00" // grinning face https://www.fileformat.info/info/unicode/char/1f600/index.htm
	+ " \uD834\uDD60" // musical symbol eight note
	+ " \uD83D\uDCEB" // closed mailbox with raised flag
);
ohnew2.setRows(new ArrayList<OrderRow>());
ohnew2.getRows().add( createTestRow(1, 1) );
ohnew2.getRows().add( createTestRow(2, 2) );
ohnew2.getRows().add( createTestRow(3, 3) );
em.persist(ohnew2);

for(int idx=1; idx<10; idx++) {
	em.persist( createTestBean(100+idx) );
	em.persist( createTestBean2(100+idx) );
}

em.getTransaction().commit();

%><!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
  <title>JPAExample2</title>
</head>
<body>

<b>List Items</b>
<table cellspacing="0" border="1">
  <c:forEach var="item" items="<%= JPAUtils.findAll(em, OrderHeader.class) %>">
  <jsp:useBean id="item" type="test.OrderHeader" />
  <tr>
  	<td>${item.id}</td>
  	<td>${item.custId}</td>
  	<td><%= JPAUtils.HTMLEncode(item.getComment()) %></td>
  	<td><%= JPAUtils.formatDateTime(item.getUpdated()) %></td>
	<td>childcount ${item.rows.size()}</td>
  </tr>
  </c:forEach>
</table>

</body>
</html>