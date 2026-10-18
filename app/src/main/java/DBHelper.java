
package com.example.hostel_application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "hostelconnect.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE wardens (" +
                "email TEXT PRIMARY KEY)");

        db.execSQL("CREATE TABLE complaints (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_name TEXT, " +
                "sap_id TEXT, " +
                "room_number TEXT, " +
                "category TEXT, " +
                "title TEXT, " +
                "description TEXT, " +
                "created_at TEXT, " +
                "status TEXT DEFAULT 'Pending')");

        db.execSQL("CREATE TABLE pest_control (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "details TEXT)");

        db.execSQL("CREATE TABLE laundry (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "details TEXT)");

        db.execSQL("CREATE TABLE mess_menu (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "day TEXT, " +
                "meal TEXT, " +
                "items TEXT)");

        ContentValues warden = new ContentValues();
        warden.put("email", "warden@hostel.local");
        db.insertWithOnConflict(
                "wardens", null, warden, SQLiteDatabase.CONFLICT_IGNORE);

        ContentValues pest = new ContentValues();
        pest.put("date", "To be announced");
        pest.put("details", "Check with the hostel warden.");
        db.insert("pest_control", null, pest);

        ContentValues laundryItem = new ContentValues();
        laundryItem.put("title", "Laundry timings");
        laundryItem.put("details", "Confirm timings with the warden.");
        db.insert("laundry", null, laundryItem);

        ContentValues menu = new ContentValues();
        menu.put("day", "Monday");
        menu.put("meal", "Lunch");
        menu.put("items", "Menu to be announced");
        db.insert("mess_menu", null, menu);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {

        // Preserve existing data while upgrading the complaints table.
        if (oldVersion < 2) {
            db.execSQL(
                    "ALTER TABLE complaints ADD COLUMN student_name TEXT");
            db.execSQL(
                    "ALTER TABLE complaints ADD COLUMN room_number TEXT");
            db.execSQL(
                    "UPDATE complaints SET status = 'Pending' " +
                            "WHERE status IS NULL OR status = 'Submitted'");
        }
    }

    // ---------------- WARDEN AUTHORIZATION ----------------

    public boolean isAuthorizedWarden(String email) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT email FROM wardens WHERE email = ?",
                new String[]{email});

        boolean authorized = cursor.moveToFirst();
        cursor.close();

        return authorized;
    }

    public long addWarden(String email) {
        ContentValues values = new ContentValues();
        values.put("email", email);

        return getWritableDatabase().insertWithOnConflict(
                "wardens", null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    // ---------------- COMPLAINTS ----------------

    // New method: stores the complete complaint.
    public long addComplaint(
            String studentName,
            String sapId,
            String roomNumber,
            String category,
            String description) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("student_name", studentName);
        values.put("sap_id", sapId);
        values.put("room_number", roomNumber);
        values.put("category", category);
        values.put("title", category + " Complaint");
        values.put("description", description);
        values.put("created_at",
                String.valueOf(System.currentTimeMillis()));
        values.put("status", "Pending");

        return db.insert("complaints", null, values);
    }

    // Retained for compatibility with older code.
    public long addComplaint(
            String sapId,
            String category,
            String title,
            String description) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("sap_id", sapId);
        values.put("category", category);
        values.put("title", title);
        values.put("description", description);
        values.put("created_at",
                String.valueOf(System.currentTimeMillis()));
        values.put("status", "Pending");

        return db.insert("complaints", null, values);
    }

    // Warden: all complaints. Student: their own complaints only.
    public Cursor getComplaints(String sapId, boolean warden) {
        SQLiteDatabase db = getReadableDatabase();

        if (warden) {
            return db.rawQuery(
                    "SELECT * FROM complaints ORDER BY id DESC",
                    null);
        }

        return db.rawQuery(
                "SELECT * FROM complaints " +
                        "WHERE sap_id = ? ORDER BY id DESC",
                new String[]{sapId});
    }

    public int updateComplaintStatus(long id, String status) {
        if (!"Pending".equals(status) &&
                !"Completed".equals(status)) {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put("status", status);

        return getWritableDatabase().update(
                "complaints",
                values,
                "id = ?",
                new String[]{String.valueOf(id)});
    }

    // ---------------- PEST CONTROL ----------------

    public long addPestControl(String date, String details) {
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("details", details);

        return getWritableDatabase().insert(
                "pest_control", null, values);
    }

    public Cursor getPestControl() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM pest_control ORDER BY id DESC",
                null);
    }

    public int deletePestControl(long id) {
        return getWritableDatabase().delete(
                "pest_control",
                "id = ?",
                new String[]{String.valueOf(id)});
    }

    // ---------------- LAUNDRY ----------------

    public long addLaundry(String title, String details) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("details", details);

        return getWritableDatabase().insert(
                "laundry", null, values);
    }

    public Cursor getLaundry() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM laundry ORDER BY id DESC",
                null);
    }

    public int deleteLaundry(long id) {
        return getWritableDatabase().delete(
                "laundry",
                "id = ?",
                new String[]{String.valueOf(id)});
    }

    // ---------------- MESS MENU ----------------

    public long addMenuItem(String day, String meal, String items) {
        ContentValues values = new ContentValues();
        values.put("day", day);
        values.put("meal", meal);
        values.put("items", items);

        return getWritableDatabase().insert(
                "mess_menu", null, values);
    }

    public Cursor getMenuItems() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM mess_menu ORDER BY id DESC",
                null);
    }

    public int deleteMenuItem(long id) {
        return getWritableDatabase().delete(
                "mess_menu",
                "id = ?",
                new String[]{String.valueOf(id)});
    }
}
