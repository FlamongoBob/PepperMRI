package com.example.peppermri.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.peppermri.MainActivity;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.utils.Manager;

public class Fragment_PepperInformation extends Fragment {

    MainActivity mainActivity;
    Controller controller;
    Manager manager;


    public Fragment_PepperInformation(Controller controller, MainActivity mainActivity, Manager manager) {
        this.controller = controller;
        this.mainActivity = mainActivity;
        this.manager = manager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__pepper_information, container, false);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.admin_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return manager.manageFragmentView(item);
    }

}