package com.example.healthapp.study;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapp.Pacients.PatientAdapter;
import com.example.healthapp.R;
import com.example.healthapp.model.Patient;

import java.util.ArrayList;
import java.util.List;

public class StudyAdapter extends RecyclerView.Adapter<StudyAdapter.StudyViewHolder> {

    private List<Study> studies;     // текущий отображаемый список
    private List<Study> originalListStudy;  // полный список для фильтрации мб в будущем нужен поиск

    private OnStudyMoveListener moveListener;

    private OnStudyClickListener clickListener;

    public interface OnStudyClickListener{
        void onStudyClick(Study study);
    }
    public interface OnStudyMoveListener {
        void onStudyMove(int fromPosition, int toPosition);
    }

    public StudyAdapter(List<Study> studies, OnStudyClickListener listener) {
        this.studies = new ArrayList<>(studies);
        this.clickListener = listener;
    }

    public void updateList(List<Study> newStudies) {
        this.studies = new ArrayList<>(newStudies);
        notifyDataSetChanged();
    }

    public List<Study> getStudies() {
        return new ArrayList<>(studies);
    }

    public void setOnStudyMoveListener(OnStudyMoveListener listener) {
        this.moveListener = listener;
    }
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) return;
        // забираем элемент
        Study moved = studies.remove(fromPosition);

        // вставляем его на новое место
        studies.add(toPosition, moved);

        notifyItemMoved(fromPosition, toPosition);

        if(moveListener != null){
            moveListener.onStudyMove(fromPosition,toPosition);
        }

    }

    @NonNull
    @Override
    public StudyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_study_card, parent, false);
        return new StudyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyViewHolder holder, int position) {
        Study study = studies.get(position);

        holder.tvTitle.setText(study.getTitle());

        String created = holder.itemView.getContext()
                .getString(R.string.field_create_date, study.getDate());

        holder.tvDate.setText(created);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null)
                clickListener.onStudyClick(study);
        });
    }

    @Override
    public int getItemCount() {
        return studies.size();
    }

    static class StudyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDate;
        ImageView ivPhoto;

        public StudyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvStudyTitle);
            tvDate = itemView.findViewById(R.id.tvStudyDate);
        }
    }
}
