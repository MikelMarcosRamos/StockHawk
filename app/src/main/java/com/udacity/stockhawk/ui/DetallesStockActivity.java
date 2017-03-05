package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

/**
 * Created by chronoss on 4/03/17.
 */

public class DetallesStockActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_stock);

        //To avoid two calls to the Fragment onCreate on screen rotation
        if (savedInstanceState != null)
            return;
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.arg_selected_symbol))) {
            String symbol = intent.getStringExtra(getString(R.string.arg_selected_symbol));

            Bundle arguments = new Bundle();
            arguments.putString(getString(R.string.arg_selected_symbol), symbol);
            DetallesStockFragment fragment = new DetallesStockFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detalles_stock_container, fragment)
                    .commit();
        }
    }
}
