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
import com.google.firebase.database.DatabaseReference;

public class LoginFragment extends Fragment {

    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private TextView tv_signin;

    //FIREBASE
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login,container,false);
        et_email=view.findViewById(R.id.et_email);
        et_password=view.findViewById(R.id.et_password);
        btn_login=view.findViewById(R.id.btn_login);
        tv_signin=view.findViewById(R.id.tv_signin);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String password =et_password.getText().toString();
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getContext(),"All Fields are Required ",Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(getActivity(), MainActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                                        getActivity().finish();
                                    }
                                    else {
                                        Toast.makeText(getContext(),"Authentication Failed !",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        return view;

    }
}