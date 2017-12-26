package com.zgy.translate.utils;

import android.view.View;

/**
 *
 */

public class OptItem {

    private String itemContent;
    private View.OnClickListener onClickListener;
    private int txColor;
    private float txSize;

    public OptItem(String itemContent, View.OnClickListener onClickListener) {
        this.itemContent = itemContent;
        this.onClickListener = onClickListener;
    }

    public OptItem(String itemContent, int txColor, View.OnClickListener onClickListener) {
        this.itemContent = itemContent;
        this.onClickListener = onClickListener;
        this.txColor = txColor;
    }

    public OptItem(String itemContent, float txSize, View.OnClickListener onClickListener) {
        this.itemContent = itemContent;
        this.onClickListener = onClickListener;
        this.txSize = txSize;
    }

    public OptItem(String itemContent, int txColor, float txSize, View.OnClickListener onClickListener) {
        this.itemContent = itemContent;
        this.onClickListener = onClickListener;
        this.txColor = txColor;
        this.txSize = txSize;
    }

    public String getItemContent() {
        return itemContent;
    }

    public void setItemContent(String itemContent) {
        this.itemContent = itemContent;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public int getTxColor() {
        return txColor;
    }

    public void setTxColor(int txColor) {
        this.txColor = txColor;
    }

    public float getTxSize() {
        return txSize;
    }

    public void setTxSize(float txSize) {
        this.txSize = txSize;
    }
}
