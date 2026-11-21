package com.lazar.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.ViewGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {
    private EditText emailInput, passwordInput, messageInput;
    private TextView resultView;
    private String token;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(36, 48, 36, 24);

        TextView title = new TextView(this);
        title.setText("LAZAR\nRevive your thoughts. Rediscover your focus.");
        title.setTextSize(24);
        layout.addView(title);

        emailInput = addField(layout, "Email");
        passwordInput = addField(layout, "Password");
        Button login = new Button(this); login.setText("Log in"); layout.addView(login);
        Button register = new Button(this); register.setText("Create account"); layout.addView(register);

        messageInput = addField(layout, "Write what's on your mind...");
        Button organize = new Button(this); organize.setText("Organize my tasks"); layout.addView(organize);
        resultView = new TextView(this); resultView.setText("Log in to start using Lazar."); layout.addView(resultView);

        login.setOnClickListener(v -> authenticate(false));
        register.setOnClickListener(v -> authenticate(true));
        organize.setOnClickListener(v -> sendChat());

        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);
    }

    private EditText addField(LinearLayout parent, String hint) {
        EditText edit = new EditText(this);
        edit.setHint(hint);
        edit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.addView(edit);
        return edit;
    }

    private void authenticate(boolean create) {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        Call<ApiModels.AuthResponse> call = create
            ? ApiClient.api().register(new ApiModels.AuthRequest(email, password))
            : ApiClient.api().login(new ApiModels.AuthRequest(email, password));
        call.enqueue(new Callback<>() {
            @Override public void onResponse(Call<ApiModels.AuthResponse> call, Response<ApiModels.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    token = response.body().accessToken;
                    resultView.setText("Signed in as " + response.body().email);
                } else resultView.setText("Authentication failed: HTTP " + response.code());
            }
            @Override public void onFailure(Call<ApiModels.AuthResponse> call, Throwable error) {
                resultView.setText("Network error: " + error.getMessage());
            }
        });
    }

    private void sendChat() {
        if (token == null) { resultView.setText("Please log in first."); return; }
        String message = messageInput.getText().toString().trim();
        if (message.isEmpty()) { resultView.setText("Enter a few thoughts first."); return; }
        ApiModels.ChatRequest request = new ApiModels.ChatRequest(message, "ORGANIZE_TASKS");
        ApiClient.api().chat("Bearer " + token, request).enqueue(new Callback<>() {
            @Override public void onResponse(Call<ApiModels.ChatResponse> call, Response<ApiModels.ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) resultView.setText(response.body().output);
                else resultView.setText("Request failed: HTTP " + response.code());
            }
            @Override public void onFailure(Call<ApiModels.ChatResponse> call, Throwable error) {
                resultView.setText("Network error: " + error.getMessage());
            }
        });
    }
}
