package carwasherRepo.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bean.Carwasher;
import bean.CarwasherAddress;
import bean.Orders;
import carwasher.com.carwasher.AddAddressActivity;
import carwasher.com.carwasher.R;
import carwasherRepo.adapter.CarwasherOrdersInProgressAdapter;
import carwasherRepo.adapter.CarwasherOrdersNewAdapter;
import resources.CarConstant;
import resources.RestClient;
import resources.RestInvokerService;
import resources.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dell on 8/13/2019.
 */

public class CarwasherHomeFragment extends Fragment{

    RecyclerView recyclerViewInProgressOrders,recyclerViewForNewOrders;
    public static Gson gson = new Gson();
    List<Orders> inProgressOrdersList;
    List<Orders> newOrdersList;
    ProgressDialog progressDialog1;
    ProgressDialog progressDialog2;

    public static CarwasherHomeFragment newInstance() {
        CarwasherHomeFragment fragment;
        fragment = new CarwasherHomeFragment();
        return fragment;
    }

    public CarwasherHomeFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Home");
        View view  = inflater.inflate(R.layout.carwasher_home_fragment, container, false);


        recyclerViewInProgressOrders = (RecyclerView) view.findViewById(R.id.recyclerViewForInProgressOrders);
        recyclerViewInProgressOrders.setHasFixedSize(true);
        recyclerViewInProgressOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewForNewOrders = (RecyclerView) view.findViewById(R.id.recyclerViewForNewOrders);
        recyclerViewForNewOrders.setHasFixedSize(true);
        recyclerViewForNewOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        Carwasher carwasher = SaveSharedPreference.getCarwasherFromGson(getContext());
        checkForAddressAdd();

        //loadInProgressOrders(carwasher.getCarwasherId());
       // loadNewOrders(carwasher);
        return view;
    }

    public void loadInProgressOrders(Integer id){
        progressDialog1 =  CarConstant.getProgressDialog(getContext(),"Loading...");
        progressDialog1.show();
        RestInvokerService restInvokerService = RestClient.getClient().create(RestInvokerService.class);
        Call<Map<String, Object>> call = restInvokerService.getInprogressOrdersForCarwasher(id);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> map = response.body();
                if( map.get("resCode").equals(200.0)){
                    List<String> list = (List<String>) map.get("data");
                    processInProgressOrderResponse(list);
                } else {
                    progressDialog1.dismiss();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressDialog1.dismiss();
            }
        });
    }

    public void loadNewOrders(Carwasher carwasher){
        if( carwasher.getCarwasherAddress() != null){
            Integer id = carwasher.getCarwasherId();
            String city = carwasher.getCarwasherAddress().getCity();
            String state = carwasher.getCarwasherAddress().getState();
            RestInvokerService restInvokerService = RestClient.getClient().create(RestInvokerService.class);
            Call<Map<String, Object>> call = restInvokerService.getNewOrdersForCarwasher(id, city, state);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Map<String, Object> map = response.body();
                    if( map.get("resCode").equals(200.0)){
                        List<String> list = (List<String>) map.get("data");
                        processNewOrderResponse(list);
                    }
                }
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                }
            });
        }

    }


    public void processInProgressOrderResponse(List<String> list){
        inProgressOrdersList = new ArrayList<>();
        for(String ss: list){
            Orders order = gson.fromJson(ss, Orders.class);
            inProgressOrdersList.add(order);
        }
        if(inProgressOrdersList != null){
            progressDialog1.dismiss();
            CarwasherOrdersInProgressAdapter carwasherOrdersCompletedAdapter = new CarwasherOrdersInProgressAdapter(getContext(),inProgressOrdersList);
            recyclerViewInProgressOrders.setAdapter(carwasherOrdersCompletedAdapter);
        }else {
            progressDialog1.dismiss();
            Toast.makeText(getContext(),"Not able to fetch in progress Order List",Toast.LENGTH_SHORT).show();
        }
    }


    public void processNewOrderResponse(List<String> list){
        newOrdersList = new ArrayList<>();
        for(String ss: list){
            Orders order = gson.fromJson(ss, Orders.class);
            newOrdersList.add(order);
        }
        if(newOrdersList != null){
            CarwasherOrdersNewAdapter carwasherOrdersNewAdapter = new CarwasherOrdersNewAdapter(getContext(),newOrdersList);
            recyclerViewForNewOrders.setAdapter(carwasherOrdersNewAdapter);
        }else {
            Toast.makeText(getContext(),"Not able to fetch new Order List",Toast.LENGTH_SHORT).show();
        }

    }

    public void checkForAddressAdd(){
        Carwasher carwasher = SaveSharedPreference.getCarwasherFromGson(getContext());
        CarwasherAddress address = carwasher.getCarwasherAddress();
        if(address == null){
            Intent carIntent = new Intent(getActivity(), AddAddressActivity.class);
            startActivity(carIntent);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Carwasher carwasher = SaveSharedPreference.getCarwasherFromGson(getContext());
        checkForAddressAdd();
        loadInProgressOrders(carwasher.getCarwasherId());
        loadNewOrders(carwasher);
    }
}
