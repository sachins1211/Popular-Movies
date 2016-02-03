package com.example.sachin.popularmovies.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.example.sachin.popularmovies.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class MoviesEntry implements BaseColumns {
        // table name

        public static final String SQLITE_TABLE = "movies";
        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(SQLITE_TABLE).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SQLITE_TABLE;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + SQLITE_TABLE;

        // for building URIs on insertion
        public static Uri buildFlavorsUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
