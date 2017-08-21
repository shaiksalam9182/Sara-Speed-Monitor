package salam.raj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText et_mobile_no,et_password;
    Button bt_login;
    ProgressDialog pdLoading;

    FirebaseDatabase mdatabase;
    DatabaseReference mdbreference;
    String mobileno,password;
    ArrayList<Map> user_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pdLoading = new ProgressDialog(Login.this);
        user_details = new ArrayList<Map>();

        mdatabase = FirebaseDatabase.getInstance();
        mdbreference = mdatabase.getReference("user_registration");


        et_mobile_no = (EditText)findViewById(R.id.etmobileno);
        et_password = (EditText)findViewById(R.id.et_login_password);

        bt_login = (Button)findViewById(R.id.btlogin);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdLoading.setMessage("Authenticating Please Wait..");
                pdLoading.setCancelable(false);
                pdLoading.show();


                mobileno = et_mobile_no.getText().toString();
                password = et_password.getText().toString();

                if (mobileno.equals("") || password.equals("")){
                    pdLoading.dismiss();
                    Toast.makeText(Login.this,"Please fill all fields",Toast.LENGTH_SHORT).show();
                }else {
                    Authenticatewithfirebase(mobileno,password);
                }



            }
        });
    }

    private void Authenticatewithfirebase(String mobileno, final String password) {
        Query login_query = mdbreference.orderByChild("phone").equalTo(mobileno);
        login_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dsp : dataSnapshot.getChildren()){
                        user_details.add((Map) dsp.getValue());
                    }
                    if (user_details.get(0).get("password").toString().equals(password)){
                        pdLoading.dismiss();
                        Toast.makeText(Login.this,"Successfully Authenticated",Toast.LENGTH_SHORT).show();
                        //user_details.clear();
                    }else {
                        pdLoading.dismiss();
                        Toast.makeText(Login.this,"Password Is Incorrect",Toast.LENGTH_SHORT).show();
                    }


                    Log.e("details",user_details.toString());

                }else {
                    pdLoading.dismiss();
                    Toast.makeText(Login.this,"user doesn't exist",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void callRegister(View v){
        startActivity(new Intent(Login.this,Register_user.class));
    }
}
