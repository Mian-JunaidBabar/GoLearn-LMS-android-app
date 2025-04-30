package com.example.mad_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.R;
import com.example.mad_project.model.ClassItem;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private Context context;
    private List<ClassItem> classList;

    public ClassAdapter(Context context, List<ClassItem> classList) {
        this.context = context;
        this.classList = classList;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassItem item = classList.get(position);
        holder.title.setText(item.getClassTitle());
        holder.description.setText(item.getDescription());
        holder.teacher.setText("By " + item.getTeacherName());
        holder.assignments.setText(String.valueOf(item.getPendingAssignments()));
        holder.image.setImageResource(item.getImageResId());
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, teacher, assignments;
        ImageView image;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.class_title);
            description = itemView.findViewById(R.id.class_description);
            teacher = itemView.findViewById(R.id.class_teacher);
            assignments = itemView.findViewById(R.id.class_assignments);
            image = itemView.findViewById(R.id.class_image);
        }
    }
}
