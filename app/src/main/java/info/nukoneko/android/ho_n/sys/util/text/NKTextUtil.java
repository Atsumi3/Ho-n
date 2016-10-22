package info.nukoneko.android.ho_n.sys.util.text;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKTextUtil {
    private NKTextUtil() {
    }

    private final static String PATTERN_MAIL =
            "^[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+(\\.[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+)*+(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$";

    private final static String PATTERN_URL =
            "https?://[\\w/:%#\\$&\\?\\(\\)~\\.=\\+\\-]+";

    private final static String PATTERN_TAG =
            "[#＃][Ａ–Ｚａ-ｚA-Za-z一-鿆0-9０-９ぁ-ヶｦ-ﾟー_]+";

    /**
     * check format
     *
     * @param mail address
     * @return collect
     */
    public static boolean isMailAddress(String mail) {
        return mail.matches(PATTERN_MAIL);
    }

    /**
     * create link
     *
     * @param text     text
     * @param callback callback
     * @return text
     */
    public static SpannableString setLinkTag(@NonNull String text,
                                             @NonNull NKTextLinkCallback callback) {
        SpannableString builder = new SpannableString(text);

        // URL
        Pattern pattern = Pattern.compile(PATTERN_URL);
        final Matcher urlMatcher = pattern.matcher(text);
        while (urlMatcher.find()) {
            int start = urlMatcher.start();
            int end = urlMatcher.end();
            final String uri = urlMatcher.group(0);
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    callback.onClickUri(uri);
                }
            }, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        // TAG
        pattern = Pattern.compile(PATTERN_TAG);
        Matcher tagMatcher = pattern.matcher(text);
        while (tagMatcher.find()) {
            int start = tagMatcher.start();
            int end = tagMatcher.end();
            final String tag = tagMatcher.group(0).substring(1);
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    callback.onClickTag(tag);
                }
            }, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        return builder;
    }
}
