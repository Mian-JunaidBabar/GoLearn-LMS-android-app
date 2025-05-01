package com.example.mad_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.R;
import com.example.mad_project.model.AssignmentSubmissionItem;

import java.util.List;

public class AssignmentSubmissionAdapter extends RecyclerView.Adapter<AssignmentSubmissionAdapter.ViewHolder> {
    private final Context context;
    private final List<AssignmentSubmissionItem> submissions;

    public AssignmentSubmissionAdapter(Context context, List<AssignmentSubmissionItem> submissions) {
        this.context = context;
        this.submissions = submissions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, fileName, grade;
        Button btnViewFile, btnGrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            fileName = itemView.findViewById(R.id.fileName);
            grade = itemView.findViewById(R.id.grade);
            btnViewFile = itemView.findViewById(R.id.btnViewFile);
            btnGrade = itemView.findViewById(R.id.btnGrade);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment_submission, parent, false);
        return new ViewHolder(view);
    }

    private boolean isSupportedFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".doc") || lower.endsWith(".docx")
                || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssignmentSubmissionItem item = submissions.get(position);
        holder.studentName.setText(item.getStudentName());
        holder.fileName.setText(item.getFileName());

        // Set grade text
        holder.grade.setText(item.getGrade() != null && !item.getGrade().isEmpty()
                ? "Grade: " + item.getGrade()
                : "Grade: Not Graded");

        // View file button functionality
        holder.btnViewFile.setOnClickListener(v -> {
            if (isSupportedFile(item.getFileName())) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(item.getFileUrl()), getMimeType(item.getFileName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "No app found to open this file type", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Unsupported file type", Toast.LENGTH_SHORT).show();
            }
        });

        // Grade button functionality
        holder.btnGrade.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Enter Grade for " + item.getStudentName());

            final EditText input = new EditText(context);
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("Submit", (dialog, which) -> {
                String grade = input.getText().toString();
                if (!grade.isEmpty()) {
                    item.setGrade(grade);
                    holder.grade.setText("Grade: " + grade);
                    // TODO: Save grade to backend or database here
                } else {
                    Toast.makeText(context, "Grade cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
    }

    private String getMimeType(String fileName) {
        if (fileName.endsWith(".pdf")) return "application/pdf";
        if (fileName.endsWith(".doc")) return "application/msword";
        if (fileName.endsWith(".docx"))
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".png")) return "image/png";
        return "*/*"; // Fallback for unsupported file types
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }
}