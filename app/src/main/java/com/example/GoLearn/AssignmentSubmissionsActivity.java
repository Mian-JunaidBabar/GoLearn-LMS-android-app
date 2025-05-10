package com.example.GoLearn;

        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.GoLearn.adapter.AssignmentSubmissionAdapter;
        import com.example.GoLearn.model.AssignmentSubmissionItem;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;

        public class AssignmentSubmissionsActivity extends AppCompatActivity {

            private RecyclerView recyclerView;
            private AssignmentSubmissionAdapter adapter;
            private List<AssignmentSubmissionItem> submissionList;
            private DatabaseReference dbRef;

            TextView assignmentTitle, assignmentDescription, assignmentPoints;
            Button btnUpdate, btnDelete;

            private String classId, assignmentId;
            private int totalPoints;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_assignment_submissions);

                classId = getIntent().getStringExtra("classId");
                assignmentId = getIntent().getStringExtra("assignmentId");
                totalPoints = getIntent().getIntExtra("points", 0);

                if (classId == null || assignmentId == null) {
                    Toast.makeText(this, "Error: classId or assignmentId not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                recyclerView = findViewById(R.id.submissions_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                assignmentTitle = findViewById(R.id.assignment_title);
                assignmentDescription = findViewById(R.id.assignment_description);
                assignmentPoints = findViewById(R.id.assignment_points);

                assignmentTitle.setText(getIntent().getStringExtra("title"));
                assignmentDescription.setText(getIntent().getStringExtra("description"));
                assignmentPoints.setText("Points: " + totalPoints);

                btnUpdate = findViewById(R.id.btn_update);
                btnDelete = findViewById(R.id.btn_delete);

                submissionList = new ArrayList<>();
                adapter = new AssignmentSubmissionAdapter(this, submissionList, classId, assignmentId, totalPoints);
                recyclerView.setAdapter(adapter);

                dbRef = FirebaseDatabase.getInstance().getReference();

                loadSubmissions();
            }

            private void loadSubmissions() {
                DatabaseReference submissionsRef = dbRef.child("classes").child(classId).child("assignments").child(assignmentId).child("submissions");
                submissionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        submissionList.clear();
                        List<DataSnapshot> submissionSnapshots = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            submissionSnapshots.add(snapshot);
                        }

                        if (submissionSnapshots.isEmpty()) {
                            adapter.notifyDataSetChanged();
                            return;
                        }

                        final int[] fetchCounter = {0};
                        final int totalSubmissions = submissionSnapshots.size();

                        for (DataSnapshot submissionSnapshot : submissionSnapshots) {
                            String userId = submissionSnapshot.getKey();
                            long points = submissionSnapshot.hasChild("points") ? (long) submissionSnapshot.child("points").getValue() : 0;
                            String grade = (points > 0) ? String.valueOf(points) + " / " + totalPoints : "Not Graded";
                            String fileName = extractFileName(submissionSnapshot.child("fileUrl").getValue(String.class));

                            Log.d("LoadSubmissions", "Fetching user for ID: " + userId);

                            DatabaseReference userRef = dbRef.child("users").child(userId);
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    String studentName = userSnapshot.child("name").getValue(String.class);
                                    Log.d("LoadSubmissions", "User ID: " + userId + ", Fetched Name: " + studentName);
                                    submissionList.add(new AssignmentSubmissionItem(
                                            studentName != null ? studentName : "Unknown User",
                                            fileName,
                                            grade
                                    ));
                                    fetchCounter[0]++;
                                    if (fetchCounter[0] == totalSubmissions) {
                                        Log.d("LoadSubmissions", "All submissions processed, notifying adapter");
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("LoadSubmissions", "Error fetching user data for " + userId, databaseError.toException());
                                    submissionList.add(new AssignmentSubmissionItem(
                                            "Error Fetching Name",
                                            fileName,
                                            grade
                                    ));
                                    fetchCounter[0]++;
                                    if (fetchCounter[0] == totalSubmissions) {
                                        Log.d("LoadSubmissions", "All submissions processed (with error), notifying adapter");
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AssignmentSubmissionsActivity.this, "Error loading submissions", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private String extractFileName(String url) {
                if (url == null) return "No file";
                try {
                    Uri uri = Uri.parse(url);
                    return uri.getLastPathSegment();
                } catch (Exception e) {
                    return "Unknown";
                }
            }
        }