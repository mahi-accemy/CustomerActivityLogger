package com.accemy.mahindralogger;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class MahindraLogResponse {

    @SerializedName("Data") public boolean data;
    @Nullable @SerializedName("Error") public MahindraLogError error;

    public class MahindraLogError {
        @SerializedName("ErrorCode") public String code;
        @SerializedName("ErrorMsg") public String msg;
    }
}
