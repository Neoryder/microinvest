package com.jar.microinvest.order;

import com.google.gson.Gson;
import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.*;

/**
 *
 */
public class OrderService {

    private final DB db;
    private final DBCollection collection;
    private final DBCollection gorderCollection;

    public OrderService(DB db) {
        this.db = db;
        this.collection = db.getCollection("ordersCollection");
        this.gorderCollection = db.getCollection("groupOrder");
    }

    public List<Order> findAll() {
        System.out.println("findAll");
        List<Order> orderz = new ArrayList<>();
        DBCursor dbObjects = collection.find();
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            orderz.add(new Order((BasicDBObject) dbObject));
        }
        System.out.println(orderz);
        return orderz;
    }

    public void createNewOrder(String body) {

        Order order = new Gson().fromJson(body, Order.class);
        System.out.println("createOrUpdateGroupOrder");
        
        BasicDBObject newDocument = new BasicDBObject();
    	newDocument.append("$set", new BasicDBObject().append("stock", order.getStock()))
    	    .append("$set", new BasicDBObject().append("type", order.getType()))
    	    .append("$set", new BasicDBObject().append("stockPrice", order.getPrice().toPlainString()))
    	    //.append("$inc", new BasicDBObject().append("amountInBucket", order.getTotal().toPlainString()))
    	    .append("$set", new BasicDBObject().append("done", false));
    	
    	BasicDBObject searchQuery = new BasicDBObject()
        	.append("stock", order.getStock())
        	.append("type", order.getType())
        	.append("done", false);
    
    	gorderCollection.update(searchQuery, newDocument);
	
        System.out.println("createNewOrder");
        System.out.println(body);
        
        collection.insert(new BasicDBObject("title", order.getType())
            .append("stock", order.getStock()).append("type", order.getType())
            .append("quantity", order.getQuantity().toPlainString())
            .append("price", order.getPrice().toPlainString())
            .append("total", order.getTotal().toPlainString())
            .append("originalOrderAmount", order.getTotal().toPlainString())
            .append("done", order.isDone()).append("createdOn", new Date()));
            
    }

    public Order find(String id) {
        System.out.println("find");
        return new Order((BasicDBObject) collection.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }

    public Order update(String orderId, String body) {
        System.out.println("update");
        Order order = new Gson().fromJson(body, Order.class);
        collection.update(new BasicDBObject("_id", new ObjectId(orderId)), new BasicDBObject("$set", new BasicDBObject("done", order.isDone())));
        return this.find(orderId);
    }
    
    public void remove(String orderId) {
        System.out.println("remove");
        collection.remove(new BasicDBObject("_id", new ObjectId(orderId)));
    }
    
    public List<Order> summarizeOrders() {
        System.out.println("summarizeOrders");
        List<Order> orderz = new ArrayList<>();
        Map<String,Order> orderMap = new HashMap<>();
        DBCursor dbObjects = collection.find();
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            Order order = new Order((BasicDBObject) dbObject);
            String stock = order.getStock();
            if(orderMap.containsKey(stock)){
                Order temp = orderMap.get(stock);
                temp.setTotal(temp.getTotal().add(order.getTotal()));
                orderMap.put(stock,temp);
            } else {
                orderMap.put(stock,order);
            }
        }
        orderz.addAll(orderMap.values());
        return orderz;
    }

}
