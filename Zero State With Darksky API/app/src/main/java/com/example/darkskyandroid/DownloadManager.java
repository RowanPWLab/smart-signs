package com.example.darkskyandroid;

/**
        * Created by Nick on 10/20/2015.
        */
public interface DownloadManager {
    void onDownloadSuccess(String response);
    void onDownloadFailed(String response);
    void onNotDownloaded();
}