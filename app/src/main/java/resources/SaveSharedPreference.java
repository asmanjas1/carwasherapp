package resources;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.Map;

import bean.Carwasher;
import bean.CarwasherFirebase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SaveSharedPreference {

    public static Gson gson = new Gson();

    //Make this to private mode
    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setIsUserLoggedIn(Context ctx){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(CarConstant.LOGGED_IN,true);
        editor.apply();
    }

    public static void storeFirebaseToken(String token, Context ctx){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString("token",token);
        editor.apply();
    }

    public static void saveFirebaseTokenToserver(Context ctx){
        CarwasherFirebase cf =  new CarwasherFirebase();
        cf.setConsumerFirebaseId(getCarwasherFromGson(ctx).getCarwasherId());
        String token = getFirebaseToken(ctx);
        if(token == null){
            token = FirebaseInstanceId.getInstance().getToken();
        }
        cf.setFirebaseToken(token);

        if (token != null){
            RestInvokerService restInvokerService = RestClient.getClient().create(RestInvokerService.class);
            Call<Map<String, Object>> call = restInvokerService.saveFirevaseToken(cf);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Map<String, Object> map = response.body();
                    if( map.get("resCode").equals(200.0)){
                        Log.d("Firebase Token Store: ",map.toString());
                    } else {
                        Log.d("Firebase Token Store: ","Failed");
                    }
                }
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                }
            });
        }
    }

    public static String getFirebaseToken(Context ctx){
        return getSharedPreferences(ctx).getString("token",null);
    }

    public static boolean getIsUserLoggedIn(Context ctx){
        return getSharedPreferences(ctx).getBoolean(CarConstant.LOGGED_IN,false);
    }

    public static void setCarwasherObj(Context ctx, String consumer){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(CarConstant.CARWASHER,consumer);
        editor.apply();
    }

    public static void setCompletedOrderForCarwasher(Context ctx, String data){
        Editor editor =getSharedPreferences(ctx).edit();
        editor.putString(CarConstant.COMPLETED_CONSUMER_ORDER_DATA, data);
        editor.apply();
    }

    public static String getCompletedOrderForCarwasher(Context ctx){
        return getSharedPreferences(ctx).getString(CarConstant.COMPLETED_CONSUMER_ORDER_DATA,null);
    }

    public static String getCarwasherObj(Context ctx){
        return getSharedPreferences(ctx).getString(CarConstant.CARWASHER,null);
    }
    public static void logOut(Context ctx){
        String token = getFirebaseToken(ctx);
        Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.apply();
        storeFirebaseToken(token, ctx);
    }

    public static Carwasher getCarwasherFromGson(Context ctx){
        Carwasher carwasher = gson.fromJson(getCarwasherObj(ctx), Carwasher.class);
        return carwasher;
    }
}
