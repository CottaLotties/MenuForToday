package com.example.menuapplication;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

// activity for the list of dishes
public class ListActivity extends AppCompatActivity {

    DishDao dishDao;
    List<String> list;
    List<Dish> dishes;
    int type;
    SharedPreferences activeAdvice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        activeAdvice = getSharedPreferences("AdviceFile", Activity.MODE_PRIVATE);
        refresh(); // show the list of dishes
    }

    // method to show the list of dishes
    public void refresh(){
        Intent mainIntent = getIntent(); // here we get the dish type from the intent
        type = mainIntent.getIntExtra("type", 1);
        getSupportActionBar().setTitle(getTypeByDish()); // set the title for the dish list

        dishDao = App.getInstance().getDatabase().dishDao();
        list = dishDao.getByType(type);
        dishes = dishDao.getAllByType(type);

        ListView listView = findViewById(R.id.list);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(listAdapter);

        // listener for the ListView elements; here we choose a dish and come back to menu
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent toMain = new Intent(ListActivity.this, MainActivity.class);
            SharedPreferences.Editor editor = activeAdvice.edit();
            editor.putString(getTypeByDish(),((TextView)view).getText().toString());
            editor.apply();
            toMain.putExtra("source","ListActivity");
            ListActivity.this.startActivity(toMain);
            onBackPressed();
        });
        registerForContextMenu(listView);
    }

    // create a small context menu to show when we long click on the dish; we can edit or delete a
    // specific dish
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 1, getString(R.string.edit));
        menu.add(0, v.getId(), 2, getString(R.string.delete));
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    // here we edit or delete the dish
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapter = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String dishName = ((TextView)adapter.targetView).getText().toString();
        long id = dishes.get(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position).id;

        if (item.getOrder()==1){
            // edit selected dish
            showAlertForDishChange(dishName, id);
        }

        else if (item.getOrder()==2){
            // delete the selected dish from the database
            dishDao.deleteById(id);
            refresh();
            checkIfChanged(dishName, -1);
        }

        return super.onContextItemSelected(item);
    }

    // show the dialog for editing the dish
    public void showAlertForDishChange(String dishName, long id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText nameInput = new EditText(this);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin= (int)getResources().getDimension(R.dimen.edit_dialog_margin);
        params.rightMargin= (int)getResources().getDimension(R.dimen.edit_dialog_margin);
        nameInput.setLayoutParams(params);
        container.addView(nameInput);

        builder.setTitle(getString(R.string.edit_dish_dialog_title));
        builder.setView(container);
        nameInput.setText(dishName);

        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            if (!nameInput.getText().toString().equals("")) {
                dishDao.setDish(nameInput.getText().toString(), id);
                refresh();
                checkIfChanged(dishName, id);
            }
            else{
                Toast.makeText(getApplicationContext(), getString(R.string.edit_dish_dialog_warning),
                        Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        AlertDialog editDialog = builder.create();
        editDialog.show();
    }

    // the method to check if the chosen dish for the menu was edited or deleted
    public void checkIfChanged(String dishName, long id){
        SharedPreferences.Editor editor = activeAdvice.edit();
        try{
            String dishType = getTypeByDish();
            if (activeAdvice.getString(dishType, "breakfast").equals(dishName)) {
                if (id != -1) editor.putString(dishType, dishDao.getNameById(id));
                else editor.putString(dishType, "");
                editor.apply();
            }
        } catch (Exception e){}
    }

    // get string dish type by number
    public String getTypeByDish(){
        String dishType;
        switch (type){
            case 2: dishType = getString(R.string.salad);
                break;
            case 3: dishType = getString(R.string.dinner);
                break;
            case 4: dishType = getString(R.string.supper);
                break;
            case 5: dishType = getString(R.string.dessert);
                break;
            case 6: dishType = getString(R.string.order);
                break;
            case 1:
            default: dishType = getString(R.string.breakfast);
                break;
        }
        return dishType;
    }

    // come back to MainActivity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent toMain = new Intent(ListActivity.this, MainActivity.class);
            ListActivity.this.startActivity(toMain);
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
}
