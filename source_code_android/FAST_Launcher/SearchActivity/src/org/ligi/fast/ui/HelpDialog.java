package org.ligi.fast.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import org.ligi.fast.R;

class HelpDialog {

    public static void show(Context ctx) {

        TextView tv = new TextView(ctx);
        tv.setPadding(10, 10, 10, 10);
        tv.setText(Html.fromHtml(ctx.getString(R.string.help_content)));

        Linkify.addLinks(tv, Linkify.ALL);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        new AlertDialog.Builder(ctx).setTitle(R.string.help_label).setView(tv)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
