package com.jar.microinvest.order;


import com.jar.microinvest.JsonTransformer;
import spark.Spark;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by shekhargulati on 09/06/14.
 */
public class OrderResource {

    private static final String API_CONTEXT = "/api/v1";

    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        post(API_CONTEXT + "/orderz", "application/json", (request, response) -> {
            orderService.createNewOrder(request.body());
            response.status(201);
            return response;
        }, new JsonTransformer());


        get(API_CONTEXT + "/orderz/:id", "application/json", (request, response)

                -> orderService.find(request.params(":id")), new JsonTransformer());

        get(API_CONTEXT + "/orderz", "application/json", (request, response)

                -> orderService.findAll(), new JsonTransformer());

        get(API_CONTEXT + "/orderzSummary", "application/json", (request, response)

                -> orderService.summarizeOrders(), new JsonTransformer());

        Spark.put(API_CONTEXT + "/orderz/:id", "application/json", (request, response)

                -> {orderService.remove(request.params(":id"));
                    response.status(201);
                    return response;
                }, new JsonTransformer());
                
    }


}
