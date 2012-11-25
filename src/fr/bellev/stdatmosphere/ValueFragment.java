package fr.bellev.stdatmosphere;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

public class ValueFragment implements OnItemSelectedListener {
	private double mFactors[];
	private double mValue;
	private View mView;
	private int page;
	private int row;
	private int SpinId;
	private int ValId;

	public static ValueFragment newInstance(String label, double[] units, int SpinId, int ValId, int page, int row) {
		Log.d("ValueFragment","newInstance called.");
		ValueFragment myFragment = new ValueFragment();
		myFragment.SpinId=SpinId;
	    myFragment.mFactors=units;
	    myFragment.ValId=ValId;
	    myFragment.page=page;
	    myFragment.row=row;
		return myFragment;
	}

	private String format(double value){		
		DecimalFormat DF;
		try {
			DF = (DecimalFormat)NumberFormat.getInstance();
		}
		catch(Exception e) {
			DF = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		}
		DF.applyPattern("@########");
		if (Math.abs(value)<1e-4) {
			DF.applyPattern("@#####E0");
		}
		String text=DF.format(value);
		return text;
	}
	public void SetValue(double value) {
		TextView field;
		Spinner unitSpinner;
		int pos;
		
		this.mValue=value;
		
		if (mView!=null) {
			unitSpinner = (Spinner) mView.findViewById(this.SpinId);
			if (unitSpinner!=null) {
				pos=unitSpinner.getSelectedItemPosition();
			} else {
				pos=0;
				Log.d("ValueFragment ["+page+"]["+row+"]","SetValue: unitSpinner is null !");
			}
			field = (TextView) mView.findViewById(this.ValId);
			double factor=1;
			if (pos>-1) {
				factor=this.mFactors[pos];
			}
			String text = this.format(value/factor);
			if (field!=null) {
				field.setText(text);
			} else {
				Log.d("ValueFragment ["+page+"]["+row+"]","SetValue: field is null !");
			}

		} else {
			Log.d("ValueFragment ["+page+"]["+row+"]","SetValue: mView is null !");
		}
		
		return;
	}
	
	public double GetValue() {
		return this.mValue;
	}
	
	public void setSpinId(int SpinId){
		this.SpinId=SpinId;
	}
	public void setView(View v){
		this.mView=v;
	}
	public View getView(){
		return this.mView;
	}

	public void setValId(int ValId){
		this.ValId=ValId;
	}
	
	public int GetPos() {
		if (mView!=null) {
			Spinner Spin = (Spinner) mView.findViewById(this.SpinId);
			if (Spin==null) {
				return 0;
			} else {
				return Spin.getSelectedItemPosition();
			}
		}
		return 0;
	}
	
	public void setPos(int position) {
		if (mView!=null) {
			Spinner Spin = (Spinner) mView.findViewById(this.SpinId);
			if (Spin!=null) {
				Spin.setSelection(position, true);
				TextView field = (TextView) mView.findViewById(this.ValId);
				double factor=1;
				if (position>-1) {
					factor=this.mFactors[position];
				}
				NumberFormat DF = new DecimalFormat("#0.0000");
				String text = DF.format(this.mValue/factor); 
				if (field!=null) {
					field.setText(text);
				}
			}
		}
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		TextView field;
		double factor=1;
		field = (TextView) mView.findViewById(this.ValId);
		MainActivity host = (MainActivity) view.getContext();
		if (host!=null) {
			host.setPos(this.page, this.row, pos);
		}
		if (pos>-1) {
			factor=mFactors[pos];
		}
		String text = this.format(this.mValue/factor);
		if (field!=null) {
			field.setText(text);
		} else {
			Log.d("ValueFragment ["+page+"]["+row+"]","Could not find the value field on change unit !");
			Log.d("ValueFragment ["+page+"]["+row+"]","Id is "+this.ValId+".");
		}
	}


	public void onNothingSelected(AdapterView<?> parent) {
		// Do Nothing
	}
}
