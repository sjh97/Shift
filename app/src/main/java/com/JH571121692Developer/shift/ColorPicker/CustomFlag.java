package com.JH571121692Developer.shift.ColorPicker;

import android.content.Context;
import android.widget.TextView;

import com.JH571121692Developer.shift.R;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.flag.FlagView;

public class CustomFlag extends FlagView {

    private TextView textView;
    private AlphaTileView view;

    public CustomFlag(Context context, int layout){
        super(context, layout);
        textView = findViewById(R.id.flag_color_code);
        view = findViewById(R.id.flag_color_layout);
    }

    @Override
    public void onRefresh(ColorEnvelope colorEnvelope) {
//        textView.setText("#" + colorEnvelope.getHexCode());
        textView.setText("#" + String.format("%06X", (0xFFFFFF & colorEnvelope.getColor())));
//        view.setBackgroundColor(colorEnvelope.getColor());
        view.setPaintColor(colorEnvelope.getColor());
    }
}