package com.ohmshantiapps.Chats;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ohmshantiapps.model.Status;
import com.ohmshantiapps.model.UserStatus;

import java.util.concurrent.TimeUnit;

public class CleanupWorker extends Worker {

    public CleanupWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Initialize Firebase instances
        DatabaseReference storiesRef = FirebaseDatabase.getInstance().getReference("stories");
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Calculate cutoff time for stories older than 24 hours
        long cutoffTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24);

        // Fetch stories that need to be deleted
        storiesRef.orderByChild("lastUpdated").endAt(cutoffTime).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DataSnapshot storySnapshot : task.getResult().getChildren()) {
                    // Get the story data
                    UserStatus userStatus = storySnapshot.getValue(UserStatus.class);
                    if (userStatus != null && userStatus.getStatuses() != null) {
                        // Delete images from Firebase Storage
                        for (Status status : userStatus.getStatuses()) {
                            String imageUrl = status.getImageUrl();
                            StorageReference photoRef = storage.getReferenceFromUrl(imageUrl);
                            photoRef.delete().addOnSuccessListener(aVoid -> {
                                // Handle successful deletion of image
                            }).addOnFailureListener(e -> {
                                // Handle any errors during image deletion
                            });
                        }
                    }
                    // Remove the old story from Firebase Realtime Database
                    storySnapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                        // Handle successful removal of story
                    }).addOnFailureListener(e -> {
                        // Handle any errors during story removal
                    });
                }
            }
        }).addOnFailureListener(e -> {
            // Handle any errors during data fetch
        });

        return Result.success();
    }
}
