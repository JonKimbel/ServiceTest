package com.jonkimbel.servicetest.ui;

import android.content.res.Resources;
import android.text.Annotation;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.style.BulletSpan;

class Formatter {
    private static final String LIST_KEY = "list";
    private static final String LIST_VALUE_BULLET = "bullet";
    private static final int BULLET_GAP_WIDTH_DP = 4;

    static SpannableString formatString(Resources res, int stringId) {
        CharSequence stringText = res.getText(stringId);
        SpannedString spannedText = null;
        Annotation[] annotations = {};
        if (stringText instanceof SpannedString) {
            spannedText = (SpannedString) stringText;
            annotations = spannedText.getSpans(0, stringText.length(), Annotation.class);
        }

        SpannableString spannableString = new SpannableString(stringText);
        for (Annotation annotation : annotations) {
            if (annotation.getKey().equals(LIST_KEY)) {
                String listStyle = annotation.getValue();
                if (listStyle.equals(LIST_VALUE_BULLET)) {
                    spannableString.setSpan(new BulletSpan(dpToPx(res, BULLET_GAP_WIDTH_DP)),
                            spannedText.getSpanStart(annotation),
                            spannedText.getSpanEnd(annotation),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        return spannableString;
    }

    private static int dpToPx(Resources res, @SuppressWarnings("SameParameterValue") int dpValue) {
        return (int) (dpValue * res.getDisplayMetrics().density + 0.5f);
    }
}
