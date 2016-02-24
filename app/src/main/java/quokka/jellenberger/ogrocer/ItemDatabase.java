package quokka.jellenberger.ogrocer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 1/12/2016.
 */
public class ItemDatabase {
    private SQLiteDatabase database;
    private ItemDatabaseHelper dbHelper;
    private String[] allColumns = {
            ItemDatabaseHelper.ITEM_ID,
            ItemDatabaseHelper.ITEM_NAME,
            ItemDatabaseHelper.ITEM_PRICES,
            ItemDatabaseHelper.ITEM_STORES,
    };
    public ItemDatabase(Context context, String tableName) {
        dbHelper = new ItemDatabaseHelper(context, tableName);
        dbHelper.getWritableDatabase();
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addItem(ItemInfo item) {
        dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(dbHelper.ITEM_NAME, item.getItemName());
        values.put(dbHelper.ITEM_PRICES, item.s_getPrices());
        values.put(dbHelper.ITEM_STORES, item.s_getStores());
        Log.d("adding into",dbHelper.TABLE_NAME);
        long insertId = database.insert(dbHelper.TABLE_NAME, null, values);
        Cursor cursor = database.query(dbHelper.TABLE_NAME, allColumns, dbHelper.ITEM_ID + " = " + insertId, null, null, null, null);
        cursor.close();
    }

    public void deleteItem(ItemInfo item) {
        long id = item.getItemID();
        database.delete(dbHelper.TABLE_NAME, ItemDatabaseHelper.ITEM_ID + " = " + id, null);
    }

    public List<ItemInfo> getAllItems() {
        List<ItemInfo> items = new ArrayList<>();

        Cursor cursor = database.query(dbHelper.TABLE_NAME, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ItemInfo item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    public List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        Cursor cursor = database.query(dbHelper.TABLE_NAME, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursorToName(cursor);
            names.add(name);
            cursor.moveToNext();
        }
        cursor.close();
        return names;
    }
    public ItemInfo cursorToItem(Cursor cursor) {
        ItemInfo info = new ItemInfo();
        info.setItemID(cursor.getLong(0));
        info.setItemName(cursor.getString(1));
        info.setPrices(cursor.getString(2));
        info.setStores(cursor.getString(3));
        return info;
    }
    public String cursorToName(Cursor cursor) {
        return cursor.getString(1);
    }
}
