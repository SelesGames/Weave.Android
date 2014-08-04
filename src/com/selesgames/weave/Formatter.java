package com.selesgames.weave;

import android.content.Context;
import android.text.TextUtils;

import com.selesgames.weave.R;
import com.selesgames.weave.WeaveUtils;

public class Formatter {

    private static final String HEADER_TEMPLATE = "<h1 id=\"sg_title\">%s</h1><h2 id=\"sg_pubtime\">%s</h2>";

    private static final String HEADER_TEMPLATE_IMAGE = "<h2 id=\"sg_source\">%s</h2><div id=\"sg_herodiv\"><a href=\"%s\"><img id=\"sg_heroimage\" src=\"%s\"/></a></div><h1 id=\"sg_title\">%s</h1><h2 id=\"sg_pubtime\">%s</h2>";

    private String htmlTemplate;

    private String mSource;
    private String mTitle;
    private String mImage;
    private String mLink;
    private String mPublishDate;
    private String mForeground;
    private String mBackground;
    private String mFontName;
    private String mFontSize;
    private String mLinkColor;
    private String mPadding;
    private Context mContext;
    private int mTopMargin;

    public Formatter(Context context, String source, String title, String image, String link, String publishDate,
            String foreground, String background, String fontName, String fontSize, String linkColor, String padding) {
        mContext = context;
        mSource = source;
        mTitle = title;
        mImage = image;
        mLink = link;
        mPublishDate = publishDate;
        mForeground = foreground;
        mBackground = background;
        mFontName = fontName;
        mFontSize = fontSize;
        mLinkColor = linkColor;
        mPadding = padding;
    }

    public String createHtml(String body) {
        if (htmlTemplate == null) {
            htmlTemplate = WeaveUtils.readRawFile(mContext, R.raw.article_template);
        }

        String headerHtml;
        if (TextUtils.isEmpty(mImage)) {
            headerHtml = String.format(HEADER_TEMPLATE, mTitle, mSource);
        } else {
            headerHtml = String.format(HEADER_TEMPLATE_IMAGE, mSource, mImage, mImage, mTitle, mPublishDate);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(htmlTemplate.replace("[FOREGROUND]", mForeground).replace("[BACKGROUND]", mBackground)
                .replace("[FONT]", mFontName).replace("[FONTSIZE]", mFontSize).replace("[ACCENT]", mLinkColor)
                .replace("[PADDING]", mPadding).replace("[SOURCE]", mSource).replace("[TITLE]", mTitle)
                .replace("[HEADER]", headerHtml).replace("[LINK]", mLink).replace("[BODY]", body)
                .replace("[TOPMARGIN]", String.valueOf(mTopMargin) + "px"));
        return sb.toString();
    }
}
