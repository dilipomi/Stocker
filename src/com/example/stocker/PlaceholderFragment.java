package com.example.stocker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Type;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class PlaceholderFragment extends ListFragment {

	private ArrayList<StockData> stockList;
	private StockArrayAdapter stockAdapter;
	private AutoCompleteTextView autoTextView;
	private HashSet<String> record;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		stockList = new ArrayList<StockData>();
		record = new HashSet<String>();
		callAsynchronousTask();
		setRetainInstance(true);
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
				autoTextView.setText("");
			}

		});
		return rootView;
	}

	private void callAsynchronousTask() {
		final Handler handler = new Handler();
		Timer timer = new Timer();
		TimerTask doAsyncTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						String[] symbolArray = new String[stockList.size()];
						for (int i = 0; i < stockList.size(); i++) {
							symbolArray[i] = stockList.get(i).symbol;
						}
						new getStockQuote().execute(symbolArray);

					}
				});
			}
		};
		timer.schedule(doAsyncTask, 0, 10000);
	}

	private class getStockQuote extends
			AsyncTask<String, Void, ArrayList<StockData>> {

		@Override
		protected ArrayList<StockData> doInBackground(String... stockRequests) {
			int count = stockRequests.length;
			ArrayList<StockData> result = new ArrayList<StockData>();
			for (int i = 0; i < count; i++) {
				String stock = stockRequests[i];
				if (stock.contains("\t"))
					stock = stock.substring(0, stock.indexOf("\t"));
				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(
						"http://finance.google.com/finance/info?client=ig&q="
								+ stock);
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
					e.printStackTrace();
				}
				String content = builder.toString();
				String content2 = content.substring(content.indexOf("//") + 3);
				try {
					JSONArray jsonArray = new JSONArray(content2);
					JSONObject jsonObject = jsonArray.getJSONObject(0);
					StockData stockData = new StockData(
							jsonObject.getString("t"),
							jsonObject.getString("l_cur"),
							jsonObject.getString("c"),
							jsonObject.getString("cp"), "N/A");
					result.add(stockData);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(ArrayList<StockData> resultList) {
			super.onPostExecute(resultList);
			for (StockData result : resultList) {
				if (!record.contains(result.symbol)) {
					stockList.add(result);
					record.add(result.symbol);
					stockAdapter.notifyDataSetChanged();
				} else {
					for (StockData s : stockList) {
						if (s.symbol.equals(result.symbol)) {
							s.price = result.price;
							s.change = result.change;
							s.changePercent = result.changePercent;
							stockAdapter.notifyDataSetChanged();
							if (!s.stopLose.equals("N/A")) {
								double priceDouble = Double
										.parseDouble(s.price);
								double stockLostDouble = Double
										.parseDouble(s.stopLose);
								if (priceDouble <= stockLostDouble) {
									sendNotification(s.symbol);
								}
							}
							break;
						}
					}
				}
			}
		}

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences.Editor editor = this.getActivity().getPreferences(0)
				.edit();
		String stockListJsonString = new Gson().toJson(stockList);
		editor.putString("stockList", stockListJsonString);
		editor.commit();
	}

	@SuppressLint("NewApi")
	public void sendNotification(String symbol) {
		Context context = getActivity();
		Context context2 = getActivity().getApplicationContext();
		Intent notificationIntent = new Intent(getActivity().getApplicationContext(),
				MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(),
				0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		Notification.Builder notificationBuilder = new Notification.Builder(
				getActivity().getApplicationContext())
				.setTicker(symbol + "is below the stopLose you set")
				.setAutoCancel(true).setContentTitle("Notification!")
				.setContentText(symbol + "is below the stopLose you set")
				.setContentIntent(pendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getActivity()
				.getSystemService(getActivity().NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, notificationBuilder.build());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		stockAdapter = new StockArrayAdapter(getActivity(),
				R.layout.row_custom, stockList);
		updateStockList();
		setListAdapter(stockAdapter);
	}

	private void updateStockList() {
		String stockListJsonString = this.getActivity().getPreferences(0)
				.getString("stockList", null);
		if (stockListJsonString != null) {
			java.lang.reflect.Type type = new TypeToken<ArrayList<StockData>>() {
			}.getType();
			ArrayList<StockData> temp = new Gson().fromJson(
					stockListJsonString, type);
			for (StockData s : temp) {
				if (!record.contains(s.symbol)) {
					stockList.add(s);
					record.add(s.symbol);
				}
			}
			stockAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Do you want to remove "
				+ stockList.get(position).symbol + "?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				record.remove(stockList.get(position).symbol);
				stockList.remove(position);
				stockAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.show();
	}

}
