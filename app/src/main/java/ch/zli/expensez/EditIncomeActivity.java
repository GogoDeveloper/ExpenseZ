package ch.zli.expensez;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.HashMap;

public class EditIncomeActivity extends AppCompatActivity {

    EditText editTxtIncome, editTxtFixCosts;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_income);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference incomeRef = database.getReference().child("Income");

        editTxtIncome = findViewById(R.id.etxtIncome);
        editTxtFixCosts = findViewById(R.id.etxtFixCosts);
        btnSave = findViewById(R.id.btnSaveIncome);




        btnSave.setOnClickListener(v -> {
            if (editTxtIncome.getText() != null && editTxtFixCosts.getText() != null){
                incomeRef.child("income").setValue(Float.valueOf(editTxtIncome.getText().toString()));
                incomeRef.child("fixcosts").setValue(Float.valueOf(editTxtFixCosts.getText().toString()));
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                IncomeModel incomeModel = dataSnapshot.getValue(IncomeModel.class);

                if (incomeModel != null){
                    editTxtIncome.setText(String.valueOf(incomeModel.getIncome()));
                    editTxtFixCosts.setText(String.valueOf(incomeModel.getFixcosts()));
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(error);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}
