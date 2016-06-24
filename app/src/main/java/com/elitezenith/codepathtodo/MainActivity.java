package com.elitezenith.codepathtodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_EDIT = 0;
    ArrayList<String> todoItems;
    ArrayAdapter<String> aToDoAdapter;
    ListView lvItems;
    EditText etEditText;
    Button btnAddItem;
    Intent intent;
    DBHandler dbHandler;
    String[] pos_idMap = new String[999];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new DBHandler(this,null,null,1);
        setContentView(R.layout.activity_main);
        populateArrayItems();
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aToDoAdapter);
        etEditText = (EditText) findViewById(R.id.etEditText);
        btnAddItem = (Button) findViewById(R.id.btnAddItem);

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (removeItem(position)){
                    todoItems.remove(position);
                    aToDoAdapter.notifyDataSetChanged();
                    etEditText.setText("");
                }
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String editingItem = parent.getItemAtPosition(position).toString();
                intent = new Intent(parent.getContext(),EditItemActivity.class);
                intent.putExtra("editingItem", editingItem);
                intent.putExtra("position", position);
                startActivityForResult(intent,REQUEST_CODE_EDIT);
                // this keeps going, see onActivityResult() for after Activity is done
            }
        });
    }

    private boolean removeItem(int position) {
        boolean retVal = dbHandler.deleteItem(pos_idMap[position]);
        pos_idMap = dbHandler.pos_idMap;
        return retVal;
    }

    public void populateArrayItems() {
        readItems();
        aToDoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,todoItems);
    }

    private void readItems() {
        todoItems = new ArrayList<String>(dbHandler.readAllToList());
        pos_idMap = dbHandler.pos_idMap;
    }

    private boolean addItem() {
        String newItem = etEditText.getText().toString();
        boolean retVal = dbHandler.addItem(newItem);
        if (retVal)
            pos_idMap = dbHandler.pos_idMap;
        return retVal;
    }

    private boolean editItem(int position, String editingItem) {
        String id = pos_idMap[position];
        boolean retVal = dbHandler.UpdateItem(id,editingItem);
        return retVal;
    }

    public void onAddItem(View view) {
        if(etEditText.getText().toString().trim().length() < 1) {
            etEditText.setText("");
            return;
        }

        if (addItem()) {
            aToDoAdapter.add(etEditText.getText().toString());
            aToDoAdapter.notifyDataSetChanged();
            etEditText.setText("");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_CODE_EDIT:
                if(resultCode != 1)
                    break;
                String editingItem = data.getStringExtra("editingItem");
                int position = data.getIntExtra("position",-1);
                boolean retVal = editItem(position,editingItem);
                if (retVal) {
                    todoItems.set(position,editingItem);
                    aToDoAdapter.notifyDataSetChanged();
                }
                break;
        }
    }


}
