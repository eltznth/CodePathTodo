package com.elitezenith.codepathtodo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {
    EditText etEditItem;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Intent intent = getIntent();
        String editingItem = intent.getStringExtra("editingItem");
        position = intent.getIntExtra("position",-1);
        etEditItem = (EditText) findViewById(R.id.etEditItem);
        etEditItem.setText(editingItem);
        etEditItem.setSelection(etEditItem.getText().length());
        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
         }

    public void onSave(View view) {
        String editingItem = etEditItem.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("editingItem", editingItem);
        intent.putExtra("position",position);
        setResult(1,intent);
        finish();
    }

    public void onCancel(View view) {
        finish();
    }
}
