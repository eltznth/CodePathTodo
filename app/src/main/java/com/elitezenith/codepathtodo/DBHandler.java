package com.elitezenith.codepathtodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "codepathTodo.db";
    public static final String TABLE_NAME = "todoList";
    public static final String COL_ID = "_id";
    public static final String COL_TODO = "todo";
    private Context supercontext;
    public String[] pos_idMap = new String[999];

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        supercontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+TABLE_NAME+" ("
                       +COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                       +COL_TODO+" TEXT"
                       +");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addItem(String todoItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TODO,todoItem);
        long liid = db.insert(TABLE_NAME,null,values); // returns last insert id
        if (liid > 0) {
//            Toast.makeText(supercontext,"Insert: "+todoItem+" as id="+liid,Toast.LENGTH_SHORT).show(); // to help debug
            int i;
            for (i=0;i<pos_idMap.length && pos_idMap[i] != null;i++); // make i = the first empty cell of the array
            pos_idMap[i] = Long.toString(liid);
        }
        db.close();
        return liid > 0;
    }

    public List readAllToList() {
        List<String> result = new ArrayList<String>();
        SQLiteDatabase db = getWritableDatabase(); // change to getWritable if there's a problem
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE 1=1;";
        Cursor c = db.rawQuery(query,null);
        int i = 0;
        pos_idMap = new String[999];

        if (c.moveToFirst()) {
            do {
                result.add(c.getString(c.getColumnIndex(COL_TODO)));
                pos_idMap[i++] = c.getString(c.getColumnIndex(COL_ID));
//                Toast.makeText(supercontext, "test:"+c.getString(1), Toast.LENGTH_SHORT).show(); // to help debug
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return result;
    }

    public boolean deleteItem(String id) {
//        Toast.makeText(supercontext,"delete id="+id,Toast.LENGTH_SHORT).show(); // to help debug
        SQLiteDatabase db = getWritableDatabase();
        boolean retVal= db.delete(TABLE_NAME,COL_ID+"="+id,null) > 0;
        if (retVal) {
            String[] newPos_idMap = new String[pos_idMap.length];
            int j = 0;
            for (String aPos_idMap : pos_idMap) {
                if (aPos_idMap == id)
                    continue;
                newPos_idMap[j++] = aPos_idMap;
            }
            pos_idMap = newPos_idMap;
            newPos_idMap = null; // is this the right way to free newPos_idMap from RAM?
        }
        return retVal;
    }

    public boolean UpdateItem(String id, String editingItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TODO,editingItem);
        int retVal = db.update(TABLE_NAME,cv,COL_ID+"="+id,null); // retVal = how many rows affected
//        Toast.makeText(supercontext,"update id="+id+", "+" row updated: "+retVal,Toast.LENGTH_SHORT).show(); // to help debug
        return retVal > 0;
    }
}
