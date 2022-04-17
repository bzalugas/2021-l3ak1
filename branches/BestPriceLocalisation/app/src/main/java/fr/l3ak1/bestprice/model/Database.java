package fr.l3ak1.bestprice.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Database
{
	public static final String URL_API = "https://bestprice-2021-l3ak1-api.herokuapp.com";
}
