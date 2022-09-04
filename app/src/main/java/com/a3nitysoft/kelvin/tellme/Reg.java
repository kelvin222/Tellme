package com.a3nitysoft.kelvin.tellme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class Reg extends AppCompatActivity {
    private Button Logbtn;
    private Button Regbtn;
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPin;
    private EditText mPincon;
    private Spinner mState;
    private Spinner mSex;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference mDatabase;

    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);


        mRegProgress = new ProgressDialog(this);


        firebaseFirestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        mUsername = (EditText) findViewById(R.id.regname);
        mEmail = (EditText) findViewById(R.id.regmail);
        mState = (Spinner) findViewById(R.id.spinstate);
        mSex = (Spinner) findViewById(R.id.spinsex);
        mPin = (EditText) findViewById(R.id.regpin);
        mPin.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        mPincon = (EditText) findViewById(R.id.regpincon);
        mPincon.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        Regbtn = (Button) findViewById(R.id.reg);

        String[] state1 = {"", "Abia State", "Adamawa State", "Akwa Ibom State", "Anambra State", "Bauchi State", "Bayelsa State", "Benue State", "Borno State", "Cross River State", "Delta State", "Ebonyi State", "Enugu State", "Edo State", "Ekiti State", "Gombe State", "Imo State", "Jigawa State", "Kaduna State", "Kano State", "Katsina State", "Kebbi State", "Kogi State", "Kwara State", "Lagos State", "Nasarawa State", "Niger State", "Ogun State", "Ondo State", "Osun State", "Oyo State", "Plateau State", "Rivers State", "Sokoto State", "Taraba State", "Yobe State", "Zamfara State", "FCT Abuja"};
        String[] sex1 = {"", "Male", "Female"};

        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<String>(Reg.this,
                        android.R.layout.simple_spinner_item, state1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mState.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 =
                new ArrayAdapter<String>(Reg.this,
                        android.R.layout.simple_spinner_item, sex1);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSex.setAdapter(adapter2);


        Logbtn = (Button) findViewById(R.id.login);
        Logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent(Reg.this,Login.class);
                startActivity(logIntent);
            }
        });


        Regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name = mUsername.getText().toString();
                String email = mEmail.getText().toString();
                String state = mState.getSelectedItem().toString();
                String sex = mSex.getSelectedItem().toString();
                String pin = mPin.getText().toString();
                String pincon = mPincon.getText().toString();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(state) || !TextUtils.isEmpty(sex) || !TextUtils.isEmpty(pin) || !TextUtils.isEmpty(pincon)){
                    if (pin.equals(pincon)){
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register_user(display_name,email,state,sex,pin);}
                    else {

                        Toast.makeText(Reg.this, "Please Retype Your Pin and Confirm.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
    private void register_user(final String display_name, String email,final String state,final String sex,final String pin) {

        mAuth.createUserWithEmailAndPassword(email, pin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){


                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    String search = display_name.toLowerCase();

                    mDatabase = firebaseFirestore.collection("Users").document(uid);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("about", "am Using Tellme");
                    userMap.put("email", email);
                    userMap.put("sex", sex);
                    userMap.put("state", state);
                    userMap.put("pin", pin);
                    userMap.put("image", "default");
                    userMap.put("thumb", "default");
                    userMap.put("uid", uid);
                    userMap.put("search", search);
                    userMap.put("device_token", device_token);


                    mDatabase.set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mRegProgress.dismiss();

                                Intent mainIntent = new Intent(Reg.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }

                        }
                    });



                } else {

                    mRegProgress.hide();
                    Toast.makeText(Reg.this, "Cannot SignUp. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}

