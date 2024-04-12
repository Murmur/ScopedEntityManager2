package test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
//import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// Do not map two JPA objects to a same table
@JsonPropertyOrder({ "id", "custId", "comment", "rows", "updated" }) // use fixed json field ordering
@Entity // JPA table object, use field-level access
@Table(name="orderheader2") @Access(AccessType.FIELD)
public class OrderHeader2 {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @JsonProperty("customerId") // customized field name in a json document
    @Column(name="custid")
    private long custId;
	
    @JsonProperty("commentText")
    private String comment;

    @Column(name="updated_utc") @Temporal(TemporalType.TIMESTAMP) 
    // do not use factory functions @Factory("JPAUtils.db2cal") @Externalizer("JPAUtils.cal2db")
    private LocalDateTime updated; // java8 datetime, localTime is without a timezone information

    @JsonInclude(JsonInclude.Include.NON_NULL) // do not write NULL field to a json document
    // OpenJPA ElementJoinColumn custom tag provides
    // one-sided one-to-many link without using extra jointable.
    //    name=foreign field in child table, referencedColumnName=key field in master table
    //@ElementJoinColumn(name="headerid", referencedColumnName="id", nullable=false)
    @OneToMany(targetEntity=test.OrderRow.class,  // not mandatory in typed getter method
            fetch=FetchType.LAZY       // populate list on first getter call
            ,cascade=CascadeType.ALL   // deleting parent bean deletes associated childs
			,orphanRemoval=true		   // delete orphan child rows
			)    
	@JoinColumn(name="headerid", referencedColumnName="id", nullable=false)
	@OrderBy("comment ASC")
    private List<OrderRow> rows;
    
    public long getId() { return id; }
    //public void setId(long id) { this.id = id; }

    public long getCustId() { return custId; }
    public void setCustId(long id) { this.custId = id; }

    public String getComment() { return comment; }
    public void setComment(String val) { comment=val; }

    public Calendar getUpdated() {
    	// convert LocalDateTime(utc in db) to legacy calendar
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis( updated.atZone(ZoneOffset.UTC).toInstant().toEpochMilli() );
    	return cal;
    }
    public void setUpdated(Calendar cal) {
    	// convert legacy Calendar to LocalDateTime(utc in db)
    	updated = cal.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
    }
    
    @Transient // do not save to db
    public String getUpdatedAsUTCString() {
    	String val = updated.toString();
    	if(val.length()==19) return val+"Z"; // "2024-06-10T13:30:45"
    	else if(val.length()<19) return val+":00Z"; // "2024-06-10T13:30" without trailing zeros
    	else return val.substring(0,19)+"Z"; // "2024-06-10T13:30:45.456"
    	//return updated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }
    
    @Transient @JsonIgnore // do not save to db, do not write to json 
    public long getSomeValue() {
    	return System.currentTimeMillis()-123;
    }

    public List<OrderRow> getRows() { return rows; }
    public void setRows(List<OrderRow> items) { rows=items; }
    
}
