package com.example.stockhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StockPage extends AppCompatActivity implements View.OnClickListener {

    private TextView stockName;
    private TextView stockSymbol;
    private TextView stockPrice;
    private TextView stockChange;
    private TextView stockOpenValue;
    private TextView stockHighValue;
    private TextView stockLowValue;
    private TextView stockPrevCloseValue;
    private Button priceHistoryButton;
    private Button favButton;
    private RequestQueue mQueue;
    private Stock chosenStock;
    private FirebaseAuth mAuth;
    private List<String> FavList;
    private HashMap buyList;

    public static Button buyButton;

    EditText input;
    SharesBought shares = new SharesBought();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_page);

        mQueue = Volley.newRequestQueue(this);
        stockName = findViewById(R.id.stockName);
        stockSymbol = findViewById(R.id.stockSymbol);
        stockPrice = findViewById(R.id.stockPrice);
        stockChange = findViewById(R.id.stockChange);
        stockOpenValue = findViewById(R.id.stockOpenValue);
        stockLowValue = findViewById(R.id.stockLowValue);
        stockHighValue = findViewById(R.id.stockHighValue);
        stockPrevCloseValue = findViewById(R.id.stockPrevCloseValue);
        priceHistoryButton = findViewById(R.id.stockPriceHistoryButton);
        favButton = findViewById(R.id.addToFavButton);
        buyButton = findViewById(R.id.buyStocks);

        priceHistoryButton.setOnClickListener(this);
        favButton.setOnClickListener(this);
        buyButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        buyButton.setVisibility(View.GONE);
        setBuyButtonVisibility();

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);

        chosenStock = getIntent().getParcelableExtra("chosenStock");
        stockName.setText(chosenStock.name);
        stockSymbol.setText(chosenStock.symbol);
        stockPrice.setText(df.format(Float.parseFloat(chosenStock.price)) + " USD");
        stockChange.setText(df.format(Float.parseFloat(chosenStock.change)) +
                " (" + chosenStock.changePercentage + ")");
        Float changeValue = Float.parseFloat(chosenStock.change);
        if (changeValue < 0) {
            stockChange.setTextColor(Color.rgb(200, 0, 0));
        } else if (changeValue == 0) {
            stockChange.setTextColor(Color.rgb(204, 204, 204));
        } else {
            stockChange.setTextColor(Color.rgb(0, 200, 0));
        }
        stockOpenValue.setText(df.format(Float.parseFloat(chosenStock.open)) + "$");
        stockLowValue.setText(df.format(Float.parseFloat(chosenStock.low)) + "$");
        stockHighValue.setText(df.format(Float.parseFloat(chosenStock.high)) + "$");
        stockPrevCloseValue.setText(df.format(Float.parseFloat(chosenStock.pervClose)) + "$");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stockPriceHistoryButton:
                chosenStock = getIntent().getParcelableExtra("chosenStock");
                Intent intent = new Intent(this, StockPriceHistory.class);
                intent.putExtra("chosenStock", chosenStock);
                startActivity(intent);
                break;
            case R.id.addToFavButton:
                chosenStock = getIntent().getParcelableExtra("chosenStock");
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("Users").child(mAuth.getCurrentUser().getUid()).hasChild("fav")) {
                            String currentFav = snapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("fav").getValue().toString();
                            FavList = new ArrayList<>();
                            Boolean hasSymbol = false;
                            if (currentFav.contains("[")) {
                                currentFav = currentFav.substring(1, currentFav.length() - 1);
                                String[] favArray = currentFav.split(",");
                                for (String item : favArray) {
                                    FavList.add(item.trim());
                                }

                                for (String item : favArray) {
                                    if (item.trim().equals(chosenStock.symbol)) {
                                        FavList.remove(item.trim());
                                        hasSymbol = true;
                                    }
                                }
                                if (!hasSymbol) {
                                    FavList.add(chosenStock.symbol);
                                    Toast toast = Toast.makeText(getApplicationContext(), "Dodano do ulubionych", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Usunieto z ulubionych", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            } else {
                                if (!currentFav.equals(chosenStock.symbol)) {
                                    FavList.add(currentFav);
                                    FavList.add(chosenStock.symbol);
                                }
                            }

                            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("fav").removeValue();
                            if (FavList.size() > 0) {
                                databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("fav").setValue(FavList);
                            }
                        } else {
                            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("fav").setValue(chosenStock.symbol);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                break;
            case R.id.buyStocks:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("The number of Stocks to be bought");
                builder.setMessage("How many stock you want to buy?");
                input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int purchasedQuantity;
                        String tempNumber = input.getText().toString();
                        if (!tempNumber.isEmpty()) {
                            purchasedQuantity = Integer.parseInt(input.getText().toString());
                        } else {
                            purchasedQuantity = 0;
                        }
                        chosenStock = getIntent().getParcelableExtra("chosenStock");
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        if (purchasedQuantity > 0) {
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").hasChild("purchasedStocks")) {
                                        buyList = (HashMap) dataSnapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("purchasedStocks").getValue();
                                        HashMap tempHash = new HashMap();
                                        int ownedStockQuantity = 0;
                                        if (buyList.containsKey(chosenStock.symbol)) {
                                            ownedStockQuantity = Integer.parseInt(dataSnapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("purchasedStocks").child(chosenStock.symbol).child("-- number --").getValue().toString());
                                        }
                                        tempHash.put("-- number --", purchasedQuantity + ownedStockQuantity);
                                        buyList.put(chosenStock.symbol, tempHash);
                                        Object money = dataSnapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("money").getValue();
                                        float currentMoney = Float.parseFloat(money.toString());
                                        float totalPrice = (float) purchasedQuantity * Float.parseFloat(chosenStock.price.toString());
                                        float currentBalance = currentMoney - totalPrice;
                                        if (totalPrice < currentMoney) {
                                            mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("purchasedStocks").setValue(buyList);
                                            mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("money").setValue(String.format("%.2f", currentBalance));
                                            Toast.makeText(getApplicationContext(), "You have bought " + chosenStock.name + " x" + purchasedQuantity + " for " + totalPrice + "$", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Not enough money!!", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        HashMap buyList = new HashMap();
                                        buyList.put("-- number --", purchasedQuantity);
                                        Object money = dataSnapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("money").getValue();
                                        float currentMoney = Float.parseFloat(money.toString());
                                        float totalPrice = (float) purchasedQuantity * Float.parseFloat(chosenStock.price.toString());
                                        float currentBalance = currentMoney - totalPrice;
                                        if (totalPrice < currentMoney) {
                                            mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("purchasedStocks").child(chosenStock.symbol).setValue(buyList);
                                            mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("game").child("money").setValue(String.format("%.2f", currentBalance));
                                            Toast.makeText(getApplicationContext(), "You have bought " + chosenStock.name + " x" + purchasedQuantity + " for " + totalPrice + "$", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Not enough money!", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    databaseError.toException();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "You must enter number bigger then 0!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

        }
    }

    private void setBuyButtonVisibility() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(mAuth.getCurrentUser().getUid()).hasChild("game")) {
                    buyButton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}


