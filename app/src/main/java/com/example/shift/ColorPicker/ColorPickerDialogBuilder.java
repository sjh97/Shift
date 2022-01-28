//package com.example.shift.ColorPicker;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.widget.RelativeLayout;
//
//import com.example.shift.R;
//import com.skydoves.colorpickerview.ColorEnvelope;
//import com.skydoves.colorpickerview.ColorPickerView;
//import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
//
//public class ColorPickerDialogBuilder {
//
//    private Activity activity;
//    private Context context;
//    private RelativeLayout layout;
//
//    public ColorPickerDialogBuilder(Context context, RelativeLayout layout) {
//        this.context = context;
//        this.layout = layout;
//        this.activity = (Activity) context;
//    }
//
//    public void setBackgroundColor(){
//        final MyColorPickerDialog.Builder builder = new MyColorPickerDialog.Builder(context,
//                android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
//        builder.setPreferenceName("MyColorPickerDialog")
//                .setPositiveButton("OK", new ColorEnvelopeListener() {
//                    @Override
//                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
//                        int getGridColor = builder.getColorGridColor();
//                        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getString(R.string.changeColorKey)
//                                , context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = preferences.edit();
//                        if(getGridColor == Color.parseColor("#000000")){
//                            editor.putInt(context.getApplicationContext().getString(R.string.backgroundColorKey), envelope.getColor());
//                            editor.putString(context.getApplicationContext().getString(R.string.color_or_image),
//                                    "color");
//                            editor.commit();
//                            layout.setBackgroundColor(envelope.getColor());
//                            activity.getWindow().setStatusBarColor(envelope.getColor());
//                        }
//                        else{
//                            editor.putInt(context.getApplicationContext().getString(R.string.backgroundColorKey), getGridColor);
//                            editor.putString(context.getApplicationContext().getString(R.string.color_or_image),
//                                    "color");
//                            editor.commit();
//                            layout.setBackgroundColor(getGridColor);
//                            activity.getWindow().setStatusBarColor(getGridColor);
//                        }
//                    }
//                })
//                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .attachAlphaSlideBar(false)
//                .attachBrightnessSlideBar(true);
//        ColorPickerView colorPickerView = builder.getColorPickerView();
//        colorPickerView.setFlagView(new CustomFlag(context, R.layout.custom_flag));
//        builder.show();
//    }
//
//    public void setTextColor(){
//        final MyColorPickerDialog.Builder builder = new MyColorPickerDialog.Builder(context,
//                android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
//        builder.setPreferenceName("MyColorPickerDialog")
//                .setPositiveButton("OK", new ColorEnvelopeListener() {
//                    @Override
//                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
//                        int getGridColor = builder.getColorGridColor();
//                        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getString(R.string.changeColorKey)
//                                , context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = preferences.edit();
//                        if(getGridColor == Color.parseColor("#000000")){
//                            editor.putInt(context.getApplicationContext().getString(R.string.textColorKey), envelope.getColor());
//                            editor.commit();
//                            ImageEditTextViewUpdater updater = new ImageEditTextViewUpdater(context);
//                            updater.updateColor(envelope.getColor());
//                        }
//                        else{
//                            editor.putInt(context.getApplicationContext().getString(R.string.textColorKey), getGridColor);
//                            editor.commit();
//                            ImageEditTextViewUpdater updater = new ImageEditTextViewUpdater(context);
//                            updater.updateColor(getGridColor);
//                        }
//                    }
//                })
//                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .attachAlphaSlideBar(false)
//                .attachBrightnessSlideBar(true);
//        ColorPickerView colorPickerView = builder.getColorPickerView();
//        colorPickerView.setFlagView(new CustomFlag(context, R.layout.custom_flag));
//        builder.show();
//    }
//
//}