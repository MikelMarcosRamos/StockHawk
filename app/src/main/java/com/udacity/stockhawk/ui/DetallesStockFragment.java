package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetallesStockFragment extends Fragment implements SeekBar.OnSeekBarChangeListener{

    protected String mSymbol;
    protected List<Entry> entries;
    private static final String POSITION = "POSITION";
    private static final String NUM_STOCKS = "NUM_STOCKS";
    private int mPosition = -1;
    private int mNumElements = -1;

    @BindView(R.id.symbol)
    TextView symbol;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.change)
    TextView change;

    @BindView(R.id.graph)
    LineChart graph;

    @BindView(R.id.sb_position)
    SeekBar mSeekBarPosition;
    @BindView(R.id.sb_num_elements)
    SeekBar mSeekBarNumElements;
    @BindView(R.id.tvXMax)
    TextView tvX;
    @BindView(R.id.tvYMax)
    TextView tvY;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(getString(R.string.arg_selected_symbol))) {
            this.mSymbol = getArguments().getString(getString(R.string.arg_selected_symbol));
        }

        if (savedInstanceState != null) {
            this.mPosition = savedInstanceState.getInt(POSITION);
            this.mNumElements = savedInstanceState.getInt(NUM_STOCKS);
        } else if (this.mPosition < 0){
            this.mPosition = 0;
            this.mNumElements = 8;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detalles_stock, container, false);
        ButterKnife.bind(this, view);



        this.symbol.setText(this.mSymbol);

        Uri uri = Contract.Quote.URI;//.buildUpon().appendPath(Integer.toString(mMovie.getId())).build();
        String condicion = Contract.Quote.COLUMN_SYMBOL + " = ?";
        String[] valores = {""};
        valores[0] = this.mSymbol;
        Cursor cursor = getContext().getContentResolver().query(uri,
                null,
                condicion,
                valores,
                null);



        if (cursor != null) {
            if (cursor.getCount() > 0) {

                entries = new ArrayList<>();
                cursor.moveToFirst();
                price.setText(cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE)));
                change.setText(cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE)));
                if (Float.parseFloat(change.getText().toString()) > 0) {
                    change.setBackgroundResource(R.drawable.percent_change_pill_green);
                } else {
                    change.setBackgroundResource(R.drawable.percent_change_pill_red);
                }
                String history = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
                String[] historicalQuotes = history.split("\n");
                for (String historicalQuote : historicalQuotes ) {
                    String[] data = historicalQuote.split(", ");
                    float fdate = Float.parseFloat(data[0]);
                    float fclose = Float.parseFloat(data[1]);
                    entries.add(new Entry(fdate, fclose));
                }
                Collections.reverse(entries);
            }
            cursor.close();
        }

        // setting data
        this.mSeekBarPosition.setProgress(this.mPosition);
        this.mSeekBarNumElements.setProgress(this.mNumElements);

        this.mSeekBarPosition.setMax(this.entries.size() - 2);
        tvY.setText(String.valueOf(this.mSeekBarPosition.getProgress()));
        this.mSeekBarNumElements.setMax(this.entries.size());
        tvX.setText(String.valueOf(this.mSeekBarNumElements.getProgress()));

        this.mSeekBarPosition.setOnSeekBarChangeListener(this);
        this.mSeekBarNumElements.setOnSeekBarChangeListener(this);

        LineDataSet dataSet = new LineDataSet(entries, "");
        LineData lineData = new LineData(dataSet);
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.RED);
        dataSet.setFillColor(Color.YELLOW);
        this.graph.setData(lineData);

        XAxis xAxis = this.graph.getXAxis();
        xAxis.setValueFormatter(new MyValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLUE);
        this.graph.getAxisRight().setDrawLabels(false);
        this.graph.getAxisLeft().setTextColor(Color.BLUE);

        this.graph.getDescription().setEnabled(false);
        this.graph.setDrawGridBackground(false);
        this.graph.setTouchEnabled(false);
        this.graph.getXAxis().setTextColor(Color.WHITE);
        this.graph.getAxisLeft().setDrawLabels(false);
        this.graph.getAxisRight().setTextColor(Color.WHITE);
        this.graph.getLegend().setTextColor(Color.WHITE);
        this.graph.getLegend().setTextSize(12f);

        setData();

        return view;
    }

    public class MyValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Date date = new Date((long)value);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            // "value" represents the position of the label on the axis (x or y)
            return df.format(date);
        }
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        this.mPosition = this.mSeekBarPosition.getProgress();
        this.mNumElements = this.mSeekBarNumElements.getProgress();

        tvY.setText("" + (this.mSeekBarPosition.getProgress()));
        tvX.setText("" + (this.mSeekBarNumElements.getProgress()+2));

        setData( );
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    private void setData() {
        int posicion = this.mSeekBarPosition.getProgress();
        int numElementos = this.mSeekBarNumElements.getProgress() + 1;

        numElementos = numElementos + 1;
        if (posicion + numElementos > this.entries.size()) {
            numElementos = this.entries.size() - posicion;
        }

        ArrayList<Entry> yVals1 = new ArrayList<>();

        for (int count = 0;count < numElementos; count++) {
            Entry entry = this.entries.get(posicion + count);
            yVals1.add(new Entry(entry.getX(), entry.getY()));
        }

        LineDataSet set1;

        if (this.graph.getData() != null &&
                this.graph.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) this.graph.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            this.graph.getData().notifyDataChanged();
            this.graph.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(yVals1, "The year 2017");

            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);
            data.setValueTextSize(10f);

            this.graph.setData(data);
        }
        this.graph.invalidate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, this.mPosition);
        outState.putInt(NUM_STOCKS, this.mNumElements);
    }

    @Override
    public void onResume() {
        super.onResume();

        setData();
    }
}
