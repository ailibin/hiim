package com.aiitec.hiim.im.other;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.aiitec.hiim.R;
import com.aiitec.hiim.annotation.ContentView;
import com.aiitec.hiim.base.BaseKtFragment;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMMessage;

import org.jetbrains.annotations.NotNull;


/**
 * 大图显示的Fragment 可以手势放大缩小
 *
 * @author Anthony
 * @version 1.0
 *          createTime 2017/9/22.
 */
@ContentView(R.layout.afb_fragment_big_image)
public class BigImageFragment extends BaseKtFragment {

    private static final String ORIGINAL_IMAGE_URL = "oriImageUrl";
    private PhotoView iv_image;
    private String oriImageUrl;

    public static BigImageFragment newInstance(TIMMessage message) {
        Bundle args = new Bundle();
        String oriImageUrl = "";
        TIMImageElem elem = (TIMImageElem) message.getElement(0);
        for (final TIMImage image : elem.getImageList()) {
            if (image.getType() == TIMImageType.Original) {
                oriImageUrl = image.getUrl();
                break;
            }
        }
        args.putString(ORIGINAL_IMAGE_URL, oriImageUrl);
        BigImageFragment fragment = new BigImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(@NotNull View view) {

//        iv_image = view.findViewById(R.id.iv_image);
        oriImageUrl = getArguments().getString(ORIGINAL_IMAGE_URL);

        iv_image.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                getActivity().finish();
            }
        });

//        Glide.with(this)
//                .load(oriImageUrl)
//                .into(iv_image);

    }

}
