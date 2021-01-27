package ch.zli.expensez;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private static DecimalFormat df = new DecimalFormat("0.05");

    private static long expenseIndex = 0;

    float todaysLimit, remainingMoney, totalexpense;

    DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM-yyyy");
    DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");

    Button btnSave;
    ListView listViewTDExpenses;
    EditText yourExpense;
    ArrayList<Float> listExpenses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ImageButton settings = findViewById(R.id.settingsBtn);
        settings.setOnClickListener(v -> {
            PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), settings);
            dropDownMenu.getMenuInflater().inflate(R.menu.dropdown_menu, dropDownMenu.getMenu());
            dropDownMenu.setOnMenuItemClickListener(item -> {

                if (item.getTitle().equals("Edit income")) {
                    startActivity(new Intent(this, EditIncomeActivity.class));
                    finish();
                }

                return true;
            });
            dropDownMenu.show();
        });

        btnSave = findViewById(R.id.btnSaveMain);
        listViewTDExpenses = findViewById(R.id.lvTodaysExpenses);
        yourExpense = findViewById(R.id.etxtYourExpense);
        DatabaseReference listRef = database.getReference().child(yearFormat.format(LocalDateTime.now())).child(monthFormat.format(LocalDateTime.now())).child(dayFormat.format(LocalDateTime.now()));


        //listRef.child("List").addListenerForSingleValueEvent();

        if (listExpenses.size() == 0){
            listRef.child("List").get().addOnSuccessListener(dataSnapshot -> {
                for (DataSnapshot item : dataSnapshot.getChildren()) {

                    if (!item.getKey().equals("ListLength")){
                        listExpenses.add(Float.valueOf(Objects.requireNonNull(item.getValue(String.class))));
                    }



                }

            });
        }



        /*for (Object item: listRef.child("List")) {

        }*/

        ArrayAdapter<Float> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listExpenses);
        listViewTDExpenses.setAdapter(arrayAdapter);

        btnSave.setOnClickListener(v -> {
            expenseIndex++;

            listRef.child("List").child("expense" + expenseIndex).setValue(df.format(Float.parseFloat(String.valueOf(yourExpense.getText()))));
            listExpenses.add(Float.valueOf(df.format(Float.parseFloat(String.valueOf(yourExpense.getText())))));
            //listExpenses.add(listRef.child("List").child("expense"+expenseIndex));


        });


        DatabaseReference incomeRef = database.getReference().child("Income");

        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                IncomeModel incomeModel = snapshot.getValue(IncomeModel.class);
                if (incomeModel != null) {
                    float income = incomeModel.getIncome();
                    float fixcosts = incomeModel.getFixcosts();

                    todaysLimit = (income - fixcosts) / 30;

                    TextView txtTLNr = findViewById(R.id.txtTLNr);
                    txtTLNr.setText(df.format(todaysLimit));

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        listRef.child("List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                long listChildren = snapshot.getChildrenCount();
                expenseIndex = listChildren - 1;
                listRef.child("List").child("ListLength").setValue(listChildren - 1);


                //listExpenses.add(Float.valueOf(snapshot.child("expense" + expenseIndex).getValue(String.class)));

                remainingMoney = todaysLimit - totalexpense;

                TextView txtRM = findViewById(R.id.txtRMNr);
                txtRM.setText(df.format(totalexpense));
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }


        });


    }


    //Methods below are for the app to be in fullscreen mode, copied from android docs
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUi() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}