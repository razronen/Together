package com.together.raz.together;

/**
 * Created by Raz on 5/13/2017.
 */
import android.app.Application;
import android.content.Context;

import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(
        formUri = "http://www.backendofyourchoice.com/reportpath"
)
public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);


        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}