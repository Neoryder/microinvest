package com.jar.microinvest.grouporder;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by shekhargulati on 09/06/14.
 */
public class GroupOrder {

    private String id;
    private String stock;
    private String type;
    private String title;
    private long amountInBucket;
    private long stockPrice;
    private boolean done;
    private Date createdOn = new Date();

    public GroupOrder(BasicDBObject dbObject) {
        this.id = ((ObjectId) dbObject.get("_id")).toString();
        this.stock = dbObject.getString("stock");
        this.type = dbObject.getString("type");
        this.title = dbObject.getString("title");
        this.amountInBucket = dbObject.getBigDecimalVersion("amountInBucket");
        this.stockPrice = dbObject.getBigDecimalVersion("stockPrice");
        this.done = dbObject.getBoolean("done"); 
        this.createdOn = dbObject.getDate("createdOn");
    }
    
    public BigDecimal getBigDecimalVersion(String bdString){
        if(bdString!=null){
            try {
                BigDecimal tmp =  new BigDecimal(bdString);
                return tmp;
            } catch (Exception e){
                e.printStackTrace();
                return BigDecimal.ZERO;
            }
        } else {
            return BigDecimal.ZERO;
        }


    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public Date getCreatedOn() {
        return createdOn;
    }
}
