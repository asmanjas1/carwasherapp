package resources;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarConstant {

    public static String LOGGED_IN = "Logged In";
    public static String USER_TYPE = "User_Type";
    public static String CONSUMER = "Consumer";
    public static String CARWASHER = "Carwasher";
    public static String COMPLETED_CONSUMER_ORDER_DATA = "Completed_Order_Data";

    static ProgressDialog progressDoalog = null;

    public static ProgressDialog getProgressDialog(Context context ,String msg){

        progressDoalog = new ProgressDialog(context);
        progressDoalog.setMax(100);
        progressDoalog.setMessage(msg);
        progressDoalog.setCancelable(false);
        progressDoalog.setCanceledOnTouchOutside(false);
        progressDoalog.setTitle("Please Wait");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        return progressDoalog;
    }
}
