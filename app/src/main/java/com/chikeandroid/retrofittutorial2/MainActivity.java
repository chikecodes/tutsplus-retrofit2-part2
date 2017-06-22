package com.chikeandroid.retrofittutorial2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chikeandroid.retrofittutorial2.data.model.Post;
import com.chikeandroid.retrofittutorial2.data.remote.APIService;
import com.chikeandroid.retrofittutorial2.data.remote.ApiUtils;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mResponseTv;

    private APIService mAPIService;

    // RxJava
    @NonNull
    private CompositeDisposable mDisposables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText titleEt = (EditText) findViewById(R.id.et_title);
        final EditText bodyEt = (EditText) findViewById(R.id.et_body);
        Button submitBtn = (Button) findViewById(R.id.btn_submit);
        mResponseTv = (TextView) findViewById(R.id.tv_response);

        mAPIService = ApiUtils.getAPIService();

        // RxJava
        mDisposables = new CompositeDisposable();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEt.getText().toString().trim();
                String body = bodyEt.getText().toString().trim();
                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(body)) {
                    sendPost(title, body);
                }
            }
        });
    }

    public void showErrorMessage() {
        Toast.makeText(this, R.string.mssg_error_submitting_post, Toast.LENGTH_SHORT).show();
    }

    public void sendPost(String title, String body) {

        /*// RxJava
        mAPIService.savePost(title, body, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Post>() {
                    @Override
                    public void onNext(@NonNull Post post) {
                        showResponse(post.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

                // Make sure to dispose subscription in onDestroy()
                */

        mAPIService.savePost(title, body, 1).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if(response.isSuccessful()) {
                    showResponse(response.body().toString());
                    Log.i(TAG, "post submitted to API." + response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                showErrorMessage();
                Log.e(TAG, "Unable to submit post to API.");
            }
        });
    }

    public void updatePost(long id, String title, String body) {
        mAPIService.updatePost(id, title, body, 1).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if(response.isSuccessful()) {
                    showResponse(response.body().toString());
                    Log.i(TAG, "post updated." + response.body().toString());
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                showErrorMessage();
                Log.e(TAG, "Unable to update post.");
            }
        });
    }

   /*

   Example of cancelling a request

   private Call<Post> mCall;
    public sendPost(String title, String body) {
        mCall = mAPIService.savePost(title, body, 1);
        mCall.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if(response.isSuccessful()) {
                    showResponse(response.body().toString());
                    Log.i(TAG, "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                if(call.isCanceled()) {
                    Log.e(TAG, "request was aborted");
                }else {
                    Log.e(TAG, "Unable to submit post to API.");
                }
                showErrorMessage();

            }
        });
    }

    public void cancelRequest() {
        mCall.cancel();
    }*/

    public void showResponse(String response) {
        if(mResponseTv.getVisibility() == View.GONE) {
            mResponseTv.setVisibility(View.VISIBLE);
        }
        mResponseTv.setText(response);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // dispose to prevent memory leak
        mDisposables.dispose();
    }
}
