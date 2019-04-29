package com.aiitec.hiim.im.other;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aiitec.hiim.R;
import com.aiitec.hiim.adapter.CommonRecyclerViewAdapter;
import com.aiitec.hiim.annotation.ContentView;
import com.aiitec.hiim.base.BaseKtActivity;
import com.aiitec.hiim.im.adapter.PhotoAdapter;
import com.aiitec.hiim.im.entity.Image;
import com.aiitec.hiim.im.location.util.ToastUtil;
import com.aiitec.hiim.im.utils.AiiUtil;
import com.aiitec.hiim.utils.PermissionsUtils;
import com.aiitec.hiim.utils.ScreenUtils;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片选择类
 *
 * @author Anthony
 *         createTime 2017/9/18 12:10
 */
@ContentView(R.layout.activity_photo)
public class PhotoActivity extends BaseKtActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_MODIFIED
    };
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORE = 0x01;
    private static final int PERMISSIONS_REQUEST_CAMERA = 0x02;
    private PhotoAdapter adapter;
    private List<Image> images = new ArrayList<>();
    private List<Image> selectItems = new ArrayList<>();
    private String cameraFilePath;
    public static final int REQUEST_CAMERA = 0x01;
    public static final String IMAGE_RESULT = "images";
    public static final String IMAGE_SELECT_ITEMS = "selectItems";
    public static final String ARG_MAX = "max";
    private PermissionsUtils permissionsUtils;
    private int max = 9;

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {

        addBaseStatusBarView();
        setColumnTitle("照片");
        setRightBtnVisible(true);
        setRightBtnText("发送", ContextCompat.getColor(this,R.color.white));
        setTitleItemClickListener();

        selectItems = getBundle().getParcelable(IMAGE_SELECT_ITEMS);
        max = getBundle().getInt(ARG_MAX, 9);
//        selectItems = (ArrayList<Image>) getIntent().getSerializableExtra("selectItems");
        if (selectItems == null) {
            selectItems = new ArrayList<>();
        }
        RecyclerView photo_recyclerview = (RecyclerView) findViewById(R.id.photo_recyclerview);
        addCameraItem();

        adapter = new PhotoAdapter(getApplicationContext(), images);
        adapter.setOnRecyclerViewItemClickListener(new CommonRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position == 0) {
                    permissionsUtils.requestPermissions(PERMISSIONS_REQUEST_CAMERA, Manifest.permission.CAMERA);
                } else {
                    Image entity = adapter.getItem(position);
                    //最大为1就是单选
                    if (max == 1) {
                        boolean isSelect = entity.isSelected();
                        //把上次选中的取消选中
                        entity.setSelected(!isSelect);
                        if (adapter.selectIndex != -1) {
                            images.get(adapter.selectIndex).setSelected(false);
                        }
                        if (!isSelect) {
                            adapter.selectIndex = position;
                        } else {
                            adapter.selectIndex = -1;
                        }
                        adapter.notifyItemChanged(adapter.selectIndex);
                    } else {
                        boolean isSelect = entity.isSelected();
                        if (!isSelect) {
                            if (adapter.selectNum >= max) {
                                ToastUtil.show(getApplicationContext(), "最多只能选择" + max + "张");
                                return;
                            }
                            adapter.selectNum++;
                        } else {
                            adapter.selectNum--;
                        }
                        entity.setSelected(!isSelect);
                        adapter.update();
//                        adapter.notifyItemChanged(adapter.selectIndex);
                    }

                }
            }
        });

        photo_recyclerview.setAdapter(adapter);

        photo_recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        photo_recyclerview.addItemDecoration(new GridSpacingItemDecoration(3, ScreenUtils.dip2px(getApplicationContext(), 10), true));

        permissionsUtils = new PermissionsUtils(this);
        permissionsUtils.setOnPermissionsListener(new PermissionsUtils.OnPermissionsListener() {
            @Override
            public void onPermissionsSuccess(int requestCode) {
                switch (requestCode) {
                    case PERMISSIONS_REQUEST_READ_EXTERNAL_STORE:
                        getSupportLoaderManager().initLoader(0, null, PhotoActivity.this);
                        break;
                    case PERMISSIONS_REQUEST_CAMERA:
                        startCemare();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPermissionsFailure(int requestCode) {
                switch (requestCode) {
                    case PERMISSIONS_REQUEST_READ_EXTERNAL_STORE:
                        ToastUtil.show(getApplicationContext(), "请开启读取SD卡权限");
                        break;
                    case PERMISSIONS_REQUEST_CAMERA:
                        ToastUtil.show(getApplicationContext(), "请开启拍照权限");
                        break;
                    default:
                        break;
                }
            }
        });
        permissionsUtils.requestPermissions(PERMISSIONS_REQUEST_READ_EXTERNAL_STORE, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    /**
     * 设置标题栏相关按钮的监听
     */
    private void setTitleItemClickListener() {
        setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> paths = new ArrayList<>();
                for (Image image : images) {
                    if (image.isSelected()) {
                        paths.add(image.getPath());
                    }
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra(IMAGE_RESULT, paths);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void startCemare() {
        if (!AiiUtil.isSDCardEnable()) {
            ToastUtil.show(PhotoActivity.this, "SD卡不可用");
            return;
        }
        String cameraPath = AiiUtil.getSDCardPath() + "/giftfly";
        File dir = new File(cameraPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(cameraPath, System.currentTimeMillis() + ".jpg");
        cameraFilePath = file.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void addCameraItem() {
        Image image = new Image();
        image.setPath("相机");
        images.add(image);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // TODO Auto-generated method stub
        // 为了查看信息，需要用到CursorLoader。

        CursorLoader cursorLoader = new CursorLoader(
                this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                STORE_IMAGES,
                null,
                null,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC"
                //时间顺序排序有了，怎么没有倒序？悲剧
        );

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {

        images.clear();
        addCameraItem();
        if (mCursor != null) {
            while (mCursor.moveToNext()) {

                int index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //获取图片的路径
                String path = mCursor.getString(index);
                Image image = new Image();
                for (int i = 0; i < selectItems.size(); i++) {
                    if (path.equals(selectItems.get(i).getPath())) {
                        image.setSelected(true);
                    }
                }
                image.setPath(path);
                images.add(image);
            }

        }
        adapter.setSelectNum(selectItems.size());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        images.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CAMERA == requestCode && resultCode == Activity.RESULT_OK) {
            ArrayList<String> paths = new ArrayList<String>();
            paths.add(cameraFilePath);
            Intent intent = new Intent();
            intent.putStringArrayListExtra(IMAGE_RESULT, paths);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        permissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
