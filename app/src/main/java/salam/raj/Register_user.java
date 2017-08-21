package salam.raj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register_user extends AppCompatActivity {

    EditText et_phone,et_first_name,et_last_name,et_password;
    Button btregister;

    FirebaseDatabase database;
    DatabaseReference dbref;

    String phone,firstname,lastname,password;

    SharedPreferences sdf;
    SharedPreferences.Editor editor;

    ProgressDialog pdLoading;

    String sphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        pdLoading = new ProgressDialog(Register_user.this);

        sdf = getApplicationContext().getSharedPreferences("users",0);
        editor = sdf.edit();

        database = FirebaseDatabase.getInstance();
        dbref= database.getReference("user_registration");

        sphone = sdf.getString("userphoneno",null);
        /*if (sphone!=null){
            startActivity(new Intent(Register_user.this,MainActivity.class));
        }*/

        et_phone = (EditText)findViewById(R.id.etphoneno);
        et_first_name = (EditText)findViewById(R.id.etfirstname);
        et_last_name = (EditText)findViewById(R.id.etlastname);
        et_password = (EditText)findViewById(R.id.etpassword);

        btregister = (Button)findViewById(R.id.bt_register);

        btregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdLoading.setMessage("Registering Please Wait...");
                pdLoading.setCancelable(false);
                pdLoading.show();
                phone = et_phone.getText().toString();
                firstname = et_first_name.getText().toString();
                lastname = et_last_name.getText().toString();
                password  = et_password.getText().toString();
                final String conc = firstname+","+lastname;

                String key = dbref.push().getKey();
                dbref.child(key).child("phone").setValue(phone);
                dbref.child(key).child("firstname").setValue(firstname);
                dbref.child(key).child("lastname").setValue(lastname);
                dbref.child(key).child("password").setValue(password);


                dbref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        pdLoading.dismiss();
                        Toast.makeText(Register_user.this,"Successfully Registered",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register_user.this,Login.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        pdLoading.dismiss();
                        Toast.makeText(Register_user.this,"Error In Registration",Toast.LENGTH_SHORT).show();
                    }
                });






            }
        });
    }
}
