package com.example.mynotes.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynotes.MainActivity;
import com.example.mynotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupFragment extends Fragment {

    private EditText et_email;
    private EditText et_password;
    private Button btn_signin;
    private TextView tv_login;

    //FIREBASE
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup,container,false);
        et_email=view.findViewById(R.id.et_email);
        et_password=view.findViewById(R.id.et_password);
        btn_signin=view.findViewById(R.id.btn_signin);
        tv_login=view.findViewById(R.id.tv_login);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                if(email.isEmpty()){
                    et_email.setError("Email_Id is required !");
                    et_email.requestFocus();
                }
                if(password.isEmpty() || password.length() <= 6){
                    et_password.setError("Password must be more than 6 characters!");
                    et_password.requestFocus();
                }
                if(!email.isEmpty() && !password.isEmpty() && password.length()>6){
                    register(email,password);
                }
            }
        });

        return view;
    }

    public void register(final String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",userId);
                            hashMap.put("username",email);
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(),"Authentication Successful",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), MainActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                                        getActivity().finish();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(getActivity(),"Registration Failed !",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}