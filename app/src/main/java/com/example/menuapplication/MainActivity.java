package com.example.menuapplication;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // dao objects we will need
    DishDao dishDao;
    AdviceDao adviceDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDatabase db = App.getInstance().getDatabase();
        dishDao = db.dishDao();
        adviceDao = db.adviceDao();

        // checking if we should show the tutorial to user
        checkTutorial();

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

    // method to show the tutorial to user; if the app is opened for the first time, the tutorial
    // will be shown
    public void checkTutorial(){
        // checking if the app is opened for the first time
        SharedPreferences start = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = start.getBoolean("ifFirst", false);

        if (!previouslyStarted){
            SharedPreferences.Editor edit = start.edit();
            edit.putBoolean("ifFirst", true);
            edit.apply();

            // showing tutorial dialog
            AlertDialog.Builder tutorialDialogBuilder = new AlertDialog.Builder(this);
            tutorialDialogBuilder.setTitle(getString(R.string.tutorial_dialog_title));
            View view = getLayoutInflater().inflate(R.layout.tutorial_dialog, null);
            tutorialDialogBuilder.setView(view);
            tutorialDialogBuilder.setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());

            AlertDialog tutorialDialog = tutorialDialogBuilder.create();
            tutorialDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // show the latest advice when the activity resumes
        showLatestAdvice();
    }

    // method to show the last menu advice
    public void showLatestAdvice(){
        Advice latestAdvice = adviceDao.getAdvice(0);
        try {
            ((Button) findViewById(R.id.breakfast)).setText(dishDao.getNameById(latestAdvice.breakfastId));
            ((Button) findViewById(R.id.salad)).setText(dishDao.getNameById(latestAdvice.saladId));
            ((Button) findViewById(R.id.dinner)).setText(dishDao.getNameById(latestAdvice.dinnerId));
            ((Button) findViewById(R.id.supper)).setText(dishDao.getNameById(latestAdvice.supperId));
            ((Button) findViewById(R.id.dessert)).setText(dishDao.getNameById(latestAdvice.dessertId));
            ((Button) findViewById(R.id.order)).setText(dishDao.getNameById(latestAdvice.orderId));
        } catch(Exception e){}
    }

    // method to show the list of dishes for the chosen meal type
    public void showList(int type){
        Intent listIntent = new Intent(MainActivity.this, ListActivity.class);
        listIntent.putExtra("type", type);
        MainActivity.this.startActivity(listIntent);
    }

    // method that creates the random menu advice
    public void showAdvice(){
        Advice advice = new Advice();
        // choose the random dish for every meal type
        advice.id = 0;
        advice.breakfastId = setDish(dishDao.getAllByType(1)).id;
        advice.saladId = setDish(dishDao.getAllByType(2)).id;
        advice.dinnerId = setDish(dishDao.getAllByType(3)).id;
        advice.supperId = setDish(dishDao.getAllByType(4)).id;
        advice.dessertId = setDish(dishDao.getAllByType(5)).id;
        advice.orderId = setDish(dishDao.getAllByType(6)).id;
        adviceDao.removeAdvice(0);
        adviceDao.insertAdvice(advice);
        showLatestAdvice();
    }

    // method that chooses a random dish for chosen meal type
    public Dish setDish(List<Dish> arr){
        Random rand = new Random();
        int elementNum = rand.nextInt(arr.size());
        if (elementNum>0) return arr.get(rand.nextInt(arr.size()));
        else return  null;
    }

    // show the dialog to add a new dish
    private void showAddDialog() {
        AlertDialog.Builder addDialogBuilder = new AlertDialog.Builder(this);
        addDialogBuilder.setTitle(getString(R.string.add_dish_dialog_title));
        // set a layout for the dialog
        View v = getLayoutInflater().inflate(R.layout.add_dialog, null);
        addDialogBuilder.setView(v);

        addDialogBuilder.setPositiveButton(getString(R.string.ok), (arg0, arg1) -> {
            // check if the name of a dish is not empty
            String dishName = ((EditText) v.findViewById(R.id.dishName)).getText().toString();
            if (dishName.equals(""))Toast.makeText(getApplicationContext(),
                    getString(R.string.add_dish_dialog_warning_1), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(),
                        getString(R.string.add_dish_dialog_warning_2), Toast.LENGTH_LONG).show();
            }
        });

        addDialogBuilder.setNegativeButton(getString(R.string.cancel), (dialog, which) ->
                dialog.dismiss());

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
        int dishType = 1; // default type: breakfast
        String [] types = new String[]{getString(R.string.breakfast), getString(R.string.salad),
                getString(R.string.dinner), getString(R.string.supper), getString(R.string.dessert),
                getString(R.string.order)};
        for (int i=0; i<types.length; i++){
            if (types[i].equals(type)) dishType=i+1;
        }
        return dishType;
    }
}