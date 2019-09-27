package com.tanveer.qhunter.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.tanveer.qhunter.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.profile_fragment,container,false);


        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null){
            SignUpFragment nextFrag= new SignUpFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_container, nextFrag, "signUp")
                    .addToBackStack(null)
                    .commit();
        }else {

        }


        return mView;
    }
}
