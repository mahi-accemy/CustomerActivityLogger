package com.accemy.mahindralogger;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MahindraLogItem {

    @SerializedName("CustomerID") private String customerId = "";
    @SerializedName("MileID") private String mMileId;
    @SerializedName("Name") private String mUserName;
    @SerializedName("Source") private static String source;
    @SerializedName("interventionName") private static String interventionName;
    @SerializedName("appVersion") private static String appVersion;
    @SerializedName("devicename") private static String deviceName;
    @SerializedName("osname") private static String osName;
    @SerializedName("deviceimieNo") private static String deviceImieNo;
    @SerializedName("SessionID") private String mSessionId;
    @SerializedName("Date") private String mTimestamp;
    @SerializedName("modelCd") private String mModelCd;
    @SerializedName("modelGrpCd") private String mModelGrpCd;
    @SerializedName("personaPitched") private String mPersonaPitched;
    @SerializedName("sessionStartTime") private String mSessionStartTime;
    @SerializedName("sessionEndTime") private String mSessionEndTime;
    @SerializedName("personaName") private String mPersonaName;
    @SerializedName("Activities") private Activities mActivities;

    @SerializedName("ActivityEvents") private ArrayList<ActivityEvent> mEvents;

    public static void INIT_CONSTS(String appVersion, String deviceName, String osName, String deviceImieNo, String source, String interventionName) {
        MahindraLogItem.appVersion = appVersion;
        MahindraLogItem.deviceName = deviceName;
        MahindraLogItem.osName = osName;
        MahindraLogItem.deviceImieNo = deviceImieNo;
        MahindraLogItem.source = source;
        MahindraLogItem.interventionName = interventionName;
    }

    public MahindraLogItem(String customerId, String mileId, String userName, String sessionId, String timestamp, String pageID, String pageName,
                           String previousPageId, String previousPageName, String eventId, String eventName,
                           String enquiryId, String tdBookingId, String modelCd, String modelGrpCd,
                           String personaPitched, String sessionStartTime, String sessionEndTime, String personaName,
                           String eventType){
        this.customerId = customerId;
        this.mMileId = mileId;
        this.mUserName = userName;
        this.mSessionId = sessionId;
        this.mTimestamp = timestamp;
        this.mModelCd = modelCd;
        this.mModelGrpCd = modelGrpCd;
        this.mPersonaPitched = personaPitched;
        this.mSessionStartTime = sessionStartTime;
        this.mSessionEndTime = sessionEndTime;
        this.mPersonaName = personaName;
        this.mActivities = new Activities(new TestDrive(enquiryId, tdBookingId));

        this.mEvents = new ArrayList<>();
        mEvents.add(new ActivityEvent(pageID, pageName, previousPageId, previousPageName, eventId, eventName, eventType));
    }

    private class Activities {
        @SerializedName("TestDrives") private ArrayList<TestDrive> mTestDrives;

        public Activities(TestDrive testDrive) {
            this.mTestDrives = new ArrayList<>();
            this.mTestDrives.add(testDrive);
        }
    }

    private class ActivityEvent{

        @SerializedName("pageID") private String mPageId;
        @SerializedName("pageName") private String mPageName;
        @SerializedName("previousPageID") private String mPreviousPageId;
        @SerializedName("previousPageName") private String mPreviousPageName;
        @SerializedName("eventID") private String mEventId;
        @SerializedName("eventName") private String mEventName;
        @SerializedName("eventType") private String mEventType;

        public ActivityEvent(String pageId, String pageName, String previousPageId, String previousPageName,
                             String eventId, String eventName, String eventType) {
            this.mPageId = pageId;
            this.mPageName = pageName;
            this.mPreviousPageId = previousPageId;
            this.mPreviousPageName = previousPageName;
            this.mEventId = eventId;
            this.mEventName = eventName;
            this.mEventType = eventType;
        }
    }

    private class TestDrive{

        @SerializedName("enquiryID") private String mEnquiryId;
        @SerializedName("TDbookingID") private String mTdBookingId;

        public TestDrive(String enquiryId, String tdBookingId) {
            this.mEnquiryId = enquiryId;
            this.mTdBookingId = tdBookingId;
        }
    }
}
