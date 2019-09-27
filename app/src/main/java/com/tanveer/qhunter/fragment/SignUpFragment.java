package com.tanveer.qhunter.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tanveer.qhunter.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class SignUpFragment extends Fragment {
    private RelativeLayout phoneSignBtn;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView=inflater.inflate(R.layout.activity_register,container,false);
        phoneSignBtn=mView.findViewById(R.id.phone_number_container);

        phoneSignBtn.setOnClickListener(view -> {
            Toast.makeText(getActivity(),"Work",Toast.LENGTH_LONG).show();

            PhoneNumerProvideFragment nextFrag= new PhoneNumerProvideFragment();
            SignUpFragment signUpFragment= new SignUpFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(signUpFragment)
                    .replace(R.id.profile_container, nextFrag, "Phone")
                    .addToBackStack(null)
                    .commit();



        });

        return mView;

    }
}
