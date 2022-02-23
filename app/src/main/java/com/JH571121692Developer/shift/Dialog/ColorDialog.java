package com.JH571121692Developer.shift.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.JH571121692Developer.shift.R;

public class ColorDialog extends Dialog{

    private TextView colorPickerTextView;
    private ImageView colorPickerImageView;
    private GridLayout colorGridView;
    private int colorGridColor;
    private OnColorListener onColorListener;

    protected ColorDialog(Context context, OnColorListener onColorListener){
        super(context);
        this.onColorListener = onColorListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycolorpicker_dialog);

        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.6);
        //높이 설정
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;
        getWindow().setBackgroundDrawableResource(R.drawable.round_border);

        colorPickerTextView = findViewById(R.id.colorPickerGridLayout_preTextView);
        colorPickerImageView = findViewById(R.id.colorPickerGridLayout_preimageView);
        colorGridView = findViewById(R.id.colorPickerGridLayout);

        colorGridColor = getContext().getColor(R.color.googleCalendarColor1);

        for(int i =0; i<colorGridView.getChildCount(); i++){
            final ImageView imageView = (ImageView) colorGridView.getChildAt(i);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    colorGridColor = ((ColorDrawable)imageView.getBackground()).getColor();
                    colorPickerImageView.setBackgroundColor(colorGridColor);
                    colorPickerTextView.setText("#" + String.format("%06X", (0xFFFFFF & colorGridColor)));
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doneClick();
        dismiss();
    }

    public void doneClick(){
        if(onColorListener != null){
            onColorListener.OnColorListener(colorGridColor);
        }
        dismiss();
    }
}
