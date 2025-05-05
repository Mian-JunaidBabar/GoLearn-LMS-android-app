package com.example.GoLearn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.model.PersonItem;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<PersonItem> students;
    private OnRemoveClickListener removeClickListener;

    public interface OnRemoveClickListener {
        void onRemoveClick(PersonItem student);
    }

    public StudentAdapter(List<PersonItem> students, OnRemoveClickListener listener) {
        this.students = students;
        this.removeClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button removeBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.studentName);
            removeBtn = itemView.findViewById(R.id.removeButton);
        }
    }

    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentAdapter.ViewHolder holder, int position) {
        PersonItem student = students.get(position);
        holder.name.setText(student.getName());
        holder.removeBtn.setOnClickListener(v -> removeClickListener.onRemoveClick(student));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}