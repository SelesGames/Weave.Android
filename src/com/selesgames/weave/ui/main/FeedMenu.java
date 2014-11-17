package com.selesgames.weave.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.selesgames.weave.R;

public class FeedMenu extends FrameLayout {

    public interface OnClickListener {
        void onEdit();

        void onDelete();
    }

    @InjectView(R.id.text_edit)
    TextView mTextEdit;

    @InjectView(R.id.text_delete)
    TextView mTextDelete;

    private PopupWindow mWindow;

    private OnClickListener mClickListener;

    public FeedMenu(Context context) {
        super(context);
        init(context);
    }

    public FeedMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FeedMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.menu_feed, this);

        ButterKnife.inject(this);

        mTextEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onEdit();
                }
            }
        });
        mTextDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onDelete();
                }
            }
        });
    }

    public void setOnClickListener(OnClickListener listener) {
        mClickListener = listener;
    }

    public void show(View anchor) {
        // Create popup window
        mWindow = new PopupWindow(this, anchor.getWidth(), anchor.getHeight(), true);
        mWindow.setOutsideTouchable(true);
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set window position
        int[] pos = new int[2];
        anchor.getLocationInWindow(pos);
        mWindow.setAnimationStyle(R.style.ListItemMenuAnimation);
        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, pos[0], pos[1]);
    }

    public void dismiss() {
        mWindow.dismiss();
    }

}
