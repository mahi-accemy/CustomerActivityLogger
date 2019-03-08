package com.accemy.mahindralogger;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class MahindraLogger {

    private static final String DIRECTORY_NAME = "mahindracustomeractivitylogs";
    private static final String LATEST_FILE_NAME = "latest.txt";
    private static volatile MahindraLogger sSoleInstance;

    private Context mContext;
    private MahindraLoggerService mWebService;
    private Gson mGson;
    private SimpleDateFormat dateFormat;
    private boolean isProd = false;

    //private constructor.
    private MahindraLogger(){
        //Prevent form the reflection api.
        if (sSoleInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static MahindraLogger getInstance(Application applicationContext) {
        //Double check locking pattern
        if (sSoleInstance == null) { //Check for the first time
            synchronized (MahindraLogger.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sSoleInstance == null) sSoleInstance = new MahindraLogger();
            }

            //Actions to be done once, at the time of singleton creation
            sSoleInstance.mContext = applicationContext;
            sSoleInstance.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");

            initialiseConstants(null, null, null);
        }

        File logsDirectory = new File(sSoleInstance.mContext.getFilesDir()+ File.separator + DIRECTORY_NAME);
        if (!logsDirectory.exists()) {
            if(!logsDirectory.mkdir()){
                throw new RuntimeException("Unable to create log directory!");
            }
        }
        return sSoleInstance;
    }

    public MahindraLogger setProd(boolean isProduction){
        this.isProd = isProduction;
        mWebService = null;
        return sSoleInstance;
    }

    public MahindraLogger initConsts(Activity activity){
        if(activity != null){
            initialiseConstants(activity, null, null);
        }
        return sSoleInstance;
    }

    public MahindraLogger initConsts(Activity activity, String interventionName, String source){
        if(activity != null){
            initialiseConstants(activity, interventionName, source);
        }
        return sSoleInstance;
    }

    /*************************************** Setting constants ************************************/
    String version = "N/A";
    String deviceName = Build.MANUFACTURER + Build.PRODUCT;
    String osName = "Android " + Double.parseDouble(Build.VERSION.RELEASE.replaceAll("(\\d+[.]\\d+)(.*)", "$1"));
    String deviceImieNo = "N/A";
    String interventionId = "N/A";
    String interventionName = "N/A";

    private static void initialiseConstants(Activity activity, String interventionName, String interventionId){
        try {

            try {
                PackageInfo pInfo = sSoleInstance.mContext.getPackageManager().getPackageInfo(sSoleInstance.mContext.getPackageName(), 0);
                sSoleInstance.version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if(activity != null){
                StringBuilder builder = new StringBuilder();
                if(sSoleInstance.isTablet(sSoleInstance.mContext)){
                    sSoleInstance.interventionName = "Tablet Sales Assistant";
                    builder.append("Tab");
                }else{
                    sSoleInstance.interventionName = "Phone Sales Assistant";
                    builder.append("Phone");
                }
                builder.append(sSoleInstance.getScreenSizeInches(activity)).append("_v").append(sSoleInstance.version);
                sSoleInstance.interventionId = builder.toString();

                if(interventionName != null && interventionName.length() > 0){
                    sSoleInstance.interventionName = interventionName;
                }
                if(interventionId != null && interventionId.length() > 0){
                    sSoleInstance.interventionId = interventionId;
                }

                Dexter.withActivity(activity)
                        .withPermission(Manifest.permission.READ_PHONE_STATE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    TelephonyManager tm = (TelephonyManager)sSoleInstance.mContext.getSystemService(sSoleInstance.mContext.TELEPHONY_SERVICE);
                                    try {
                                        sSoleInstance.deviceImieNo = tm.getImei();
                                    }catch (SecurityException e){
                                        e.printStackTrace();
                                        Log.e(MahindraLogger.class.getName(), "Telephony permissions not granted!");
                                    }
                                }else{
                                    TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(sSoleInstance.mContext);
                                    sSoleInstance.deviceImieNo = telephonyInfo.getImsiSIM1();
                                }
                                MahindraLogItem.INIT_CONSTS(sSoleInstance.version, sSoleInstance.deviceName, sSoleInstance.osName,
                                        sSoleInstance.deviceImieNo, sSoleInstance.interventionId, sSoleInstance.interventionName);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        })
                        .check();
            }

            MahindraLogItem.INIT_CONSTS(sSoleInstance.version, sSoleInstance.deviceName, sSoleInstance.osName,
                    sSoleInstance.deviceImieNo, sSoleInstance.interventionId, sSoleInstance.interventionName);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public String getScreenSizeInches(Activity activity){
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        // since SDK_INT = 1;
        int mWidthPixels = displayMetrics.widthPixels;
        int mHeightPixels = displayMetrics.heightPixels;

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try{
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {}
        }

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored) {}
        }

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(mWidthPixels / dm.xdpi, 2);
        double y = Math.pow(mHeightPixels / dm.ydpi, 2);

        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(Math.sqrt(x + y));
    }


    /******************************** Logging and related Async Tasks *****************************/

    public void log(@NonNull String customerId, @NonNull String mileId, String userName, String sessionId, String pageId, String pageName,
                    String previousPageId, String previousPageName, String eventId, String eventName,
                    String enquiryId, String tdBookingId, String modelCd, String modelGrpCd, String personaPitched,
                    String sessionStartTime, String sessionEndTime, String personaName, String eventType){

        if(customerId == null || customerId.trim().equalsIgnoreCase("")){
            return;
        }

        if(mileId == null || mileId.trim().equalsIgnoreCase("")){
            return;
        }

        String timestamp = "N/A";
        Date date = new Date(System.currentTimeMillis());
        if(dateFormat != null){
            timestamp = dateFormat.format(date);
        }else{
            timestamp = date.toString();
        }

        MahindraLogItem item = new MahindraLogItem(customerId, mileId, userName, sessionId, timestamp, pageId, pageName,
                previousPageId, previousPageName, eventId, eventName, enquiryId, tdBookingId, modelCd,
                modelGrpCd, personaPitched, sessionStartTime, sessionEndTime, personaName, eventType);
        new MahindraLogTask().execute(item, null, null);
    }

    private class MahindraLogTask extends AsyncTask<MahindraLogItem, Void, Void> {

        protected Void doInBackground(MahindraLogItem... items) {
            appendLog(items[0]);
            if(NetworkUtil.isNetworkConnected(sSoleInstance.mContext)) {
                new MahindraLogFilesSyncTask().execute(null, null, null);
            }
            return null;
        }

        private void appendLog(MahindraLogItem item) {
            File logFile = new File(mContext.getFilesDir() + File.separator + DIRECTORY_NAME, LATEST_FILE_NAME);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }
            }
            try {
                if(mGson == null) {
                    mGson = new GsonBuilder().excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT).create();
                }
                String jsonString = mGson.toJson(item);
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(jsonString);
                buf.newLine();
                buf.close();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class MahindraLogFilesSyncTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {
            if(mWebService == null){
                mWebService = MahindraLoggerService.Creator.newMahindraLoggerService(MahindraLogger.this.isProd);
            }
            if(mGson == null) {
                mGson = new GsonBuilder().create();
            }

            File logFile = new File(mContext.getFilesDir() + File.separator + DIRECTORY_NAME, LATEST_FILE_NAME);
            uploadFileData(logFile);
            return null;
        }

        private void uploadFileData(final File file){
            try {
                if (file.exists()) {
                    ArrayList<String> logsList = new ArrayList<>();
                    String curLine = null;
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    while ((curLine = reader.readLine()) != null) {
                        logsList.add(curLine);
                    }
                    file.delete();

                    for(int i = 0; i < logsList.size(); i++){
                        final String logString = logsList.get(i);
                        Call<MahindraLogResponse> response = mWebService.postData(RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), logString));
                        response.enqueue(new Callback<MahindraLogResponse>() {
                            @Override
                            public void onResponse(Call<MahindraLogResponse> call, retrofit2.Response<MahindraLogResponse> response) {
                                Log.d("TAG",response.code()+"");
                                if(response.isSuccessful()) {
                                    MahindraLogResponse logResponse = response.body();
                                    if (logResponse.data) {
                                        Log.d("", "Success");
                                    } else {
                                        Log.d(MahindraLogFilesSyncTask.class.getName(), "Fail");
                                    }
                                }else{
                                    Log.d(MahindraLogFilesSyncTask.class.getName(), "Fail");
                                }
                            }

                            @Override
                            public void onFailure(Call<MahindraLogResponse> call, Throwable t) {
                                Log.e(MahindraLogFilesSyncTask.class.getName(), "Error");
                                saveFailedUploadLogItem(logString);
                            }
                        });

                        Log.e("Response", response.toString());
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
                Log.e("Exception", e.getMessage());
            }
        }

        private void saveFailedUploadLogItem(String logString){
            File logFile = new File(mContext.getFilesDir() + File.separator + DIRECTORY_NAME, LATEST_FILE_NAME);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }
            }
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(logString);
                buf.newLine();
                buf.close();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
