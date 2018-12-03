package com.accemy.mahindralogger;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

public class TelephonyInfo {

    private static final String[] SIM_STATUS_METHOD_NAMES = {"getSimStateGemini", "getSimState", "getSimSerialNumberGemini", "getDeviceIdDs"};
    private static final String[] DEVICE_ID_METHOD_NAMES = {"getDeviceIdGemini", "getDeviceId"};
    private static TelephonyInfo telephonyInfo;
    private String imeiSIM1;
    private String imeiSIM2;
    private boolean isSIM1Ready;
    private boolean isSIM2Ready;

    public String getImsiSIM1() {
        return imeiSIM1;
    }

    /*public static void setImsiSIM1(String imeiSIM1) {
        TelephonyInfo.imeiSIM1 = imeiSIM1;
    }*/

    public String getImsiSIM2() {
        return imeiSIM2;
    }

    /*public static void setImsiSIM2(String imeiSIM2) {
        TelephonyInfo.imeiSIM2 = imeiSIM2;
    }*/

    public boolean isSIM1Ready() {
        return isSIM1Ready;
    }

    /*public static void setSIM1Ready(boolean isSIM1Ready) {
        TelephonyInfo.isSIM1Ready = isSIM1Ready;
    }*/

    public boolean isSIM2Ready() {
        return isSIM2Ready;
    }

    /*public static void setSIM2Ready(boolean isSIM2Ready) {
        TelephonyInfo.isSIM2Ready = isSIM2Ready;
    }*/

    public boolean isDualSIM() {
        return imeiSIM2 != null;
    }

    private TelephonyInfo() {
    }

    public static TelephonyInfo getInstance(Context context){
        try {

            if (telephonyInfo == null) {

                telephonyInfo = new TelephonyInfo();

                TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

                telephonyInfo.imeiSIM1 = telephonyManager.getDeviceId();
                telephonyInfo.imeiSIM2 = null;

                for (String methodName : DEVICE_ID_METHOD_NAMES) {
                    try {
                        telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, methodName, 0);
                        telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, methodName, 1);
                        break;
                    } catch (GeminiMethodNotFoundException e) {
                        e.printStackTrace();
//                    logTelephonyManagerMethodNamesForThisDevice(context);
                    }
                }

                telephonyInfo.isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
                telephonyInfo.isSIM2Ready = false;

                for (String methodName : SIM_STATUS_METHOD_NAMES) {
                    try {
                        telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, methodName, 0);
                        telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, methodName, 1);
                        break;
                    } catch (GeminiMethodNotFoundException e) {
                        e.printStackTrace();
//                    logTelephonyManagerMethodNamesForThisDevice(context);
                    }
                }
            }
            return telephonyInfo;
        }catch (SecurityException e){
            e.printStackTrace();
            Log.e(TelephonyInfo.class.getName(), "Telephony permissions not granted!");
        }
        return null;
    }

    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        String imei = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if(ob_phone != null){
                imei = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return imei;
    }

    private static  boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        boolean isReady = false;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

            if(ob_phone != null){
                int simState = Integer.parseInt(ob_phone.toString());
                if(simState == TelephonyManager.SIM_STATE_READY){
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }



    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }


    public static void logTelephonyManagerMethodNamesForThisDevice(Context context) {

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            Method[] methods = telephonyClass.getMethods();
            for (int idx = 0; idx < methods.length; idx++) {

                Log.e(TelephonyInfo.class.getName(), "\n" + methods[idx] + " declared by " + methods[idx].getDeclaringClass());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
