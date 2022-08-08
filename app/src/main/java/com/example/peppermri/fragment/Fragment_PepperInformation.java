package com.example.peppermri.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.peppermri.MainActivity;
import com.example.peppermri.MainActivity_Pepper;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.utils.Manager;

public class Fragment_PepperInformation extends Fragment {

    MainActivity mainActivity;
    MainActivity_Pepper mainActivity_Pepper;
    Controller controller;
    Manager manager;

    TextView tvQuestion1,tvQuestion2,tvQuestion3;
    ScrollView svQuestions;
    RadioGroup rgQuestion1,rgQuestion2,rgQuestion3;
    RadioButton rb_Question1_Yes,rb_Question1_No,rb_Question2_Yes,rb_Question2_No,rb_Question3_Yes,rb_Question3_No;
    View vRoot;


    public Fragment_PepperInformation(Controller controller, MainActivity mainActivity, Manager manager) {
        this.controller = controller;
        this.mainActivity = mainActivity;
        this.manager = manager;
    }

    public void resetFragment(MainActivity mainActivity){
        this.mainActivity = mainActivity;

        LayoutInflater inflater = (LayoutInflater)   getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vRoot = inflater.inflate(R.layout.fragment__pepper_information, null);
        initiateControls(vRoot);
    }

    public void resetMainActivity_Pepper(MainActivity_Pepper mainActivity_Pepper){
        this.mainActivity_Pepper = mainActivity_Pepper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            setHasOptionsMenu(true);
            super.onCreate(savedInstanceState);


        }catch(Exception ex){
            String err ="";
            err = ex.getMessage();
            err+="";
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.admin_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        vRoot = inflater.inflate(R.layout.fragment__pepper_information, container, false);
        initiateControls(vRoot);
        // Inflate the layout for this fragment
        return vRoot;
    }

    public void initiateControls(View vRoot){

        svQuestions = vRoot.findViewById(R.id.svQuestions);
        svQuestions.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return manager.manageFragmentView(item);
    }
}