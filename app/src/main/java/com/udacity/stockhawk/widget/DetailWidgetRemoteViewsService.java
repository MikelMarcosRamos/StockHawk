package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;


/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    // these indices must match the projection
    static final int INDEX_ID = 0;
    static final int INDEX_SYMBOL = 1;
    static final int INDEX_PRICE = 2;
    static final int INDEX_ABSOLUTE_CHANGE = 3;
    static final int INDEX_PERCENTAGE_CHANGE = 4;
    static final int INDEX_HISTORY = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DetailWidgetViewFactory(getContentResolver(), this);
    }
}
