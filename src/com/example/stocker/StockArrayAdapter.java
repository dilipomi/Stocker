package com.example.stocker;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
		StockData stock = getItem(position);
		symbol.setText(stock.symbol);
		price.setText(stock.price);
		change.setText(stock.change); 
		changePercent.setText(stock.changePercent);
		if(stock.change.startsWith("-")){
			change.setTextColor(Color.RED);
			changePercent.setTextColor(Color.RED);
		}
		else{
			change.setTextColor(Color.GREEN);
			changePercent.setTextColor(Color.GREEN);
		}
		return rowView;
	}

}
