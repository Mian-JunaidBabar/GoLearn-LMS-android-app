package com.example.GoLearn.adapter;// Add this import

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.model.AssignmentSubmissionItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AssignmentSubmissionAdapter extends RecyclerView.Adapter<AssignmentSubmissionAdapter.ViewHolder> {
    private List<AssignmentSubmissionItem> submissionList;
    private String classId;
    private String assignmentId;
    private Context context;
    private int assignmentMaxPoints; // New field

    public AssignmentSubmissionAdapter(Context context, List<AssignmentSubmissionItem> submissionList, String classId, String assignmentId, int assignmentMaxPoints) {
        this.submissionList = submissionList;
        this.classId = classId;
        this.assignmentId = assignmentId;
        this.context = context;
        this.assignmentMaxPoints = assignmentMaxPoints;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment_submission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssignmentSubmissionItem item = submissionList.get(position);
        holder.tvStudentName.setText(item.getStudentName());
        holder.tvFileName.setText(item.getFileName());
        holder.tvGrade.setText(item.getGrade());

        holder.btnViewFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getFileUrl()));
            v.getContext().startActivity(intent);
        });

        holder.btnGrade.setOnClickListener(v -> {
            showGradeDialog(item, position);
        });
    }

    private void showGradeDialog(AssignmentSubmissionItem item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Grade");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String gradeStr = input.getText().toString().trim();
            if (gradeStr.isEmpty()) {
                Toast.makeText(context, "Please enter a grade", Toast.LENGTH_SHORT).show();
                return;
            }

            int grade = Integer.parseInt(gradeStr);
            if (grade < 0 || grade > assignmentMaxPoints) {
                Toast.makeText(context, "Please enter points less then total points: " + assignmentMaxPoints, Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to Firebase
            String studentName = item.getStudentName();
            String fileUrl = item.getFileUrl();
            String fileName = item.getFileName();

            // Find the userId from the fileUrl (optional: you can store userId in item model)
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("classes").child(classId)
                    .child("assignments").child(assignmentId)
                    .child("submissions");

            ref.orderByChild("fileUrl").equalTo(fileUrl).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        child.getRef().child("points").setValue(grade);
                        // Update local list
                        submissionList.set(position, new AssignmentSubmissionItem(
                                studentName,
                                fileName,
                                fileUrl,
                                String.valueOf(grade)
                        ));
                        notifyItemChanged(position);
                        Toast.makeText(context, "Grade saved", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error saving grade", Toast.LENGTH_SHORT).show();
                }
            });

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public int getItemCount() {
        return submissionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvFileName, tvGrade;
        Button btnViewFile, btnGrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.studentName);
            tvFileName = itemView.findViewById(R.id.fileName);
            tvGrade = itemView.findViewById(R.id.grade);
            btnViewFile = itemView.findViewById(R.id.btnViewFile);
            btnGrade = itemView.findViewById(R.id.btnGrade);
        }
    }
}
