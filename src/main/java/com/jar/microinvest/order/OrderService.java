package com.jar.microinvest.order;

import com.google.gson.Gson;
import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class OrderService {

    private final DB db;
    private final DBCollection collection;

    public OrderService(DB db) {
        this.db = db;
        this.collection = db.getCollection("ordersCollection");
    }

    public List<Order> findAll() {
        System.out.println("findAll");
        List<Order> holdings = new ArrayList<>();
        DBCursor dbObjects = collection.find();
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            holdings.add(new Order((BasicDBObject) dbObject));
        }
        return holdings;
    }

    public void createNewOrder(String body) {
        System.out.println("createNewOrder");
        Order order = new Gson().fromJson(body, Order.class);
        collection.insert(new BasicDBObject("title", order.getTitle()).append("done", order.isDone()).append("createdOn", new Date()));
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
}
