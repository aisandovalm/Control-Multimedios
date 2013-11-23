package cl.telematica.controlmultimedios;

import java.util.List;

import cl.telematica.controlmultimedios.adapters.RssAdapter;
import cl.telematica.controlmultimedios.connections.ConnectionManager;
import cl.telematica.controlmultimedios.interfaces.DownloadListener;
import cl.telematica.controlmultimedios.models.EarthQuakeDataModel;
import cl.telematica.controlmultimedios.parsers.RssParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity implements DownloadListener {
	
	private ProgressBar progressBar;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listView = (ListView) findViewById(R.id.listView1);
		
		new ConexionAsincronica().execute();
	}

	@Override
	public void onRequestStart() {
		if(progressBar.getVisibility() == View.GONE){
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onRequestComplete(String data) {
		if(progressBar.getVisibility() == View.VISIBLE){
			progressBar.setVisibility(View.GONE);
		}
		final List<EarthQuakeDataModel> list = RssParser.getDataList(data);
		
		RssAdapter adapter = new RssAdapter(getApplicationContext(), R.string.app_name, list);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long value) {
				Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
				intent.putExtra("link", list.get(position).link);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onError(String error, int code) {
		if(progressBar.getVisibility() == View.VISIBLE){
			progressBar.setVisibility(View.GONE);
		}
	}
	
	private void conexion()
	{
		new ConnectionManager(this, 10000, 10000, "GET")
		.execute(getString(R.string.url));
	}
	
	
	private class ConexionAsincronica extends AsyncTask<Void, Integer, Boolean> {
		 
	    @Override
	    protected Boolean doInBackground(Void... params) {
	 
	    	conexion();
	 
	        return true;
	    }
	 
	    /*@Override
	    protected void onProgressUpdate(Integer... values) {
	        int progreso = values[0].intValue();
	 
	        progressBar.setProgress(progreso);
	    }*/
	 
	    @Override
	    protected void onPreExecute() {
	    	progressBar.setMax(100);
	    	progressBar.setProgress(0);
	    }
	 
	    @Override
	    protected void onPostExecute(Boolean result) {
	        if(result){
	            Toast.makeText(MainActivity.this, "Tarea finalizada!",
	                    Toast.LENGTH_SHORT).show();
	        }
	    }
	 
	    @Override
	    protected void onCancelled() {
	        Toast.makeText(MainActivity.this, "Tarea cancelada!",
	                Toast.LENGTH_SHORT).show();
	    }
	}
}
