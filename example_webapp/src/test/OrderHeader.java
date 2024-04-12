package test;

import java.util.*;
import javax.persistence.*;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "custId", "comment", "rows", "updated" }) // use fixed json field ordering
@Entity // JPA table object, use field-level access, add class to "WEB-INF/classes/META-INF/persistence.xml " file
@Table(name="orderheader") @Access(AccessType.FIELD)
public class OrderHeader {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @JsonProperty("customerId") // customized field name in a json document
    @Column(name="custid")
    private long custId;
	
    @JsonProperty("commentText")
    private String comment;

    @Column(name="updated_utc") @Temporal(TemporalType.TIMESTAMP) 
    @Factory("JPAUtils.db2cal") @Externalizer("JPAUtils.cal2db")
    private Calendar updated;

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

    public Calendar getUpdated() { return updated; }
    public void setUpdated(Calendar cal) { updated=cal; }

    public List<OrderRow> getRows() { return rows; }
    public void setRows(List<OrderRow> items) { rows=items; }
    
}
