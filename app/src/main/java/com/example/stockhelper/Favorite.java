package com.example.stockhelper;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Favorite extends AppCompatActivity {

    SearchView searchView;
    ListView listView;
    ProgressBar searchProgressBar;
    Map stockSymbols = new HashMap();
    String[] nameList = {};
    ArrayAdapter<String> arrayAdapter;

    private FirebaseAuth mAuth;
    private RequestQueue mQueue;
    private String currentFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        searchProgressBar = findViewById(R.id.searchProgressBarFav);
        loadHashMapFromJson();
        searchingViewList();

        mAuth = FirebaseAuth.getInstance();
        mQueue = Volley.newRequestQueue(this);

    }


    public void searchingViewList() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(mAuth.getCurrentUser().getUid()).hasChild("fav")) {
                    currentFav = snapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("fav").getValue().toString();

                    try {
                        JSONArray jsonArray = new JSONArray(loadJSONFromAsset("AllCompaniesWithSymbolsTrimmed.json"));
                        List<String> temporaryList = new ArrayList<String>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject companyDetail = jsonArray.getJSONObject(i);
                            if (currentFav.matches(".*\\b" + companyDetail.getString("Symbol") + "\\b.*")) {
                                temporaryList.add(companyDetail.getString("Name"));
                            }
                        }
                        nameList = temporaryList.toArray(nameList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    searchView = findViewById(R.id.searchBar);
                    listView = findViewById(R.id.listItem);
                    arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, nameList);
                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            searchProgressBar.setVisibility(View.VISIBLE);
                            jsonParse((String) adapterView.getItemAtPosition(i).toString());
                        }
                    });
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            Favorite.this.arrayAdapter.getFilter().filter(query);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            Favorite.this.arrayAdapter.getFilter().filter((newText));
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void loadHashMapFromJson() {

        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset("AllCompaniesWithSymbolsTrimmed.json"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject companyDetail = jsonArray.getJSONObject(i);
                stockSymbols.put(companyDetail.getString("Name"), companyDetail.getString("Symbol"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void jsonParse(String stockName) {
        String stockSymbol = stockSymbols.get(stockName).toString();
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + stockSymbol + "&apikey=IBBOJPT8T6NZA44K";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject stock = response.getJSONObject("Global Quote");
                            String open = stock.getString("02. open");
                            String high = stock.getString("03. high");
                            String low = stock.getString("04. low");
                            String price = stock.getString("05. price");
                            String volume = stock.getString("06. volume");
                            String lastTradingDay = stock.getString("07. latest trading day");
                            String prevClose = stock.getString("08. previous close");
                            String change = stock.getString("09. change");
                            String changePercentage = stock.getString("10. change percent");
                            Stock chosenStock = new Stock(stockSymbol, stockName, open, high, low, price, volume,
                                    lastTradingDay, prevClose, change, changePercentage);
                            Intent intent = new Intent(Favorite.this, StockPage.class);
                            intent.putExtra("chosenStock", chosenStock);
                            searchProgressBar.setVisibility(View.GONE);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Favorite.this, "Something gone wrong! Try again!", Toast.LENGTH_LONG).show();
                            searchProgressBar.setVisibility(View.GONE);
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
