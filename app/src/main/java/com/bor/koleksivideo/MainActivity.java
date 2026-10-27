package com.bor.koleksivideo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String PREFS = "koleksi_video_prefs";
    private static final String KEY_ITEMS = "items";
    private static final int BG = Color.rgb(17, 18, 22);
    private static final int PANEL = Color.rgb(31, 33, 39);
    private static final int WHITE = Color.rgb(245, 245, 245);
    private static final int MUTED = Color.rgb(174, 177, 185);
    private static final int RED = Color.rgb(220, 38, 38);

    private final List<VideoItem> items = new ArrayList<>();
    private LinearLayout listContainer;
    private EditText titleInput;
    private EditText urlInput;
    private EditText noteInput;
    private EditText searchInput;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        loadItems();
        buildScreen();
        renderItems("");
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(20), dp(18), dp(28));
        scroll.addView(root);

        TextView brand = text("KOLEKSI VIDEO", 12, MUTED, true);
        root.addView(brand);
        TextView heading = text("Simpan. Catat. Tonton.", 27, WHITE, true);
        LinearLayout.LayoutParams headingParams = params(-1, -2);
        headingParams.topMargin = dp(8);
        root.addView(heading, headingParams);
        TextView intro = text("Tempat pribadi buat menyimpan link video YouTube dan catatanmu. Video diputar lewat aplikasi atau situs YouTube resmi.", 14, MUTED, false);
        LinearLayout.LayoutParams introParams = params(-1, -2);
        introParams.topMargin = dp(8);
        root.addView(intro, introParams);

        TextView formTitle = text("Tambah video", 18, WHITE, true);
        LinearLayout.LayoutParams ft = params(-1, -2);
        ft.topMargin = dp(24);
        root.addView(formTitle, ft);

        titleInput = input("Judul video (opsional)", false);
        urlInput = input("Tempel link YouTube di sini", false);
        noteInput = input("Catatan pribadi (opsional)", true);
        addInput(root, titleInput);
        addInput(root, urlInput);
        addInput(root, noteInput);

        Button addButton = button("Simpan ke koleksi", RED, WHITE);
        LinearLayout.LayoutParams addParams = params(-1, dp(48));
        addParams.topMargin = dp(10);
        root.addView(addButton, addParams);
        addButton.setOnClickListener(v -> addVideo());

        TextView savedTitle = text("Koleksi kamu", 19, WHITE, true);
        LinearLayout.LayoutParams st = params(-1, -2);
        st.topMargin = dp(28);
        root.addView(savedTitle, st);

        searchInput = input("Cari judul atau catatan...", false);
        addInput(root, searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderItems(s.toString()); }
            @Override public void afterTextChanged(Editable e) { }
        });

        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams listParams = params(-1, -2);
        listParams.topMargin = dp(10);
        root.addView(listContainer, listParams);

        setContentView(scroll);
    }

    private void addVideo() {
        String title = titleInput.getText().toString().trim();
        String url = urlInput.getText().toString().trim();
        String note = noteInput.getText().toString().trim();
        if (!isYouTubeUrl(url)) {
            urlInput.setError("Masukkan link youtube.com atau youtu.be yang valid");
            return;
        }
        if (title.isEmpty()) title = "Video YouTube";
        items.add(0, new VideoItem(title, url, note));
        saveItems();
        titleInput.setText("");
        urlInput.setText("");
        noteInput.setText("");
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(urlInput.getWindowToken(), 0);
        renderItems(searchInput.getText().toString());
        Toast.makeText(this, "Video disimpan", Toast.LENGTH_SHORT).show();
    }

    private boolean isYouTubeUrl(String value) {
        try {
            Uri uri = Uri.parse(value);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (scheme == null || !(scheme.equals("https") || scheme.equals("http")) || host == null) return false;
            host = host.toLowerCase(Locale.ROOT);
            return host.equals("youtube.com") || host.endsWith(".youtube.com") ||
                    host.equals("youtu.be") || host.equals("www.youtu.be");
        } catch (Exception e) {
            return false;
        }
    }

    private void renderItems(String query) {
        if (listContainer == null) return;
        listContainer.removeAllViews();
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        int count = 0;
        for (int i = 0; i < items.size(); i++) {
            final int index = i;
            VideoItem item = items.get(i);
            String haystack = (item.title + " " + item.note + " " + item.url).toLowerCase(Locale.ROOT);
            if (!q.isEmpty() && !haystack.contains(q)) continue;
            count++;

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(14), dp(14), dp(14), dp(14));
            card.setBackgroundColor(PANEL);
            LinearLayout.LayoutParams cp = params(-1, -2);
            cp.bottomMargin = dp(10);
            listContainer.addView(card, cp);

            card.addView(text(item.title, 16, WHITE, true));
            TextView link = text(item.url, 12, MUTED, false);
            link.setTextIsSelectable(true);
            LinearLayout.LayoutParams lp = params(-1, -2);
            lp.topMargin = dp(6);
            card.addView(link, lp);
            if (!item.note.isEmpty()) {
                TextView note = text("Catatan: " + item.note, 13, MUTED, false);
                LinearLayout.LayoutParams np = params(-1, -2);
                np.topMargin = dp(8);
                card.addView(note, np);
            }

            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams ap = params(-1, dp(42));
            ap.topMargin = dp(12);
            card.addView(actions, ap);

            Button play = button("Putar di YouTube", RED, WHITE);
            actions.addView(play, new LinearLayout.LayoutParams(0, -1, 1));
            play.setOnClickListener(v -> openYouTube(item.url));

            Button delete = button("Hapus", Color.rgb(65, 67, 74), WHITE);
            LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(dp(88), -1);
            dp.leftMargin = dp(8);
            actions.addView(delete, dp);
            delete.setOnClickListener(v -> {
                items.remove(index);
                saveItems();
                renderItems(searchInput.getText().toString());
            });
        }
        if (count == 0) {
            String empty = items.isEmpty() ? "Belum ada video. Tempel link YouTube di atas untuk mulai." : "Tidak ada video yang cocok dengan pencarianmu.";
            TextView emptyView = text(empty, 14, MUTED, false);
            emptyView.setPadding(dp(4), dp(16), dp(4), dp(16));
            listContainer.addView(emptyView);
        }
    }

    private void openYouTube(String url) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        appIntent.setPackage("com.google.android.youtube");
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (ActivityNotFoundException ignored) {
                Toast.makeText(this, "Tidak ada aplikasi untuk membuka link ini", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadItems() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String raw = prefs.getString(KEY_ITEMS, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                items.add(new VideoItem(obj.optString("title"), obj.optString("url"), obj.optString("note")));
            }
        } catch (JSONException ignored) {
            items.clear();
        }
    }

    private void saveItems() {
        JSONArray array = new JSONArray();
        for (VideoItem item : items) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("title", item.title);
                obj.put("url", item.url);
                obj.put("note", item.note);
                array.put(obj);
            } catch (JSONException ignored) { }
        }
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(KEY_ITEMS, array.toString()).apply();
    }

    private EditText input(String hint, boolean multiline) {
        EditText edit = new EditText(this);
        edit.setSingleLine(!multiline);
        edit.setTextColor(WHITE);
        edit.setHintTextColor(MUTED);
        edit.setHint(hint);
        edit.setTextSize(14);
        edit.setPadding(dp(13), dp(10), dp(13), dp(10));
        edit.setBackgroundColor(PANEL);
        if (multiline) {
            edit.setMinLines(2);
            edit.setMaxLines(4);
            edit.setGravity( android.view.Gravity.TOP | android.view.Gravity.START );
        }
        return edit;
    }

    private void addInput(LinearLayout parent, EditText edit) {
        LinearLayout.LayoutParams p = params(-1, edit == noteInput ? dp(84) : dp(48));
        p.topMargin = dp(8);
        parent.addView(edit, p);
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private Button button(String label, int background, int foreground) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setTextColor(foreground);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(background));
        return b;
    }

    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static class VideoItem {
        final String title;
        final String url;
        final String note;
        VideoItem(String title, String url, String note) {
            this.title = title;
            this.url = url;
            this.note = note;
        }
    }
}
