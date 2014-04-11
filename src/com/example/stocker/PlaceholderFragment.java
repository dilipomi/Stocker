package com.example.stocker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PlaceholderFragment extends ListFragment {

	private ArrayList<String> stockList;
	private ArrayAdapter<String> stockAdapter;
	private AutoCompleteTextView autoTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		stockList = new ArrayList<String>();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		autoTextView = (AutoCompleteTextView) rootView
				.findViewById(R.id.autoCompleteTextView1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.list_item, StockStorage.STOCKS);
		autoTextView.setAdapter(adapter);
		autoTextView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				updateView(arg0.getAdapter().getItem(arg2));
			}

			private void updateView(Object item) {
				new getStockQuote().execute((String) item);
				// stockList.add(stockQuote);
				// stockAdapter.notifyDataSetChanged();
			}

		});
		return rootView;
	}

	private class getStockQuote extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... stockRequests) {
			int count = stockRequests.length;
			for (int i = 0; i < count; i++) {
				String stock = stockRequests[i];
				String symbol = stock.substring(0, stock.indexOf("\t"));
				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(
						"http://finance.google.com/finance/info?client=ig&q="
								+ symbol);
				StringBuilder builder = new StringBuilder();
				try {
					HttpResponse response = client.execute(httpGet);
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String content = builder.toString();
				String content2 = content.substring(content.indexOf("//") + 3);
				try {
					JSONArray jsonArray = new JSONArray(content2);
					JSONObject jsonObject = jsonArray.getJSONObject(0);
					return jsonObject.getString("t") + "                "
							+ jsonObject.getString("l_cur") + "               "
							+ jsonObject.getString("cp") + "%";
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!stockList.contains(result)) {
				stockList.add(result);
			}
			stockAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		stockAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.stock_row, stockList);
		setListAdapter(stockAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Do you want to remove " + stockList.get(position)
				+ "?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				stockList.remove(position);
				stockAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		builder.show();
	}

}
