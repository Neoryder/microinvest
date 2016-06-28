package com.jar.microinvest.grouporder;

import com.google.gson.Gson;
import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class GroupOrderService {

    private final DB db;
    private final DBCollection collection;

    public GroupOrderService(DB db) {
        this.db = db;
        this.collection = db.getCollection("groupOrder");
    }

    public List<GroupOrder> findAll() {
        System.out.println("findAll");
        List<GroupOrder> groupOrders = new ArrayList<>();
        DBCursor dbObjects = collection.find();
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            groupOrders.add(new GroupOrder((BasicDBObject) dbObject));
        }
        return groupOrders;
    }

    public void createNewGroupOrder(String body) {

        GroupOrder gorder = new Gson().fromJson(body, GroupOrder.class);
        System.out.println("createNewGroupOrder");
	
        System.out.println(body);
        
        collection.insert(new BasicDBObject("title", gorder.getType())
            .append("stock", gorder.getStock()).append("type", gorder.getType())
            .append("amountInBucket", gorder.getAmountInBucket().toPlainString())
            .append("price", gorder.getPrice().toPlainString())
            .append("done", gorder.isDone()).append("createdOn", new Date()));
            
    }
    

    public GroupOrder find(String id) {
        System.out.println("find");
        return new GroupOrder((BasicDBObject) collection.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }
    
    public GroupOrder update(String groupOrderId, String body) {
        System.out.println("update");
        GroupOrder groupOrder = new Gson().fromJson(body, GroupOrder.class);
        collection.update(new BasicDBObject("_id", new ObjectId(groupOrderId)), new BasicDBObject("$set", new BasicDBObject("done", groupOrder.isDone())));
        return this.find(groupOrderId);
    }
}
