package com.example.shift.Dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shift.R;

public class ColorDialog extends Dialog{

    private TextView colorPickerTextView;
    private ImageView colorPickerImageView;
    private GridLayout colorGridView;
    private int colorGridColor = Color.parseColor("#000000");
    private OnColorListener onColorListener;
    private TextView okTextView;
    private TextView cancealTextView;

    protected ColorDialog(Context context, OnColorListener onColorListener){
        super(context);
        this.onColorListener = onColorListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycolorpicker_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().getAttributes().gravity = Gravity.TOP;

        colorPickerTextView = findViewById(R.id.colorPickerGridLayout_preTextView);
        colorPickerImageView = findViewById(R.id.colorPickerGridLayout_preimageView);
        colorGridView = findViewById(R.id.colorPickerGridLayout);
        okTextView = findViewById(R.id.colorPickerOK);
        cancealTextView = findViewById(R.id.colorPickerCanceal);

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

        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onColorListener != null){
                    onColorListener.OnColorListener(colorGridColor);
                }
                dismiss();
            }
        });
        cancealTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });




    }
}
