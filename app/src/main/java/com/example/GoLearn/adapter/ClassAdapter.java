package com.example.GoLearn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.model.ClassItem;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private Context context;
    private List<ClassItem> classList;
    private OnItemClickListener itemClickListener;

    public ClassAdapter(Context context, List<ClassItem> classList) {
        this.context = context;
        this.classList = classList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassItem classItem = classList.get(position);

        holder.classTitle.setText(classItem.getTitle());
        holder.classDescription.setText(classItem.getDescription());
        holder.classTeacher.setText("By " + classItem.getTeacherName());

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(classItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {

        TextView classTitle, classDescription, classTeacher;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);

            classTitle = itemView.findViewById(R.id.class_title);
            classDescription = itemView.findViewById(R.id.class_description);
            classTeacher = itemView.findViewById(R.id.class_teacher);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ClassItem classItem);
    }
}