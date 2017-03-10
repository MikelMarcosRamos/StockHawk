package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

public class DetailWidgetViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor data = null;
    private ContentResolver mContentResolver;
    private Context mContext;

    DetailWidgetViewFactory (ContentResolver contentResolver, Context context) {
        this.mContentResolver = contentResolver;
        this.mContext = context;
    }


    @Override
    public void onCreate() {
        // Nothing to do
    }

    @Override
    public void onDataSetChanged() {
        if (data != null) {
            data.close();
        }
        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();
        data = this.mContentResolver.query(Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null,
                null,
                Contract.Quote.COLUMN_SYMBOL);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
            data = null;
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                data == null || !data.moveToPosition(position)) {
            return null;
        }
        RemoteViews views = new RemoteViews(this.mContext.getPackageName(),
                R.layout.list_item_quote);
        String symbol = data.getString(DetailWidgetRemoteViewsService.INDEX_SYMBOL);
        String price = data.getString(DetailWidgetRemoteViewsService.INDEX_PRICE);
        String change = data.getString(DetailWidgetRemoteViewsService.INDEX_PERCENTAGE_CHANGE);

        views.setTextViewText(R.id.symbol, symbol);
        views.setTextViewText(R.id.price, price);
        views.setTextViewText(R.id.change, change);
        views.setTextViewTextSize(R.id.change, TypedValue.COMPLEX_UNIT_SP, 10);

        if (Float.parseFloat(change) > 0) {
            views.setTextColor(R.id.change , Color.GREEN);
        } else {
            views.setTextColor(R.id.change , Color.RED);
        }

        final Intent fillInIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(this.mContext.getResources().getString(R.string.arg_selected_symbol), data.getString(Contract.Quote.POSITION_SYMBOL));
        fillInIntent.putExtras(bundle);
        views.setOnClickFillInIntent(R.id.list_item_id, fillInIntent);
        return views;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(this.mContext.getPackageName(), R.layout.list_item_quote);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (data.moveToPosition(position))
            return data.getLong(DetailWidgetRemoteViewsService.INDEX_ID);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
