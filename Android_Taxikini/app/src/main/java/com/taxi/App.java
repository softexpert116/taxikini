package com.taxi;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.nfc.cardemulation.HostNfcFService;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.taxi.common.User;
import com.taxi.common.Utils.Util;
import com.taxi.kini.HomeActivity;
import com.taxi.kini.IntroActivity;
import com.taxi.kini.LoginActivity;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class App extends Application {

    public static App app;
    public static SharedPreferences prefs;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        app = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

    }
    public static void goToIntroPage(final Activity activity) {
        //token update, goto intro page
        Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_TOKEN).setValue(Util.getDeviceToken(activity));
        //get user info into model
        String phone_num = Util.mUser.getPhoneNumber();
        phone_num = phone_num.substring(1);
        Util.mDatabase.child(Util.tbl_user).orderByChild(Util.USER_PHONE).equalTo(phone_num)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                String phone = "", type = Util.PASSENGER, email = "", firstname = "", lastname = "", token = "", photo = "", password = "", car_num = "",
                                        car_pic = "", license_num = "", license_pic = "", plate_num = "", nric = "";
                                String online = "0";

                                if (datas.child(Util.USER_PHONE).getValue() != null) {
                                    phone = datas.child(Util.USER_PHONE).getValue().toString();
                                }
                                if (datas.child(Util.USER_TYPE).getValue() != null) {
                                    type = datas.child(Util.USER_TYPE).getValue().toString();
                                }
                                if (datas.child(Util.USER_EMAIL).getValue() != null) {
                                    email = datas.child(Util.USER_EMAIL).getValue().toString();
                                }
                                if (datas.child(Util.USER_FIRSTNAME).getValue() != null) {
                                    firstname = datas.child(Util.USER_FIRSTNAME).getValue().toString();
                                }
                                if (datas.child(Util.USER_LASTNAME).getValue() != null) {
                                    lastname = datas.child(Util.USER_LASTNAME).getValue().toString();
                                }
                                if (datas.child(Util.USER_TOKEN).getValue() != null) {
                                    token = datas.child(Util.USER_TOKEN).getValue().toString();
                                }
                                if (datas.child(Util.USER_PHOTO).getValue() != null) {
                                    photo = datas.child(Util.USER_PHOTO).getValue().toString();
                                }
                                if (datas.child(Util.USER_PASSWORD).getValue() != null) {
                                    password = datas.child(Util.USER_PASSWORD).getValue().toString();
                                }
                                if (datas.child(Util.USER_CAR_NUM).getValue() != null) {
                                    car_num = datas.child(Util.USER_CAR_NUM).getValue().toString();
                                }
                                if (datas.child(Util.USER_CAR_PIC).getValue() != null) {
                                    car_pic = datas.child(Util.USER_CAR_PIC).getValue().toString();
                                }
                                if (datas.child(Util.USER_LICENSE_NUM).getValue() != null) {
                                    license_num = datas.child(Util.USER_LICENSE_NUM).getValue().toString();
                                }
                                if (datas.child(Util.USER_LICENSE_PIC).getValue() != null) {
                                    license_pic = datas.child(Util.USER_LICENSE_PIC).getValue().toString();
                                }
                                if (datas.child(Util.USER_PLATE_NUM).getValue() != null) {
                                    plate_num = datas.child(Util.USER_PLATE_NUM).getValue().toString();
                                }
                                if (datas.child(Util.USER_NRIC).getValue() != null) {
                                    nric = datas.child(Util.USER_NRIC).getValue().toString();
                                }
                                if (datas.child(Util.USER_ONLINE).getValue() != null) {
                                    online = datas.child(Util.USER_ONLINE).getValue().toString();
                                }

                                String id = Util.mUser.getUid();
                                Util.currentUser = new User(id, firstname, lastname, type, email, photo, phone, password, car_num, license_num, plate_num, license_pic, car_pic, nric, token, online, 0, 0);
                                // go to intro page
                                Intent intent = new Intent(activity, HomeActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        } else {
                            Util.auth.signOut();
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

    }
//    public static void startMyService(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return;
//            }
//        }
//        Intent i = new Intent(mContext, PingService.class);
//        mContext.startService(i);
////        mContext.startForegroundService(i);
//    }
//    public static void stopMyService(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                Intent i = new Intent(mContext, PingService.class);
//                mContext.stopService(i);
//            }
//        }
//    }

//    public static void sendSMS(String phoneNo, String msg) {
//        SmsManager manager = SmsManager.getDefault();
//        PendingIntent piSend = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_SENT"), 0);
//        PendingIntent piDelivered = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_DELIVERED"), 0);
//        if(msg.length() > MAX_SMS_MESSAGE_LENGTH)
//        {
//            ArrayList<String> messagelist = manager.divideMessage(msg);
//
//            manager.sendMultipartTextMessage(phoneNo, null, messagelist, null, null);
//        }
//        else
//        {
//            manager.sendTextMessage(phoneNo, null, msg, piSend, piDelivered);
//        }
////        sms.sendTextMessage(phoneNo, null, msg, piSend, piDelivered);
//        Toast.makeText(mContext, mContext.getString(R.string.sms_success_message), Toast.LENGTH_LONG).show();
////
////        try {
////            SmsManager smsManager = SmsManager.getDefault();
////            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
////            Toast.makeText(mContext, mContext.getString(R.string.sms_success_message),
////                    Toast.LENGTH_LONG).show();
////        } catch (Exception ex) {
////            Toast.makeText(mContext,ex.getMessage().toString(),
////                    Toast.LENGTH_LONG).show();
////            ex.printStackTrace();
////        }
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    public static void setPreferences(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setPreference(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(key, value)
                .commit();
    }
    public static String readPreference(String key, String defaultValue) {
        String value = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(key, defaultValue);
        return value;
    }
    public static void removePreference(String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    //    public static void setPreference_JsonObject(String key, JSONObject jsonObject) {
//        Gson gson = new Gson();
//        String json = gson.toJson(jsonObject);
//        setPreference(key, json);
//    }
//    public static JSONObject readPreference_JsonObject(String key) {
//        String json = readPreference(key, "");
//        Type type = new TypeToken<JSONObject>(){}.getType();
//        Gson gson = new Gson();
//        JSONObject jsonObject = gson.fromJson(json, type);
//        return jsonObject;
//    }
//    public static String getSelectedLang() {
//        String lang = En;
//        JSONObject jsonObject = App.readPreference_JsonObject(App.MY_INFO);
//        if (jsonObject != null) {
//            lang = getJsonValue(jsonObject, "lang");
//        }
//        return lang;
//    }
//    public static Configuration getSelectedConfiguration() {
//        String lang = En;
//        JSONObject jsonObject = App.readPreference_JsonObject(App.MY_INFO);
//        if (jsonObject != null) {
//            lang = getJsonValue(jsonObject, "lang");
//        }
//        Configuration config = new Configuration();
//        if (lang.equals(App.Fr)) {
//            config.locale = Locale.FRENCH;
//        } else if (lang.equals(App.De)) {
//            config.locale = Locale.GERMAN;
//        } else {
//            config.locale = Locale.ENGLISH;
//        }
//        return config;
//    }
    public static String getJsonValue(JSONObject jsonObject, String key) {
        String value = "";
        try {
            value = jsonObject.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    public static void setPreference_array_String(String key, ArrayList<String> list) {
        Set<String> tasksSet = new HashSet<String>(list);
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putStringSet(key, tasksSet)
                .commit();
    }

    public static ArrayList<String> readPreference_array_String(String key) {
        Set<String> tasksSet = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getStringSet(key, new HashSet<String>());
        ArrayList<String> tasksList = new ArrayList<String>(tasksSet);
        return tasksList;
    }
    public static void showToast(String string) {
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();

    }
    public static void cancelAllNotifications() {
        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

    }

    public static void social_share(Context context, String url) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
    }
    public void generateHashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.ujs.acer.Oyoo",  PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String hashKey = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                Log.e("hashkey -------------", hashKey); /// CkLR2IIEs9xCrDGJbKOQ/Jr3exE=
// release key hash: w6gx2BgXV0ybPMNC4PfbKnfpu50=
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
        }
    }
    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int count = listAdapter.getCount();
        int totalHeight = 0;
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.height *= 1.5;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    public static void DialNumber(String number, Context context)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setData(Uri.parse("tel:" + number));
            Uri uri = ussdToCallableUri(number);
            intent.setData(uri);
//            context.startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT);
        }
    }

    private static Uri ussdToCallableUri(String ussd) {

        String uriString = "";

        if(!ussd.startsWith("tel:"))
            uriString += "tel:";

        for(char c : ussd.toCharArray()) {

            if(c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }

    public static void openUrl (String url, Context context) {
        if (!URLUtil.isValidUrl(url)) {
            Toast.makeText(context, "Invalid url", Toast.LENGTH_SHORT);
            return;
        }
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static String getTimestampString()
    {
        long tsLong = System.currentTimeMillis();
        String ts =  Long.toString(tsLong);
        return ts;
    }
    public static int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = mContext.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr = mContext.obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        int primaryColor = arr.getColor(0, -1);
        return primaryColor;
    }
}