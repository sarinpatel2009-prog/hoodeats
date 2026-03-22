package com.sarin.hoodeats.services;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarin.hoodeats.models.User;

public class UserService {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private static final String USERS_COLLECTION = "users";

    public UserService() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    // REGISTER - creates auth account + saves user to Firestore
    public Task<Void> registerUser(String email, String password, User user) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid();
                        user.setId(uid);

                        db.collection(USERS_COLLECTION).document(uid).set(user)
                                .addOnCompleteListener(firestoreTask -> {
                                    if (firestoreTask.isSuccessful()) {
                                        taskCompletionSource.setResult(null);
                                    } else {
                                        taskCompletionSource.setException(
                                                firestoreTask.getException() != null
                                                        ? firestoreTask.getException()
                                                        : new Exception("Failed to save user data")
                                        );
                                    }
                                });
                    } else {
                        taskCompletionSource.setException(
                                authTask.getException() != null
                                        ? authTask.getException()
                                        : new Exception("Authentication failed")
                        );
                    }
                });

        return taskCompletionSource.getTask();
    }

    // LOGIN
    public Task<Void> loginUser(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password)
                .continueWith(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return null;
                });
    }

    // LOGOUT
    public void logoutUser() {
        auth.signOut();
    }

    // GET user from Firestore
    public Task<User> getUser(String userId) {
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) return doc.toObject(User.class);
                    }
                    return null;
                });
    }

    // GET currently logged in user
    public Task<User> getCurrentUser() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            return getUser(firebaseUser.getUid());
        }
        return Tasks.forResult(null);
    }

    // UPDATE user details
    public Task<Void> updateUser(User user) {
        return db.collection(USERS_COLLECTION)
                .document(user.getId())
                .set(user);
    }

    // DELETE user
    public Task<Void> deleteUser(String userId) {
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .delete();
    }

    // UPDATE ledger balance only
    public Task<Void> updateLedgerBalance(String userId, double newBalance) {
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .update("ledgerBalance", newBalance);
    }

    // CHECK if a user is currently logged in
    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }
}