package com.example.stocker;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StockArrayAdapter extends ArrayAdapter<StockData>{
	
	private int resource;
	private Context context;
	
	public StockArrayAdapter(Context context, int resource,
			List<StockData> objects) {
		super(context, resource, objects);
		context = this.context;
		resource = this.resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_custom, parent, false);
		TextView symbol = (TextView) rowView.findViewById(R.id.row_symbol);
		TextView price = (TextView) rowView.findViewById(R.id.price_row);
		TextView change = (TextView) rowView.findViewById(R.id.change_row);
		TextView changePercent = (TextView) rowView.findViewById(R.id.changePercent_row);
		final TextView stopLose = (TextView) rowView.findViewById(R.id.stopLose_row);
		final StockData stock = getItem(position);
		symbol.setText(stock.symbol);
		price.setText(stock.price);
		change.setText(stock.change); 
		changePercent.setText(stock.changePercent);
		stopLose.setText(stock.stopLose);
		if(stock.change.startsWith("-")){
			change.setTextColor(Color.RED);
			changePercent.setTextColor(Color.RED);
		}
		else{
			change.setTextColor(Color.GREEN);
			changePercent.setTextColor(Color.GREEN);
		}
		stopLose.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
				alert.setTitle("Setting");
				alert.setMessage("set the price you would like to receive notification");
				final EditText input = new EditText(getContext());
				alert.setView(input);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						stopLose.setText(input.getText().toString());
						stock.stopLose = input.getText().toString();
					}
				});
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				alert.show();
			}
			
		});
		return rowView;
	}

}
