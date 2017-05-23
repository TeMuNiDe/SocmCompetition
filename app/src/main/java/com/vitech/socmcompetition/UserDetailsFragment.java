package com.vitech.socmcompetition;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;



public class UserDetailsFragment extends Fragment {

public EditText name,email,reference,phone,college;

    public UserDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View contentView = inflater.inflate(R.layout.fragment_user_details, container, false);
        name  = (EditText)contentView.findViewById(R.id.user_name);
        email = (EditText)contentView.findViewById(R.id.user_email);
        reference = (EditText)contentView.findViewById(R.id.user_reference);
        phone = (EditText)contentView.findViewById(R.id.user_phone);
        college = (EditText)contentView.findViewById(R.id.user_college);



        return contentView;
    }

}
