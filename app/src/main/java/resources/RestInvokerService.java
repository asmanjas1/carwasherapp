package resources;

import java.util.Map;

import bean.Carwasher;
import bean.CarwasherAddress;
import bean.CarwasherFirebase;
import bean.Orders;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RestInvokerService {

    @POST("/carwasherController/signUp")
    Call<Map<String, Object>> createCarwasher(@Body Carwasher carwasher);

    @POST("/carwasherController/login")
    Call<Map<String, Object>> loginCarwasher(@Body Carwasher carwasher);

    @POST("/carwasherController/saveaddress")
    Call<Map<String, Object>> addAddress(@Body CarwasherAddress carwasherAddress);

    @GET("/orderController/getInProgressOrdersForCarwasher/{consumerId}")
    Call<Map<String, Object>> getInprogressOrdersForCarwasher(@Path("consumerId") Integer consumerId);

    @GET("/orderController/getCompletedOrdersForCarwasher/{consumerId}")
    Call<Map<String, Object>> getCompletedOrdersForCarwasher(@Path("consumerId") Integer consumerId);

    @GET("/orderController/getAllNewOrdersForCarwasher/{consumerId}/{city}/{state}")
    Call<Map<String, Object>> getNewOrdersForCarwasher(@Path("consumerId") Integer consumerId,
         @Path("city") String city,@Path("state") String state);

    @POST("/carwasherController/saveFirebaseToken")
    Call<Map<String, Object>> saveFirevaseToken(@Body CarwasherFirebase carwasherFirebase);

    @GET("/orderController/getAllDetailsForOrderId/{orderId}")
    Call<Map<String, Object>> getAllDetailsForOrderId(@Path("orderId") Integer orderId);

    @PUT("/orderController/confirmOrderByCarwasher/{orderId}/{carwasherId}/{consumerId}")
    Call<Map<String, Object>> confirmOrderByCarwasher(@Path("orderId") Integer orderId,
                                                      @Path("carwasherId") Integer carwasherId,
                                                      @Path("consumerId") Integer consumerId);

    @PUT("/orderController/completeOrderByOrderId/{orderId}/{consumerId}")
    Call<Map<String, Object>> completeOrderByOrderId(@Path("orderId") Integer orderId,@Path("consumerId") Integer consumerId);
}
