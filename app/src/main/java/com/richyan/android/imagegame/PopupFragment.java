package com.richyan.android.imagegame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;


public class PopupFragment extends DialogFragment{
    private int mPopupId;
    private Bitmap bitmap;
    private int COL;
    private int imgId;
    private boolean showNumber;
    public PopupFragment() {
        // Required empty public constructor
    }
    public void setmPopupId(int mPopupId){this.mPopupId=mPopupId;}
    public int getmPopupId(){return mPopupId;}
    public void setImage(Bitmap bitmap){this.bitmap = bitmap;}
    public void setCOL(int COL){this.COL = COL;}
    public void setImgId(int imgId){this.imgId = imgId;}
    public void setShowNumber(boolean showNumber){this.showNumber = showNumber;}
    public int getCOL(){return COL;}
    public int getImgId(){return imgId;}
    public boolean isShowNumber(){return showNumber;}
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Dialog dialog;
        if(getmPopupId()==1){
            View view = inflater.inflate(R.layout.settings,null);
            final RadioGroup pictRG = (RadioGroup)view.findViewById(R.id.pictRG);
            final RadioGroup formatRG = (RadioGroup)view.findViewById(R.id.formatRG);
            final CheckBox numberCB = (CheckBox)view.findViewById(R.id.numberCB);
            builder.setView(view)
                    // Add action buttons
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                           PopupFragment.this.getDialog().cancel();
                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            switch(pictRG.getCheckedRadioButtonId()){
                                case R.id.budaRadio:
                                    setImgId(R.drawable.buda);
                                    break;
                                case R.id.ziyiRadio:
                                    setImgId(R.drawable.ziyi);
                                    break;
                                case R.id.monaRadio:
                                    setImgId(R.drawable.mona);
                                    break;
                                default:
                                    break;
                            }
                            switch (formatRG.getCheckedRadioButtonId()){
                                case R.id.fourRadio:
                                    setCOL(4);
                                    break;
                                case R.id.threeRadio:
                                    setCOL(3);
                                    break;
                                default:
                                    break;
                            }
                            setShowNumber(numberCB.isChecked());
                            Intent intent = new Intent(getContext(),GameActivity.class);
                            intent.putExtra("imgId",getImgId());
                            intent.putExtra("COL", getCOL());
                            intent.putExtra("showNumber", isShowNumber());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            PopupFragment.this.dismiss();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
            //dialog = builder.create();
        }else if(getmPopupId()==2){
            View view = inflater.inflate(R.layout.fragment_popup, null);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageIV);
            imageView.setImageBitmap(bitmap);

            builder.setView(view).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    PopupFragment.this.dismiss();
                }
            });
        }else if(getmPopupId()==3){
            builder.setView(inflater.inflate(R.layout.about, null))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            PopupFragment.this.dismiss();
                        }
                    });
        }

        dialog = builder.create();
        return dialog;
    }
}
