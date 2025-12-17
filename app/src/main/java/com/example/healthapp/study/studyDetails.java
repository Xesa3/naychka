package com.example.healthapp.study;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthapp.R;
import com.example.healthapp.SharedViewModel;


public class studyDetails extends Fragment {

    private Study study;
    private TextView nameStudy, createdAt;
    private EditText textStudy;
    private Button btnBack;
    private SharedViewModel viewModel;
    private ImageView imageView;
    private Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_study_details, container, false);

        if(getArguments() != null){
            study = (Study) getArguments().getSerializable("study");
        }

        nameStudy = view.findViewById(R.id.tvNameStudy);
        createdAt = view.findViewById(R.id.tvDate);
        textStudy = view.findViewById(R.id.etPatientData);
        imageView = view.findViewById(R.id.imagePatient);

        if(study.getPhotoUri() != null){
            uri = Uri.parse(study.getPhotoUri());
            imageView.setImageURI(uri);
        }
        nameStudy.setText(study.getTitle());
        createdAt.setText(getContext().getString(R.string.field_dateResearch,study.getDate()));
        textStudy.setText(study.getFullText());



        btnBack = view.findViewById(R.id.btnBackToPatientDetails);
        btnBack.setOnClickListener(v->{
            getParentFragmentManager().popBackStack();
        });

        return view;
    }
}