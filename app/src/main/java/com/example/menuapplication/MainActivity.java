package com.example.menuapplication;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DishDao dishDao;
    SharedPreferences activeAdvice;// SharedPreferences object to save our advice

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activeAdvice = getSharedPreferences("AdviceFile", Activity.MODE_PRIVATE);
        showAdvice();

        // adding listener for the button that shows the dish add dialog
        FloatingActionButton add = findViewById(R.id.add);
        add.setOnClickListener(v -> showAddDialog());

        // adding listener for the button that refreshes the menu advice
        FloatingActionButton refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(v -> showAdvice());

        // adding listeners for the buttons that represent different meal types
        findViewById(R.id.breakfast).setOnClickListener(this);
        findViewById(R.id.salad).setOnClickListener(this);
        findViewById(R.id.dinner).setOnClickListener(this);
        findViewById(R.id.supper).setOnClickListener(this);
        findViewById(R.id.dessert).setOnClickListener(this);
        findViewById(R.id.order).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveAdvice(); // save the menu advice when the activity is destroyed
    }

    public void saveAdvice(){
        // here we save the menu advice in Shared Preferences
        SharedPreferences.Editor editor = activeAdvice.edit();
        editor.putString("breakfast",((Button)findViewById(R.id.breakfast)).getText().toString());
        editor.putString("salad",((Button)findViewById(R.id.salad)).getText().toString());
        editor.putString("dinner",((Button)findViewById(R.id.dinner)).getText().toString());
        editor.putString("supper",((Button)findViewById(R.id.supper)).getText().toString());
        editor.putString("dessert",((Button)findViewById(R.id.dessert)).getText().toString());
        editor.putString("order",((Button)findViewById(R.id.order)).getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // show the latest advice when the activity resumes
        showLatestAdvice();
    }

    // method to show the last menu advice
    public void showLatestAdvice(){
        try {
            ((Button) findViewById(R.id.breakfast)).setText(activeAdvice.getString("breakfast",""));
            ((Button) findViewById(R.id.salad)).setText(activeAdvice.getString("salad",""));
            ((Button) findViewById(R.id.dinner)).setText(activeAdvice.getString("dinner",""));
            ((Button) findViewById(R.id.supper)).setText(activeAdvice.getString("supper",""));
            ((Button) findViewById(R.id.dessert)).setText(activeAdvice.getString("dessert",""));
            ((Button) findViewById(R.id.order)).setText(activeAdvice.getString("order",""));
        } catch(Exception e){}
    }

    // method to show the list of dishes for the chosen meal type
    public void showList(int type){
        Intent listIntent = new Intent(MainActivity.this, ListActivity.class);
        listIntent.putExtra("type",type);
        MainActivity.this.startActivity(listIntent);
    }

    // method that creates the random menu advice
    public void showAdvice(){
        // !Fix! here we get db and dao, the problem is that we do it in a main thread
        AppDatabase db = App.getInstance().getDatabase();
        dishDao = db.dishDao();

        List<String> breakfasts = dishDao.getByType(1);
        List<String> salads = dishDao.getByType(2);
        List<String> dinners = dishDao.getByType(3);
        List<String> suppers = dishDao.getByType(4);
        List<String> desserts = dishDao.getByType(5);
        List<String> orders = dishDao.getByType(6);

        // choose the random dish for every meal type
        setDish(findViewById(R.id.breakfast), breakfasts);
        setDish(findViewById(R.id.salad), salads);
        setDish(findViewById(R.id.dinner), dinners);
        setDish(findViewById(R.id.supper), suppers);
        setDish(findViewById(R.id.dessert), desserts);
        setDish(findViewById(R.id.order), orders);
    }

    // method that chooses a random dish for chosen meal type
    public void setDish(TextView v, List<String> arr){
        Random rand = new Random();
        try{
            v.setText(arr.get(rand.nextInt(arr.size())));
        } catch (Exception e){
            v.setText("");
        }
    }

    // show the dialog to add a new dish
    private void showAddDialog() {
        AlertDialog.Builder addDialogBuilder = new AlertDialog.Builder(this);
        addDialogBuilder.setTitle("Add new dish");
        // set a layout for the dialog
        View v = getLayoutInflater().inflate(R.layout.add_dialog, null);
        addDialogBuilder.setView(v);

        addDialogBuilder.setPositiveButton("Ok", (arg0, arg1) -> {
            // check if the name of a dish is not empty
            String dishName = ((EditText) v.findViewById(R.id.dishName)).getText().toString();
            if (dishName.equals(""))Toast.makeText(getApplicationContext(),
                    "Fill the name field, please", Toast.LENGTH_LONG).show();
            else try {
                // create a new dish to add to the database
                Dish dish = new Dish();
                int dishType = getDishType(((RadioButton) v.findViewById(
                        ((RadioGroup) v.findViewById(R.id.type)).getCheckedRadioButtonId()))
                        .getText().toString());
                dish.id = dishDao.getMaxId() + 1;
                dish.type = dishType;
                dish.name = dishName;
                dishDao.insert(dish);
            } catch (Exception e){
                Toast.makeText(getApplicationContext(),"Fill both name and type, please",
                        Toast.LENGTH_LONG).show();
            }
        });

        addDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog addDialog = addDialogBuilder.create();
        addDialog.show();
    }

    // onClick() method for buttons that represent dishes
    @Override
    public void onClick(View v) {
        int type;
        switch (v.getId()){
            case R.id.breakfast: type = 1;
                break;
            case R.id.salad: type = 2;
                break;
            case R.id.dinner: type = 3;
                break;
            case R.id.supper: type = 4;
                break;
            case R.id.dessert: type = 5;
                break;
            case R.id.order: type = 6;
                break;
            default:
                type = 1;
                break;
        }
        showList(type);
    }

    // getting dish type for creating and saving the new dish
    public int getDishType(String type){
        int dishType;
        switch (type) {
            case "Salad":
                dishType = 2;
                break;
            case "Dinner":
                dishType = 3;
                break;
            case "Supper":
                dishType = 4;
                break;
            case "Dessert":
                dishType = 5;
                break;
            case "To order":
                dishType = 6;
                break;
            case "Breakfast":
            default:
                dishType = 1;
                break;
        }
        return dishType;
    }
}