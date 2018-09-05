package com.aiitec.widgets.pickerview.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;

import com.aiitec.hiim.R;
import com.aiitec.widgets.pickerview.model.Region;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class CityDatebbase extends SQLiteOpenHelper {

    private File file = null;
    private final int BUFFER_SIZE = 1024;
    private static final String DB_NAME = "area_android.db";
    private static String PACKAGE_NAME = "com.aiitec.chxgo";
    private static String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME + "/databases";
    private SQLiteDatabase database;
    private static final int DB_VERSION = 1;
    /**
     * 省市区表
     */
    private static final String TABLE_REGION = "gx_base_region";

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public static String getDbPath() {
        return DB_PATH;
    }

    private static CityDatebbase mInstance;
    private Context context;

    private CityDatebbase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        openDatabase();
    }

    synchronized static CityDatebbase getInstance(Context context) {
        if (mInstance == null) {
            PACKAGE_NAME = context.getPackageName();
            DB_PATH = "/data"
                    + Environment.getDataDirectory().getAbsolutePath() + "/"
                    + PACKAGE_NAME + "/databases";
            mInstance = new CityDatebbase(context);
        }
        return mInstance;
    }

    public synchronized static void destoryInstance() {
        if (mInstance != null) {
            mInstance.close();
        }
    }

    private void openDatabase() {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    private SQLiteDatabase openDatabase(String dbfile) {

        file = new File(dbfile);
        if (!file.exists()) {
            try {
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().getParentFile().exists()) {
                        file.getParentFile().mkdir();
                    }
                    file.getParentFile().mkdir();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            createTableFromRaw(dbfile);
        } else {
            try {
                database = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
                Cursor cursor = database.rawQuery("select count(*)  from sqlite_master where type='table' and name = '" + TABLE_REGION + "'", null);
                if (!cursor.moveToNext()) {//如果表不存在
                    file.delete();
                    createTableFromRaw(dbfile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        database = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        return database;
    }

    private void createTableFromRaw(String dbfile) {
        try {
            InputStream is = context.getResources().openRawResource(
                    R.raw.area_android);
            FileOutputStream fos = new FileOutputStream(dbfile);
            if (is != null) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                    fos.flush();
                }
            } else {
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 查询省市区列表
     *
     * @return 省市区列表
     */
    public synchronized ArrayList<Region> findRegions() {
        if (database == null || !database.isOpen()) {
            database = getReadableDatabase();
        }
        Cursor cr = database.query(TABLE_REGION, null, null, null, null, null,
                null);
        ArrayList<Region> regions = new ArrayList<>();
        if (cr.moveToFirst()) {
            do {

                String name = cr.getString(cr
                        .getColumnIndexOrThrow(RegionField.NAME));
                String pinyin = cr.getString(cr
                        .getColumnIndexOrThrow(RegionField.PINYIN));
                int id = cr.getInt(cr.getColumnIndexOrThrow(RegionField.ID));
                int parentId = cr.getInt(cr
                        .getColumnIndexOrThrow(RegionField.PARENT_ID));
                Region region = new Region();
                region.setName(name);
                region.setParentId(parentId);
                region.setPinyin(pinyin);
                region.setId(id);
                regions.add(region);
            } while (cr.moveToNext());
        }
        cr.close();
        return regions;
    }

    /**
     * 查询省市区列表
     *
     * @return 省市区列表
     */
    public synchronized ArrayList<Region> findProvinceRegions() {
        if (database == null || !database.isOpen()) {
            database = getReadableDatabase();
        }
        Cursor cr = database.query(TABLE_REGION, null, RegionField.PARENT_ID
                + "=1", null, null, null, null);
        ArrayList<Region> regions = new ArrayList<Region>();
        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(cr
                        .getColumnIndexOrThrow(RegionField.NAME));
                String pinyin = cr.getString(cr
                        .getColumnIndexOrThrow(RegionField.PINYIN));
                int id = cr.getInt(cr.getColumnIndexOrThrow(RegionField.ID));
                int parentId = cr.getInt(cr
                        .getColumnIndexOrThrow(RegionField.PARENT_ID));
                Region region = new Region();
                region.setName(name);
                region.setParentId(parentId);
                region.setPinyin(pinyin);
                region.setId(id);
                regions.add(region);

            } while (cr.moveToNext());
        }
        cr.close();
        return regions;
    }

    /**
     * 查询省市区列表
     *
     * @return 省市区列表
     */
    public synchronized ArrayList<Region> findCityRegions(int parentId) {
        if (database == null || !database.isOpen()) {
            database = getReadableDatabase();
        }
        Cursor cr = database.query(TABLE_REGION, null, RegionField.PARENT_ID
                + "=" + parentId, null, null, null, null);
        ArrayList<Region> regions = new ArrayList<Region>();
        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(cr.getColumnIndexOrThrow(RegionField.NAME));
//                String pinyin = cr.getString(cr.getColumnIndexOrThrow(RegionField.PINYIN));
                int id = cr.getInt(cr.getColumnIndexOrThrow(RegionField.ID));
                Region region = new Region();
                region.setName(name);
                region.setParentId(parentId);
//                region.setPinyin(pinyin);
                region.setId(id);
                regions.add(region);
            } while (cr.moveToNext());
        }
        cr.close();
        return regions;
    }

    /**
     * 查询省列表
     *
     * @return 省列表
     */
    public synchronized ArrayList<Region> findProvinceRegions(int deep) {
        if (database == null || !database.isOpen()) {
            database = getReadableDatabase();
        }
        Cursor cr = database.query(TABLE_REGION, null,
                RegionField.DEEP + " =" + deep, null, null, null, null);
        ArrayList<Region> regions = new ArrayList<Region>();
        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(cr.getColumnIndexOrThrow(RegionField.NAME));
//                String pinyin = cr.getString(cr.getColumnIndexOrThrow(RegionField.PINYIN));
                int id = cr.getInt(cr.getColumnIndexOrThrow(RegionField.ID));
                int parentId = cr.getInt(cr.getColumnIndexOrThrow(RegionField.PARENT_ID));
                Region region = new Region();
                region.setName(name);
                region.setParentId(parentId);
//                region.setPinyin(pinyin);
                region.setId(id);
                regions.add(region);
            } while (cr.moveToNext());
        }
        cr.close();
        return regions;
    }

    /**
     * 查询市列表
     *
     * @return 市列表
     */
    public synchronized ArrayList<Region> findCitysRegions(int deep) {
        if (database == null || !database.isOpen()) {
            database = getReadableDatabase();
        }
        Cursor cr = database.query(TABLE_REGION, null, RegionField.DEEP
                + "=" + deep, null, null, null, null);
        ArrayList<Region> regions = new ArrayList<Region>();
        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(cr.getColumnIndexOrThrow(RegionField.NAME));
//                String pinyin = cr.getString(cr.getColumnIndexOrThrow(RegionField.PINYIN));
                int id = cr.getInt(cr.getColumnIndexOrThrow(RegionField.ID));
                int parentId = cr.getInt(cr.getColumnIndexOrThrow(RegionField.PARENT_ID));
                Region region = new Region();
                region.setName(name);
                region.setParentId(parentId);
//                region.setPinyin(pinyin);
                region.setId(id);
                regions.add(region);
            } while (cr.moveToNext());
        }
        cr.close();
        return regions;
    }

    /**
     * 查询区列表
     *
     * @return 区列表
     */
    public synchronized ArrayList<Region> findAreaRegions(int deep) {
        if (database == null || !database.isOpen()) {
            database = getReadableDatabase();
        }
        Cursor cr = database.query(TABLE_REGION, null, RegionField.DEEP
                + "=" + deep, null, null, null, null);
        ArrayList<Region> regions = new ArrayList<Region>();
        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(cr.getColumnIndexOrThrow(RegionField.NAME));
//                String pinyin = cr.getString(cr.getColumnIndexOrThrow(RegionField.PINYIN));
                int id = cr.getInt(cr.getColumnIndexOrThrow(RegionField.ID));
                int parentId = cr.getInt(cr.getColumnIndexOrThrow(RegionField.PARENT_ID));
                Region region = new Region();
                region.setName(name);
                region.setParentId(parentId);
//                region.setPinyin(pinyin);
                region.setId(id);
                regions.add(region);
            } while (cr.moveToNext());
        }
        cr.close();
        return regions;
    }

    /**
     * 查询省市区/县列表
     *
     * @return 省市区/县列表
     */
    public synchronized ArrayList<String> findCityRegionsName(int parentId) {
        if (database == null || !database.isOpen()) {
            database = getReadableDatabase();
        }
        Cursor cr = database.query(TABLE_REGION, null, RegionField.PARENT_ID
                + "=" + parentId, null, null, null, null);
        ArrayList<String> regions = new ArrayList<>();
        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(cr.getColumnIndexOrThrow(RegionField.NAME));
                regions.add(name);
            } while (cr.moveToNext());
        }
        cr.close();
        return regions;
    }


    /**
     * 通过id查询省市区列表
     *
     * @return 省市区列表
     */
    public synchronized Region findRegionsFromId(int id) {
        if (database == null || !database.isOpen()) {
            database = getReadableDatabase();
        }
        //where deep < 5
        Cursor cr = database.query(TABLE_REGION, null, "id=" + id, null, null,
                null, null);
        Region region = null;
        if (cr.moveToFirst()) {
            String name = cr.getString(cr
                    .getColumnIndexOrThrow(RegionField.NAME));
            String pinyin = cr.getString(cr
                    .getColumnIndexOrThrow(RegionField.PINYIN));
            int parentId = cr.getInt(cr
                    .getColumnIndexOrThrow(RegionField.PARENT_ID));
            region = new Region();
            region.setName(name);
            region.setParentId(parentId);
            region.setPinyin(pinyin);
            region.setId(id);
        }
        cr.close();
        return region;
    }

    public synchronized ArrayList<Region> findRegionsFromSearch(String keyword) {
        if (database == null || !database.isOpen()) {
            database = getReadableDatabase();
        }
        Cursor cr = database.query(TABLE_REGION, null, "(name like '%"
                + keyword + "%' or pinyin like '%" + keyword
                + "%' )", null, null, null, null);
        ArrayList<Region> regions = new ArrayList<>();
        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(cr
                        .getColumnIndexOrThrow(RegionField.NAME));
                String pinyin = cr.getString(cr
                        .getColumnIndexOrThrow(RegionField.PINYIN));
                int parentId = cr.getInt(cr
                        .getColumnIndexOrThrow(RegionField.PARENT_ID));
                int id = cr.getInt(cr.getColumnIndexOrThrow(RegionField.ID));
                Region region = new Region();
                region.setName(name);
                region.setParentId(parentId);
                region.setPinyin(pinyin);
                region.setId(id);
                regions.add(region);
            } while (cr.moveToNext());
        }
        cr.close();
        return regions;
    }

    public synchronized ArrayList<Region> findAllRegions() {
        if (database == null || !database.isOpen()) {
            database = getReadableDatabase();
        }
        Cursor cr = database.query(TABLE_REGION, null, "deep < 5", null, null,
                null, null);
        ArrayList<Region> regions = new ArrayList<>();
        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(cr
                        .getColumnIndexOrThrow(RegionField.NAME));
                String pinyin = cr.getString(cr
                        .getColumnIndexOrThrow(RegionField.PINYIN));
                int parentId = cr.getInt(cr
                        .getColumnIndexOrThrow(RegionField.PARENT_ID));
                int id = cr.getInt(cr.getColumnIndexOrThrow(RegionField.ID));
                Region region = new Region();
                region.setName(name);
                region.setParentId(parentId);
                region.setPinyin(pinyin);
                region.setId(id);
                regions.add(region);
            } while (cr.moveToNext());
        }
        cr.close();
        return regions;
    }

    /**
     * 省市区缓存字段 只允许读
     *
     * @author Anthony
     */
    public static class RegionField implements BaseColumns {
        public static final String ID = "id";
        public static final String PARENT_ID = "parent_id";
        public static final String NAME = "name";
        public static final String PINYIN = "pinyin";
        public static final String DEEP = "deep";
        public static final String TIMESTAMP = "timestamp";
    }
}
