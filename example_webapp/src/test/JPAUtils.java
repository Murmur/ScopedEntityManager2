package test;

import java.util.*;
import java.text.*;
import javax.persistence.*;
import org.apache.openjpa.kernel.StoreContext;

import es.claro.persistence.ScopedEntityManager;

public class JPAUtils {
	public static final String ATTR_DATEFORMAT = "attr.dateformat";

    /**
     * Find all entities
     * @param em    entity manager
     * @param entityClass   entity class
     * @return  list of entities or empty list
     */
    public static<T> List<T> findAll(EntityManager em, Class<T> entityClass) {
        TypedQuery<T> q = em.createQuery("Select bean from " + entityClass.getSimpleName() + " bean", entityClass);
        return q.getResultList();
    }

    public static String cal2db(Calendar val, StoreContext ctx) {
    	// convert calendar to db datetime string to be used in InsertUpdate sql queries
    	// see OrderHeader.java/updated field
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df.format(val.getTime()); // convert to UTC string		
	}
    
    public static Calendar db2cal(String val, StoreContext ctx) {
		// convert from UTC string to utc calendar object
    	// see OrderHeader.java/updated field
        int y = Integer.parseInt(val.substring(0, 4));
        int m = Integer.parseInt(val.substring(5, 7));
        int d = Integer.parseInt(val.substring(8, 10));
		int h = Integer.parseInt(val.substring(11, 13));
		int mi= Integer.parseInt(val.substring(14, 16));
		int s = Integer.parseInt(val.substring(17, 19));
		int millis = (val.length() > 20 ? Integer.parseInt(val.substring(20)) : 0 );
        Calendar cal = Calendar.getInstance( TimeZone.getTimeZone("UTC") );
        cal.set(y, m-1, d, h, mi, s);
        cal.set(Calendar.MILLISECOND, millis);
        //long ts = cal.getTimeInMillis();
        //cal.setTimeZone(TimeZone.getDefault());
        //cal.setTimeInMillis(ts);
        return cal;
    }

    //public static void initEMAttributes(ScopedEntityManager em) {
    //	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); // "2015-06-04T19:25:27+0300"
    //	em.setAttribute(ATTR_DATEFORMAT, df);
    //}
    
    public static String formatDateTime(ScopedEntityManager em, Calendar val) {
    	// reuse an EM context attribute, jsp page's sql query loops may reuse this formatter.
    	SimpleDateFormat df=(SimpleDateFormat)em.getAttribute(ATTR_DATEFORMAT);
    	if(df==null) {
        	df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); // "2015-06-04T19:25:27+0300"
        	em.setAttribute(ATTR_DATEFORMAT, df);
    	}
    	return df.format(val.getTime());
    }

    public static String formatUTCDateTime(ScopedEntityManager em, Calendar val) {
    	// reuse an EM context instance, such as use in a jsp page's query loops
    	SimpleDateFormat df=(SimpleDateFormat)em.getAttribute(ATTR_DATEFORMAT+"utc");
    	if(df==null) {
        	df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); // "2015-06-04T19:25:27+0300"
        	df.setTimeZone(TimeZone.getTimeZone("UTC"));
        	em.setAttribute(ATTR_DATEFORMAT+"utc", df);
    	}
    	return df.format(val.getTime());
    }

	public static String formatDateTime(Calendar val) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(val.getTime()); // convert to default timezone string
	}

	public static String HTMLEncode(String val) {
		if (val == null) return "";
		return XMLEncode(val, false, false);	
	}
	
	public static String XMLEncode(String val, boolean useApos, boolean keepNewlines) {
		int len = val.length();
		StringBuilder sbuf = new StringBuilder(len+10);
		for (int idx=0; idx<len; idx++) {
			char ch = val.charAt(idx);
			switch (ch) {
			case '<': {    sbuf.append("&lt;");     break; }
			case '>': {    sbuf.append("&gt;");     break; }
			case '&': {    sbuf.append("&amp;");    break; }
			case '"': {    sbuf.append("&quot;");   break; }
			case '\'': {   
				if (useApos) sbuf.append("&apos;");
				else sbuf.append("&#39;");
				break;  }
            //case '€': {    sbuf.append("&#8364;"); break; }
			case '\r':
			case '\n':
			case '\t':
			case '\f': {
				if (keepNewlines) {
					sbuf.append(ch);
				} else {
					sbuf.append("&#");
					sbuf.append(Integer.toString(ch));
					sbuf.append(';');
				}
				break; }
			default: {
				sbuf.append(ch);
				}
			}
		}
		return sbuf.toString();
	}
	
}
