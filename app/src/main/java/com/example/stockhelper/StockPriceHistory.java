package com.example.stockhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class StockPriceHistory extends AppCompatActivity implements View.OnClickListener {

    private Button oneDay;
    private Button fiveDay;
    private Button oneMonth;
    private Button oneYear;
    private Button allTime;

    private LineGraphSeries<DataPoint> graphData;
    private RequestQueue mQueue;
    private GraphView graph;
    private String[] dates;
    private Stock chosenStock;
    private ProgressBar graphProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_price_history);

        oneDay = findViewById(R.id.graphOneDayButton);
        fiveDay = findViewById(R.id.graphFiveDayButton);
        oneMonth = findViewById(R.id.graphOneMonthButton);
        oneYear = findViewById(R.id.graphOneYearButton);
        allTime = findViewById(R.id.graphAllTimeButton);
        oneDay.setOnClickListener(this);
        fiveDay.setOnClickListener(this);
        oneMonth.setOnClickListener(this);
        oneYear.setOnClickListener(this);
        allTime.setOnClickListener(this);

        graph = (GraphView) findViewById(R.id.graph);
        graphProgressBar = (ProgressBar) findViewById(R.id.graphProgressBar);
        graph.setVisibility(View.GONE);
        graphProgressBar.setVisibility(View.VISIBLE);
        chosenStock = getIntent().getParcelableExtra("chosenStock");
        mQueue = Volley.newRequestQueue(this);
        showOneMonth(chosenStock.symbol);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.graphOneDayButton:
                graph.setVisibility(View.GONE);
                graphProgressBar.setVisibility(View.VISIBLE);
                showOneDay(chosenStock.symbol);
                break;
            case R.id.graphFiveDayButton:
                graph.setVisibility(View.GONE);
                graphProgressBar.setVisibility(View.VISIBLE);
                showFiveDays(chosenStock.symbol);
                break;
            case R.id.graphOneMonthButton:
                graph.setVisibility(View.GONE);
                graphProgressBar.setVisibility(View.VISIBLE);
                showOneMonth(chosenStock.symbol);
                break;
            case R.id.graphOneYearButton:
                graph.setVisibility(View.GONE);
                graphProgressBar.setVisibility(View.VISIBLE);
                showOneYear(chosenStock.symbol);
                break;
            case R.id.graphAllTimeButton:
                graph.setVisibility(View.GONE);
                graphProgressBar.setVisibility(View.VISIBLE);
                showAllTime(chosenStock.symbol);
                break;
        }
    }

    private void showAllTime(String stockSymbol) {
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol=" + stockSymbol + "&apikey=IBBOJPT8T6NZA44K";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject stock = response.getJSONObject("Weekly Time Series");
                            Iterator<String> jsonKeys = stock.keys();
                            int datesLength = stock.length();
                            dates = new String[datesLength];
                            datesLength = datesLength - 1;
                            while (jsonKeys.hasNext()) {
                                dates[datesLength] = jsonKeys.next();
                                datesLength--;
                            }
                            graphData = new LineGraphSeries();

                            for (int i = 0; i < dates.length; i++) {
                                JSONObject stockDay = stock.getJSONObject(dates[i]);
                                String price = stockDay.getString("4. close");
                                String[] date = dates[i].split("-");
                                Calendar cal = new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));
                                graphData.appendData(new DataPoint(cal.getTimeInMillis(), Double.parseDouble(price)), true, dates.length);
                            }

                            graph.getGridLabelRenderer().setTextSize(25);
                            graph.getGridLabelRenderer().setNumHorizontalLabels(5);
                            graph.getViewport().setYAxisBoundsManual(true);
                            graph.getViewport().setMinX(graphData.getLowestValueX());
                            graph.getViewport().setMaxX(graphData.getHighestValueX());
                            graph.getViewport().setMinY(graphData.getLowestValueY() * 0.9);
                            graph.getViewport().setMaxY(graphData.getHighestValueY() * 1.1);
                            graph.getViewport().setScalable(true);
                            graph.getLegendRenderer().setVisible(true);
                            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            graphData.setTitle(chosenStock.symbol);

                            graph.removeAllSeries();
                            graph.addSeries(graphData);

                            graph.setVisibility(View.VISIBLE);
                            graphProgressBar.setVisibility(View.GONE);

                            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        Format formatter = new SimpleDateFormat("MMM yy");
                                        return formatter.format(value);
                                    }
                                    return super.formatLabel(value, isValueX);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            graphProgressBar.setVisibility(View.GONE);
                            Toast.makeText(StockPriceHistory.this, "Something gone wrong! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }


    private void showOneYear(String stockSymbol) {
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol=" + stockSymbol + "&apikey=IBBOJPT8T6NZA44K";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int timeRange = 52;
                            JSONObject stock = response.getJSONObject("Weekly Time Series");
                            Iterator<String> jsonKeys = stock.keys();
                            int datesLength = stock.length();
                            if (datesLength < timeRange + 1) {
                                dates = new String[datesLength];
                                datesLength--;
                            } else {
                                dates = new String[timeRange];
                                datesLength = timeRange - 1;
                            }
                            int iter = 1;
                            while (jsonKeys.hasNext() && iter <= timeRange) {
                                dates[datesLength] = jsonKeys.next();
                                datesLength--;
                                iter++;
                            }
                            graphData = new LineGraphSeries();
                            if (dates.length == timeRange) {
                                for (int i = 0; i < timeRange; i++) {
                                    JSONObject stockDay = stock.getJSONObject(dates[i]);
                                    String price = stockDay.getString("4. close");
                                    String[] date = dates[i].split("-");
                                    Calendar cal = new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));
                                    graphData.appendData(new DataPoint(cal.getTimeInMillis(), Double.parseDouble(price)), true, timeRange);
                                }
                            } else {
                                for (int i = 0; i < dates.length; i++) {
                                    JSONObject stockDay = stock.getJSONObject(dates[i]);
                                    String price = stockDay.getString("4. close");
                                    String[] date = dates[i].split("-");
                                    Calendar cal = new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));
                                    graphData.appendData(new DataPoint(cal.getTimeInMillis(), Double.parseDouble(price)), true, dates.length);
                                }
                            }

                            graph.getGridLabelRenderer().setTextSize(25);
                            graph.getGridLabelRenderer().setNumHorizontalLabels(5);
                            graph.getViewport().setYAxisBoundsManual(true);
                            graph.getViewport().setMinX(graphData.getLowestValueX());
                            graph.getViewport().setMaxX(graphData.getHighestValueX());
                            graph.getViewport().setMinY(graphData.getLowestValueY() * 0.9);
                            graph.getViewport().setMaxY(graphData.getHighestValueY() * 1.1);
                            graph.getViewport().setScalable(true);
                            graph.getLegendRenderer().setVisible(true);
                            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            graphData.setTitle(chosenStock.symbol);

                            graph.removeAllSeries();
                            graph.addSeries(graphData);

                            graph.setVisibility(View.VISIBLE);
                            graphProgressBar.setVisibility(View.GONE);

                            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        Format formatter = new SimpleDateFormat("MMM yy");
                                        return formatter.format(value);
                                    }
                                    return super.formatLabel(value, isValueX);

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            graphProgressBar.setVisibility(View.GONE);
                            Toast.makeText(StockPriceHistory.this, "Something gone wrong! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }


    private void showOneMonth(String stockSymbol) {
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + stockSymbol + "&apikey=IBBOJPT8T6NZA44K";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int timeRange = 30;
                            JSONObject stock = response.getJSONObject("Time Series (Daily)");
                            Iterator<String> jsonKeys = stock.keys();
                            int datesLength = stock.length();
                            if (datesLength < timeRange + 1) {
                                dates = new String[datesLength];
                                datesLength--;
                            } else {
                                dates = new String[timeRange];
                                datesLength = timeRange - 1;
                            }
                            int iter = 1;
                            while (jsonKeys.hasNext() && iter <= timeRange) {
                                dates[datesLength] = jsonKeys.next();
                                datesLength--;
                                iter++;
                            }
                            graphData = new LineGraphSeries();
                            if (dates.length == timeRange) {
                                for (int i = 0; i < timeRange; i++) {
                                    JSONObject stockDay = stock.getJSONObject(dates[i]);
                                    String price = stockDay.getString("4. close");
                                    String[] date = dates[i].split("-");
                                    Calendar cal = new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));
                                    graphData.appendData(new DataPoint(cal.getTimeInMillis(), Double.parseDouble(price)), true, timeRange);
                                }
                            } else {
                                for (int i = 0; i < dates.length; i++) {
                                    JSONObject stockDay = stock.getJSONObject(dates[i]);
                                    String price = stockDay.getString("4. close");
                                    String[] date = dates[i].split("-");
                                    Calendar cal = new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));
                                    graphData.appendData(new DataPoint(cal.getTimeInMillis(), Double.parseDouble(price)), true, dates.length);
                                }
                            }

                            graph.getGridLabelRenderer().setTextSize(25);
                            graph.getGridLabelRenderer().setNumHorizontalLabels(5);
                            graph.getViewport().setYAxisBoundsManual(true);
                            graph.getViewport().setMinX(graphData.getLowestValueX());
                            graph.getViewport().setMaxX(graphData.getHighestValueX());
                            graph.getViewport().setMinY(graphData.getLowestValueY() * 0.9);
                            graph.getViewport().setMaxY(graphData.getHighestValueY() * 1.1);
                            graph.getViewport().setScalable(true);
                            graph.getLegendRenderer().setVisible(true);
                            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            graphData.setTitle(chosenStock.symbol);

                            graph.removeAllSeries();
                            graph.addSeries(graphData);

                            graph.setVisibility(View.VISIBLE);
                            graphProgressBar.setVisibility(View.GONE);

                            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        Format formatter = new SimpleDateFormat("MMM dd");
                                        return formatter.format(value);
                                    }
                                    return super.formatLabel(value, isValueX);

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            graphProgressBar.setVisibility(View.GONE);
                            Toast.makeText(StockPriceHistory.this, "Something gone wrong! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void showFiveDays(String stockSymbol) {
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + stockSymbol + "&interval=15min&apikey=IBBOJPT8T6NZA44K";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject stock = response.getJSONObject("Time Series (15min)");
                            Iterator<String> jsonKeys = stock.keys();
                            int datesLength = stock.length();
                            dates = new String[datesLength];
                            datesLength--;
                            while (jsonKeys.hasNext()) {
                                dates[datesLength] = jsonKeys.next();
                                datesLength--;
                            }
                            graphData = new LineGraphSeries();
                            for (int i = 0; i < dates.length; i++) {
                                JSONObject stockDay = stock.getJSONObject(dates[i]);
                                String price = stockDay.getString("4. close");
                                String[] dateAndTime = dates[i].split(" ");
                                String[] date = dateAndTime[0].split("-");
                                String[] time = dateAndTime[1].split(":");
                                Calendar cal = new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
                                graphData.appendData(new DataPoint(cal.getTimeInMillis(), Double.parseDouble(price)), true, dates.length);
                            }
                            graph.getLegendRenderer().setVisible(true);
                            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            graph.getGridLabelRenderer().setTextSize(25);
                            graph.getGridLabelRenderer().setNumHorizontalLabels(4);
                            graph.getViewport().setYAxisBoundsManual(true);
                            graph.getViewport().setMinX(graphData.getLowestValueX());
                            graph.getViewport().setMaxX(graphData.getHighestValueX());
                            graph.getViewport().setMinY(graphData.getLowestValueY() * 0.9);
                            graph.getViewport().setMaxY(graphData.getHighestValueY() * 1.1);
                            graph.getViewport().setScalable(true);
                            graphData.setTitle(chosenStock.symbol);

                            graph.removeAllSeries();
                            graph.addSeries(graphData);

                            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        Format formatter = new SimpleDateFormat("MMM-dd HH");
                                        return formatter.format(value);
                                    }
                                    return super.formatLabel(value, isValueX);

                                }
                            });
                            graph.setVisibility(View.VISIBLE);
                            graphProgressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            graphProgressBar.setVisibility(View.GONE);
                            Toast.makeText(StockPriceHistory.this, "Something gone wrong! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void showOneDay(String stockSymbol) {
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + stockSymbol + "&interval=5min&apikey=IBBOJPT8T6NZA44K";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject stock = response.getJSONObject("Time Series (5min)");
                            Iterator<String> jsonKeys = stock.keys();
                            int datesLength = stock.length();
                            dates = new String[datesLength];
                            datesLength--;
                            while (jsonKeys.hasNext()) {
                                dates[datesLength] = jsonKeys.next();
                                datesLength--;
                            }
                            graphData = new LineGraphSeries();
                            for (int i = 0; i < dates.length; i++) {
                                JSONObject stockDay = stock.getJSONObject(dates[i]);
                                String price = stockDay.getString("4. close");
                                String[] dateAndTime = dates[i].split(" ");
                                String[] date = dateAndTime[0].split("-");
                                String[] time = dateAndTime[1].split(":");
                                Calendar cal = new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
                                graphData.appendData(new DataPoint(cal.getTimeInMillis(), Double.parseDouble(price)), true, dates.length);
                            }
                            graph.getLegendRenderer().setVisible(true);
                            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            graph.getGridLabelRenderer().setTextSize(25);
                            graph.getGridLabelRenderer().setNumHorizontalLabels(5);
                            graph.getViewport().setYAxisBoundsManual(true);
                            graph.getViewport().setMinX(graphData.getLowestValueX());
                            graph.getViewport().setMaxX(graphData.getHighestValueX());
                            graph.getViewport().setMinY(graphData.getLowestValueY() * 0.9);
                            graph.getViewport().setMaxY(graphData.getHighestValueY() * 1.1);
                            graph.getViewport().setScalable(true);
                            graphData.setTitle(chosenStock.symbol);

                            graph.removeAllSeries();
                            graph.addSeries(graphData);

                            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        Format formatter = new SimpleDateFormat("HH:mm");
                                        return formatter.format(value);
                                    }
                                    return super.formatLabel(value, isValueX);
                                }
                            });
                            graph.setVisibility(View.VISIBLE);
                            graphProgressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            graphProgressBar.setVisibility(View.GONE);
                            Toast.makeText(StockPriceHistory.this, "Something gone wrong! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}