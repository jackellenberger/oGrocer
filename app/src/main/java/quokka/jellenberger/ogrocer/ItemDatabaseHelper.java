package quokka.jellenberger.ogrocer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jack on 1/12/2016.
 */
public class ItemDatabaseHelper extends SQLiteOpenHelper {

    public String TABLE_NAME;
    public static final String ITEM_ID = "_id";
    public static final String ITEM_NAME ="name";
    public static final String ITEM_PRICES = "prices";
    public static final String ITEM_STORES = "stores";
    public static final String ITEM_INTAB = "in_tab";

    public static final String DATABASE_NAME = "localItemDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    private String DATABASE_CREATE;

    public ItemDatabaseHelper(Context context, String tableName) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        TABLE_NAME = tableName;
        DATABASE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ITEM_ID + " INTEGER PRIMARY KEY , " +
                        ITEM_NAME + TEXT_TYPE + COMMA_SEP +
                        ITEM_PRICES + TEXT_TYPE + COMMA_SEP +
                        ITEM_STORES + TEXT_TYPE + COMMA_SEP +
                        ITEM_INTAB + INT_TYPE +
                        " )";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
