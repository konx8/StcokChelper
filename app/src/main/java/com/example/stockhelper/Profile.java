package com.example.stockhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity implements View.OnClickListener {


    private TextView accBText;
    private Button startButton;
    private ListView listView;
    private List<SharesBought> buyList;
    private RequestQueue mQueue;

    Float stockPrice;
    ArrayAdapter<String> arrayAdapter;
    ArrayList stocks = new ArrayList<>();
    ArrayList total;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        accBText = findViewById(R.id.accBalanceText);
        startButton = findViewById(R.id.startBtn);
        listView = findViewById(R.id.purchaseList);
        startButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        gameStatus();

        mQueue = Volley.newRequestQueue(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("Users").child(mAuth.getCurrentUser().getUid()).hasChild("game")) {
                    databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("money").setValue(1000f);
                    startButton.setText("Reset");
                    accBText.setText("1000");
                } else {
                    confirmDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void confirmDialog() {  // Confirm reset
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage("You really want to reset the game?");
        alertDlg.setCancelable(false);
        alertDlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").removeValue();
                finish();
                startActivity(getIntent());
                }
        });
        alertDlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDlg.create().show();
    }


    public void gameStatus() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(mAuth.getCurrentUser().getUid()).hasChild("game")) {
                    String currentMoney = snapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("money").getValue().toString();
                    startButton.setText("Reset");
                    accBText.setText(currentMoney);
                    if (snapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").hasChild("purchasedStocks")) {
                        String actions = snapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("purchasedStocks").getValue().toString();
                        listView = findViewById(R.id.purchaseList);
                        ArrayList tempActions = new ArrayList<>();
                        ArrayList itemsNames = new ArrayList<>();
                        String[] actionsTable = actions.split(",");
                        for (String item : actionsTable) {
                            StringBuilder ret = new StringBuilder();
                            Matcher matches = Pattern.compile("[A-Za-z0-9_.-]+").matcher(item);
                            while (matches.find()) {
                                ret.append(matches.group());
                            }
                            tempActions.add(ret);
                        }
                        for (int i = 0; i < tempActions.size(); i++) {
                            itemsNames.add(tempActions.get(i));
                        }

                        total = new ArrayList<>();

                        for (int i = 0; i < itemsNames.size(); i++) {
                            total.add(itemsNames.get(i).toString());
                        }

                        stocks.add(actions);
                        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, total);
                        listView.setAdapter(arrayAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String item = listView.getItemAtPosition(i).toString();
                                String[] itemArray = item.split("--");
                                String stockQuantity = itemArray[2].trim();
                                String stockSymbol = itemArray[0].trim();
                                SellStock(stockSymbol, Float.parseFloat(stockQuantity), i);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void SellStock(String stockSymbol, Float stockQunatity, int i) {
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + stockSymbol + "&apikey=IBBOJPT8T6NZA44K";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject stock = response.getJSONObject("Global Quote");
                            String price = stock.getString("05. price");
                            stockPrice = Float.parseFloat(price);
                            float currentMoney = Float.parseFloat(accBText.getText().toString());
                            Float balance = currentMoney + stockPrice * stockQunatity;
                            balance = (float) Math.round(balance * 100) / 100;
                            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("money").setValue(balance);
                            accBText.setText(Float.toString(balance));
                            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("purchasedStocks").child(stockSymbol).removeValue();
                            total.remove(i);
                            arrayAdapter.notifyDataSetChanged();
                            Toast.makeText(Profile.this, "Sold! " + stockSymbol + " for " + stockPrice * stockQunatity + "$", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Profile.this, "Something gone wrong! Try again!", Toast.LENGTH_LONG).show();
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

