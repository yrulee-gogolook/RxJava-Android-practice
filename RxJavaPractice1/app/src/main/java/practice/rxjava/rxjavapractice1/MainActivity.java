package practice.rxjava.rxjavapractice1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://data.taipei")
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.build();

		ApiEndpointInterface apiService =
				retrofit.create(ApiEndpointInterface.class);

		Observable<TotalResult> observable = apiService.getData("resourceAquire", "55ec6d6e-dc5c-4268-a725-d04cc262172b");
		//  return all station

		observable
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<TotalResult>() {
					@Override
					public void onCompleted() {
						Log.d("RESULT", "Result received.");
					}

					@Override
					public void onError(Throwable e) {
						Log.d("RESULT", "Error occurred: "+e.getMessage());

					}

					@Override
					public void onNext(TotalResult totalResult) {
						Log.d("RESULT", "===============");

						ArrayList<TotalResult.Result.StationType> results = totalResult.result.results;
						for(TotalResult.Result.StationType type : results){
							Log.d("RESULT", "_id: "+type._id);
							Log.d("RESULT", "Station: "+type.Station);
							Log.d("RESULT", "Destination: "+type.Destination);
							Log.d("RESULT", "UpdateTime: "+type.UpdateTime);
							Log.d("RESULT", "---------------");
						}

						Log.d("RESULT", "===============");
					}
				});
		
	}

	public interface ApiEndpointInterface {
		//http://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=55ec6d6e-dc5c-4268-a725-d04cc262172b

		@GET("/opendata/datalist/apiAccess")
		Observable<TotalResult> getData(@Query("scope") String scope, @Query("rid") String rid);
	}

	public class TotalResult {
		public Result result;

		public class Result {
			int offset;
			int limit;
			int count;
			String sort;
			ArrayList<StationType> results = new ArrayList<>();

			public class StationType {
				String _id;
				String Station;
				String Destination;
				String UpdateTime;
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
