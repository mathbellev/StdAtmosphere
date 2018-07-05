package fr.bellev.stdatmosphere;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class AboutDialog extends Dialog {

    private static Context mContext = null;

    public AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about);
    }

}