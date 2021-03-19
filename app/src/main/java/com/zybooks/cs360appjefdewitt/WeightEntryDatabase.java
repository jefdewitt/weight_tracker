package com.zybooks.cs360appjefdewitt;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class WeightEntryDatabase extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "weights.db";

    private static WeightEntryDatabase mWeightEntryDatabase;
    private List<Account> mAccounts;
    private List<WeightEntry> mWeightEntries;

    public static WeightEntryDatabase getInstance(Context context) {
        if (mWeightEntryDatabase == null) {
            mWeightEntryDatabase = new WeightEntryDatabase(context);
        }
        return mWeightEntryDatabase;
    }

    public WeightEntryDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    // table for usernames & passwords
    private static final class AccountTable {
        private static final String TABLE = "accounts";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    // table for weight entries
    private static final class WeightEntryTable {
        private static final String TABLE = "weights";
        private static final String COL_DATE = "date";
        private static final String COL_WEIGHT = "weight";
        private static final String COL_ACCOUNT = "account";
        private static final String COL_GOAL = "goal";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create accounts table
        sqLiteDatabase.execSQL("create table " + AccountTable.TABLE + " (" +
                AccountTable.COL_ID + " integer primary key, " +
                AccountTable.COL_USERNAME + " text, " +
                AccountTable.COL_PASSWORD + " text)");

        // Create weight entries table with foreign key of account number
        sqLiteDatabase.execSQL("create table " + WeightEntryTable.TABLE + " (" +
                WeightEntryTable.COL_DATE + " primary key, " +
                WeightEntryTable.COL_WEIGHT + " text, " +
                WeightEntryTable.COL_ACCOUNT + " int, " +
                WeightEntryTable.COL_GOAL + " boolean, " +
                "foreign key(" + WeightEntryTable.COL_ACCOUNT + ") references " +
                AccountTable.TABLE + "(" + AccountTable.COL_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists " + AccountTable.TABLE);
        sqLiteDatabase.execSQL("drop table if exists " + WeightEntryTable.TABLE);
        onCreate(sqLiteDatabase);
    }

    // Returns account if it exists
    public Account getAccount(CharSequence username, CharSequence password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Account account = new Account();

        String sql = "select * from " + AccountTable.TABLE +
                " where username = " + "'" + username + "'" +
                " and password = " + "'" + password + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            account.setId(cursor.getInt(0));
            account.setUsername(cursor.getString(1));
            account.setPassword(cursor.getString(2));
        }
        cursor.close();

        return account;
    }

    // Returns username if it exists
    public boolean usernameCheck(CharSequence username) {
        SQLiteDatabase db = this.getReadableDatabase();

        boolean usernameExists;

        String sql = "select * from " + AccountTable.TABLE +
                " where username = " + "'" + username + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            usernameExists = true;
        } else {
            usernameExists = false;
        }
        cursor.close();

        return usernameExists;
    }

    // Adds new account
    public long addAccount(CharSequence username, CharSequence password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AccountTable.COL_USERNAME, String.valueOf(username));
        values.put(AccountTable.COL_PASSWORD, String.valueOf(password));

        long accountId = db.insert(AccountTable.TABLE, null, values);
        return accountId;
    }

    // Returns all weight entries for an account
    public List<WeightEntry> getWeightEntries(String accountNumber) {
        List<WeightEntry> weightEntries = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String orderBy = WeightEntryTable.COL_DATE + " desc";

        String sql = "select * from " + WeightEntryTable.TABLE +
                " where account = " + "'" + accountNumber + "'" +
                " and goal IS NULL" +
                " order by " + orderBy;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                WeightEntry weightEntry = new WeightEntry();
                weightEntry.setDate(cursor.getString(0));
                weightEntry.setWeight(cursor.getString(1));
                weightEntries.add(weightEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return weightEntries;
    }

    // Adds new weight entry
    public long addWeightEntry(WeightEntry weightEntry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WeightEntryTable.COL_DATE, weightEntry.getDate());
        values.put(WeightEntryTable.COL_WEIGHT, weightEntry.getWeight());
        values.put(WeightEntryTable.COL_ACCOUNT, weightEntry.getAccount());

        long weightEntryId = db.insert(WeightEntryTable.TABLE, null, values);
        return weightEntryId;
    }

    // Updates existing weight entry
    public void updateWeightEntry(WeightEntry weightEntry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WeightEntryTable.COL_DATE, weightEntry.getDate());
        values.put(WeightEntryTable.COL_WEIGHT, weightEntry.getWeight());
        values.put(WeightEntryTable.COL_ACCOUNT, weightEntry.getAccount());

        db.update(WeightEntryTable.TABLE, values,
                WeightEntryTable.COL_DATE + " = ?",
                new String[] { weightEntry.getDate() });
    }

    // Deletes weight entry
    public void deleteWeightEntry(WeightEntry weightEntry) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(WeightEntryTable.TABLE,
                WeightEntryTable.COL_DATE + " = ?",
                new String[]{weightEntry.getDate()});
    }

    // Returns goal weight for account
    public WeightEntry getGoalWeightEntry(String accountNumber) {

        SQLiteDatabase db = this.getReadableDatabase();

        WeightEntry weightEntry = null;

        String sql = "select * from " + WeightEntryTable.TABLE +
                " where account = " + "'" + accountNumber + "'" +
                " and goal = 1";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            weightEntry = new WeightEntry();
            weightEntry.setWeight(cursor.getString(1));
            weightEntry.setAccount(cursor.getString(2));
            weightEntry.setIsGoal(true);
        }
        cursor.close();

        return weightEntry;
    }

    // Adds goal weight
    public long addGoalWeight(WeightEntry weightEntry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WeightEntryTable.COL_DATE, "NULL");
        values.put(WeightEntryTable.COL_WEIGHT, weightEntry.getWeight());
        values.put(WeightEntryTable.COL_ACCOUNT, weightEntry.getAccount());
        values.put(WeightEntryTable.COL_GOAL, true);

        long weightEntryId = db.insert(WeightEntryTable.TABLE, null, values);
        return weightEntryId;
    }

    // Updates goal weight
    public void updateGoalWeight(WeightEntry weightEntry, String goalWeight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WeightEntryTable.COL_DATE, "NULL");
        values.put(WeightEntryTable.COL_WEIGHT, weightEntry.getWeight());
        values.put(WeightEntryTable.COL_ACCOUNT, weightEntry.getAccount());
        values.put(WeightEntryTable.COL_GOAL, true);

        db.update(WeightEntryTable.TABLE, values,
                WeightEntryTable.COL_WEIGHT + " = ?",
                new String[] { goalWeight });
    }
}
