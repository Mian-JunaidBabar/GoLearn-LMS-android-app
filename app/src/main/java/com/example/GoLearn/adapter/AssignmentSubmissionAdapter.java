package com.example.GoLearn.adapter;

    import android.content.Context;
    import android.content.Intent;
    import android.net.Uri;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.GoLearn.R;
    import com.example.GoLearn.model.AssignmentSubmissionItem;

    import java.util.List;

    public class AssignmentSubmissionAdapter extends RecyclerView.Adapter<AssignmentSubmissionAdapter.ViewHolder> {
        private List<AssignmentSubmissionItem> submissionList;
        private String classId;
        private String assignmentId;
        private Context context;

        public AssignmentSubmissionAdapter(Context context, List<AssignmentSubmissionItem> submissionList, String classId, String assignmentId) {
            this.submissionList = submissionList;
            this.classId = classId;
            this.assignmentId = assignmentId;
            this.context = context;
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
                // Handle grading logic here
                // Example: Show a dialog to input grade
            });
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