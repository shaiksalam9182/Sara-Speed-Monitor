package salam.raj;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    EditText et_mobile_no,et_password;
    Button bt_login;
    ProgressDialog pdLoading;


    String mobileno,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pdLoading = new ProgressDialog(Login.this);


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

                Authenticatewithfirebase(mobileno,password);

            }
        });
    }

    private void Authenticatewithfirebase(String mobileno, String password) {

    }
}
