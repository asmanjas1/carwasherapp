package carwasher.com.carwasher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Map;

import bean.Carwasher;
import bean.Consumer;
import bean.ConsumerAddress;
import bean.Orders;
import bean.Vehicle;
import butterknife.ButterKnife;
import butterknife.InjectView;
import resources.CarConstant;
import resources.RestClient;
import resources.RestInvokerService;
import resources.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusActivity extends AppCompatActivity {

    @InjectView(R.id.textViewOrderStatusId) TextView textViewOrderStatusId;
    @InjectView(R.id.textViewOrderStatusDate) TextView textViewOrderStatusDate;
    @InjectView(R.id.textViewOrderStatusAmount) TextView textViewOrderStatusAmount;
    @InjectView(R.id.textViewOrderStatusStatus) TextView textViewOrderStatusStatus;
    @InjectView(R.id.textViewOrderStatusPaymentStatus) TextView textViewOrderStatusPaymentStatus;
    @InjectView(R.id.textViewOrderStatusVehicleName) TextView textViewOrderStatusVehicleName;
    @InjectView(R.id.textViewOrderStatusVehicleNumber) TextView textViewOrderStatusVehicleNumber;
    @InjectView(R.id.textViewOrderStatusAddressLine) TextView textViewOrderStatusAddressLine;
    @InjectView(R.id.textViewOrderStatusAddressLocalityLandmark) TextView textViewOrderStatusAddressLocalityLandmark;
    @InjectView(R.id.textViewOrderStatusAddressTotal) TextView textViewOrderStatusAddressTotal;
    @InjectView(R.id.textViewOrderStatusConsumerName) TextView textViewOrderStatusConsumerName;
    @InjectView(R.id.textViewOrderStatusConsumerPhoneNumber) Button textViewOrderStatusConsumerPhoneNumber;
    @InjectView(R.id.textViewOrderStatusOrderActions) Button textViewOrderStatusOrderActions;
    @InjectView(R.id.orderStatusOrder) CardView orderStatusOrder;
    @InjectView(R.id.orderStatusVehicle) CardView orderStatusVehicle;
    @InjectView(R.id.orderStatusAddress) CardView orderStatusAddress;
    @InjectView(R.id.orderStatusConsumer) CardView orderStatusConsumer;
    @InjectView(R.id.orderStatusOrderActions) CardView orderStatusOrderActions;
    @InjectView(R.id.order_summary) TextView order_summary;

    Gson gson = new Gson();
    public ProgressDialog progressDialog;
    public Integer orderId;
    public Integer consumerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        Orders order =null;
        if (intent != null) {
            String orderJson = intent.getStringExtra("orderSelected");
            order = gson.fromJson(orderJson, Orders.class);
        }
        orderId = order.getOrderId();
        if(order.getConsumer() != null){
            consumerId = order.getConsumer().getConsumerId();
        }
        loadAllDetailsForOrder(orderId);
    }

    public void loadAllDetailsForOrder(Integer id){
        order_summary.setVisibility(View.GONE);
        orderStatusOrder.setVisibility(View.GONE);
        orderStatusVehicle.setVisibility(View.GONE);
        orderStatusAddress.setVisibility(View.GONE);
        orderStatusConsumer.setVisibility(View.GONE);
        orderStatusOrderActions.setVisibility(View.GONE);

        progressDialog =  CarConstant.getProgressDialog(this,"Loading...");
        progressDialog.show();

        RestInvokerService restInvokerService = RestClient.getClient().create(RestInvokerService.class);

        Call<Map<String, Object>> call = restInvokerService.getAllDetailsForOrderId(id);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> map = response.body();
                if( map.get("resCode").equals(200.0)){
                    String ss = map.get("data").toString();
                    Log.d("asnasnas",ss);
                    Orders orders = gson.fromJson(ss, Orders.class);
                    setAllFields(orders);
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressDialog.dismiss();

            }
        });
    }

    public void setAllFields(Orders orders){
        Carwasher carwasher = SaveSharedPreference.getCarwasherFromGson(this);
        Carwasher orderCarwashher = orders.getCarwasher();
        if( orderCarwashher != null){
            Integer orderCarwashherID = orderCarwashher.getCarwasherId();
            if( orderCarwashherID != null){
                if( !carwasher.getCarwasherId().equals(orderCarwashherID)){
                    Toast.makeText(getApplicationContext(),"Order is already accepted by other person..",Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }
        }



        String orderStatus = null;
        if(orders != null){
            orderStatus = orders.getOrderStatus();
            order_summary.setVisibility(View.VISIBLE);
            orderStatusOrder.setVisibility(View.VISIBLE);
            textViewOrderStatusId.setText("Order Id: "+String.valueOf(orders.getOrderId()));
            textViewOrderStatusDate.setText("Order Date: "+orders.getOrderDate());
            textViewOrderStatusAmount.setText("Order Amount: "+String.valueOf(orders.getOrderAmount()));
            textViewOrderStatusStatus.setText("Order Status: "+ orderStatus);
            textViewOrderStatusPaymentStatus.setText("Payment Status: "+orders.getOrderPaymentStatus());
        }


        Vehicle vehicle = orders.getOrderVehicle();
        if(vehicle != null){
            orderStatusVehicle.setVisibility(View.VISIBLE);
            textViewOrderStatusVehicleName.setText("Vehicle Name: "+vehicle.getVehicleName());
            textViewOrderStatusVehicleNumber.setText("Vehicle Number: "+vehicle.getVehicleNumber());
        }
        ConsumerAddress consumerAddress = orders.getOrderAddress();
        if(consumerAddress != null){
            orderStatusAddress.setVisibility(View.VISIBLE);
            textViewOrderStatusAddressLine.setText(consumerAddress.getAddressLine());
            textViewOrderStatusAddressLocalityLandmark.setText(consumerAddress.getLocality());
            textViewOrderStatusAddressTotal.setText(consumerAddress.getCity()+" "+consumerAddress.getState()+" "+consumerAddress.getPincode());
        }

        Consumer consumer = orders.getConsumer();
        if(consumer != null){
            orderStatusConsumer.setVisibility(View.VISIBLE);
            textViewOrderStatusConsumerName.setText("Name: "+consumer.getName());
            if( orderStatus != null && orderStatus.equals("In Progress")){
                textViewOrderStatusConsumerPhoneNumber.setVisibility(View.VISIBLE);
                final String phoneNumber = consumer.getPhoneNumber();
                textViewOrderStatusConsumerPhoneNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumber));
                        startActivity(intent);
                    }
                });
            } else {
                textViewOrderStatusConsumerPhoneNumber.setVisibility(View.GONE);
            }

        }

        if(orderStatus != null && !orderStatus.equals("Completed")){
            orderStatusOrderActions.setVisibility(View.VISIBLE);
        }

        if( orderStatus != null && orderStatus.equals("In Progress")){
            textViewOrderStatusOrderActions.setText("Finish Order.");
            textViewOrderStatusOrderActions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderCompleted(consumerId);
                }
            });
        }

        if( orderStatus != null && orderStatus.equals("New")){
            final Integer carwasherId = carwasher.getCarwasherId();
            textViewOrderStatusOrderActions.setText("Accept Order.");
            textViewOrderStatusOrderActions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderAccepted( carwasherId, consumerId);
                }
            });
        }

        progressDialog.dismiss();
    }

    public void updateOrderCompleted(Integer consumerId){
        final ProgressDialog progressDialog1 =  CarConstant.getProgressDialog(this,"Processing Order...");
        progressDialog1.show();
        RestInvokerService restInvokerService = RestClient.getClient().create(RestInvokerService.class);

        Call<Map<String, Object>> call = restInvokerService.completeOrderByOrderId(orderId,consumerId);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> map = response.body();
                if( map.get("resCode").equals(200.0)){
                    Boolean  flag = (Boolean) map.get("data");
                    if(flag){
                        loadAllDetailsForOrder(orderId);
                        Toast.makeText(getApplicationContext(),"Order successfully Completed.",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Some Problem in completing order, contact admin team for support.",Toast.LENGTH_SHORT).show();
                    }

                }
                progressDialog1.dismiss();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressDialog1.dismiss();

            }
        });
    }

    public void updateOrderAccepted(Integer carwasherId, Integer consumerId){
        final ProgressDialog progressDialog1 =  CarConstant.getProgressDialog(this,"Processing Order...");
        progressDialog1.show();
        RestInvokerService restInvokerService = RestClient.getClient().create(RestInvokerService.class);

        Call<Map<String, Object>> call = restInvokerService.confirmOrderByCarwasher(orderId,carwasherId,consumerId);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> map = response.body();
                if( map.get("resCode").equals(200.0)){
                    Boolean  flag = (Boolean) map.get("data");
                    if(flag){
                        loadAllDetailsForOrder(orderId);
                        Toast.makeText(getApplicationContext(),"Order successfully accepted.",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Order already accepted by other person.",Toast.LENGTH_SHORT).show();
                    }

                }
                progressDialog1.dismiss();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressDialog1.dismiss();

            }
        });
    }
}
