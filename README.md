ScopedEntityManager2
======================
Java Persistence API(JPA_2.2) wrapper for the servlet container such as Apache Tomcat.
Automanage Entity Manager lifetime within a http request context.

Inspiration
-----------
This wrapper is forked from [codegoogle scoped-entitymanager](https://code.google.com/p/scoped-entitymanager) project.
It's no longer maintained and last changes were 2007 and JPA1.x interface. I took the code, studied,
implementation changes to simplify things, added JPA2.x interface, support for two or more automanaged
entity manager instances within a single thread.

Features
--------
- **automanage** - close entity managers at the end of http request context.
- **named instance** - named instances for custom use-case, may have 2..n instances per request context.
- **non-managed owner** - custom owner id if instance should be kept available and open across http requests lifetime.
- **custom attributes** - custom attributes within entity manager to keep some application-level housekeeping values.
- **rollback** - automanaged instance is rollbacked if active transaction was not committed.

ScopedEntityManager wrapper uses servlet listener for lifetime management,
entity managers are automatically closed at the end of http request. Each request may
have one or more automanaged instances depending on how an application retrieves instances.

Application usually has just one EntityManager instance per http request-response.

Instances are owned by the calling threadID and normal single threaded http handler code
creates just one entity manager instance, getter returns the same instance within a thread.
This provides application a transparent single transaction context per http request.

Application may create two or more instances if that is required, all instances are
released at the end of http request lifetime.

Public interface
-----------------
Public methods retrieving existing or new entity managers. Methods are thread-safe.

**PersistenceManager PersistenceManager.getInstance()**<br/>
Returns singleton of PersistenceManager class.

**EntityManager getEntityManager()**<br/>
Get default entity manager which is owned by calling threadID, each 
concecutive call within a thread receives same instance.

**EntityManager getEntityManager(long ownerId, String dbName, String emName, Map map)**<br/>
Get or create named instance owned by given ownerID.<br/>
@ownerId  owner id which usually is threadID 1...n, this may be -n..-1 custom id but
          then application must close those non-automanaged instances. <br/>
@dbName   PersistenceUnit name in persistence.xml file, always NULL current implementation uses first PU name<br/>
@emName   instance name to be get or created on first call, NULL always creates new instance<br/>
@map      JPA entity manager constructor options or NULL<br/>

**void closeEntityManagers(long ownerId)**<br/>
Close entity managers by the owner id. This is automatically called for http requests
so webapp usually does not need to use this method.
If application created a custom ownerID(-n..-1) entity managers then use this to close instances.
All open instances are closed on a web application undeployment.

Configuration
-------------
Insert http request filter in WEB-INF/web.xml to enable an automanaged lifetime management.
 PersistenceUnit name is read from persistence.xml at webapp start (jdbc connection name),
all instances closed at webapp stop, thread-level instances closed at http request.

```
  <listener>
    <description>Initialize global JPA resources, see also ScopedRequestFilter</description>
    <listener-class>es.claro.persistence.ScopedContextListener</listener-class>
  </listener>
  
  <filter>
    <filter-name>ScopedRequestFilter</filter-name>
    <filter-class>es.claro.persistence.ScopedRequestFilter</filter-class>
  </filter>
  <filter-mapping>
    <filter-name>ScopedRequestFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```

Example
-------
Servlet or .jsp script example using automanaged EM instance.
```
// no need to call em.close() for automanaged EM instances
EntityManager em = PersistenceManager.getInstance().getEntityManager();
Query qry = em.createQuery("SELECT bean FROM OrderHeader bean ORDER BY bean.custId DESC, bean.updated ASC");
List<OrderHeader> beans = (List<OrderHeader>)qry.getResultList();
- - - 
// EM is automatically rollbacked if servlet exception was thrown or commit() was not invoked
EntityManager em = PersistenceManager.getInstance().getEntityManager();
OrderHeader bean = em.find(OrderHeader.class, 2);
em.getTransaction().begin();
bean.setComment("this is new comment text " + System.currentTimeMillis() );
em.getTransaction().commit();
```

Web application in a example_webapp folder has .jsp scripts, JSTL tags, OpenJPA and
Jersey+Jackson json libraries.
Application is using `jakarta.*` package imports so Tomcat10 is the minimum version.

Dependencies
------------
Servlet container libraries.<br/>
JPA implementation [Apache OpenJPA](http://openjpa.apache.org/) libraries.<br/>

Compatibility tested on
-----------------------
Tomcat_10.1.18, JavaJDK_21.0.2, OpenJPA_3.1.3<br/>

TODO
----
**Support two or more PersistenceUnit names**<br/>
Current implementation uses first PU name from persistence.xml file, its not possible to
use other database connections.
