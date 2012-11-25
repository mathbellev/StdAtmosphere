package fr.bellev.stdatmosphere;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import fr.bellev.stdatmosphere.MainActivity;

public class EditFragment extends Fragment implements OnItemSelectedListener,TextWatcher {
	private double mFactors[];
	private double mValue=0;
	private View mView;
	private boolean mBool=false;
	private int mPos=0;
	private int Id;
	
	public static EditFragment newInstance(String label, double[] units, double value, int SpinId, int pos, int Id) {
		EditFragment myFragment = new EditFragment();
	    Bundle args = new Bundle();
	    args.putString("label", label);
	    args.putDoubleArray("units", units);
	    args.putInt("SpinId", SpinId);
	    args.putInt("Id", Id);
	    args.putInt("pos", pos);
	    args.putDouble("value",value);
	    myFragment.mFactors=units;
	    myFragment.mPos=pos;
	    myFragment.Id=Id;
	    myFragment.mValue=value;
	    myFragment.setArguments(args);
		return myFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mView=inflater.inflate(R.layout.editfragment, container, false);
		this.update();
		if (getArguments()!=null) {
			String label = getArguments().getString("label");
			this.mFactors=getArguments().getDoubleArray("units");
            //this.mPos=getArguments().getInt("pos");
            this.Id=getArguments().getInt("Id");
            this.mPos=((MainActivity) getActivity()).getIPos(this.Id);
            //this.mValue=getArguments().getDouble("value");
            this.mValue=((MainActivity) getActivity()).getIValue(this.Id);
			int SpinId=getArguments().getInt("SpinId");
			TextView field = (TextView) mView.findViewById(R.id.Label);
			field.setText(label);
			Spinner Spin = (Spinner) mView.findViewById(R.id.Unit);
			ArrayAdapter<CharSequence> SAdapter = ArrayAdapter.createFromResource(getActivity(),SpinId, android.R.layout.simple_spinner_item);
			SAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Spin.setAdapter(SAdapter);
			Spin.setOnItemSelectedListener(this);
			Spin.setSelection(this.mPos,true);
			this.update();
			EditText efield = (EditText) this.mView.findViewById(R.id.Val);
			NumberFormat DF = NumberFormat.getInstance(Locale.ENGLISH);
        	((DecimalFormat)DF).applyPattern("#0.000");
        	String text = ((DecimalFormat)DF).format(this.mValue/mFactors[this.mPos]);
        	efield.setText(text);
			efield.addTextChangedListener(this);
		}
		return this.mView;
	}
	private String format(double value) {
		NumberFormat DF = NumberFormat.getInstance(Locale.ENGLISH);
		DF.setGroupingUsed(false);
		 if (DF instanceof DecimalFormat) {
		     ((DecimalFormat)DF).setDecimalSeparatorAlwaysShown(true);
		 }
    	//((DecimalFormat)DF).applyPattern("#0.000");
		String text=DF.format(value);
		return text;
	}
	public void update() {
		this.SetValue(((MainActivity) getActivity()).getIValue(this.Id));
	}
	
	public void SetValue(double value) {

		EditText field;
		Spinner unitSpinner;
		unitSpinner = (Spinner) this.mView.findViewById(R.id.Unit);
		field = (EditText) this.mView.findViewById(R.id.Val);
		this.mValue=value;
		((MainActivity) getActivity()).setIValue(Id,value);
		int pos=unitSpinner.getSelectedItemPosition();
        this.mPos=pos;
		double factor=1;
		if (pos>-1) {
			factor=mFactors[pos];
		}
		String text = this.format(value/factor); 
		mBool=true;
		field.setText(text);
		return;
	}
	public double GetValue() {
		NumberFormat DF = NumberFormat.getInstance(Locale.ENGLISH);
		EditText field;
		Spinner unitSpinner;

		unitSpinner = (Spinner) this.mView.findViewById(R.id.Unit);
		int pos=unitSpinner.getSelectedItemPosition();
		double factor=1;
		if (pos>-1) {
			factor=mFactors[pos];
			this.mPos=pos;
		}

		field = (EditText) this.mView.findViewById(R.id.Val);
		CharSequence text = (CharSequence) field.getText();
		ParsePosition PP = new ParsePosition(0);
		try {
			Number valueN = (Number) ((DecimalFormat)DF).parse(text.toString() ,PP);
			this.mValue=factor*valueN.doubleValue();
		} catch (Exception e) {
			
		}
		
		return this.mValue;
	}
	public int GetPos() {
		return this.mPos;
	}
	public void onResume () {
		super.onResume();
		this.update();
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

		TextView field;
		double factor=1;
		MainActivity host = (MainActivity) view.getContext();
		if (host!=null) {
			host.setIPos(this.Id, pos);
		}
		field = (TextView) this.mView.findViewById(R.id.Val);
		if (pos>-1) {
			factor=mFactors[pos];
            this.mPos=pos;
		}
		String text = this.format(this.mValue/factor);
		mBool=true;
		field.setText(text);
		Log.d("EditFragment","onItemSelected called, new value="+this.mPos);
	}


	public void onNothingSelected(AdapterView<?> parent) {
		// Do Nothing
	}

	@Override
	public void afterTextChanged(Editable text) {
		Log.d("EditFragment","afterTextChanged called.");
		if (!mBool) {
			NumberFormat DF = NumberFormat.getInstance(Locale.ENGLISH);
		
			Spinner unitSpinner;
		
			unitSpinner = (Spinner) this.mView.findViewById(R.id.Unit);
			int pos=unitSpinner.getSelectedItemPosition();

			double factor=1;
			if (pos>-1) {
				factor=mFactors[pos];
				this.mPos=pos;
			}
		
			ParsePosition PP = new ParsePosition(0);
			try {
				Number valueN = (Number) ((DecimalFormat)DF).parse(text.toString() ,PP);
				this.mValue=factor*valueN.doubleValue();
			} catch (Exception e) {
				
			}
			mBool=false;
			((MainActivity)getActivity()).setIValue(this.Id, this.mValue);
			((MainActivity)getActivity()).Compute();
			((MainActivity)getActivity()).update();
		}
		mBool=false;
	}		


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
}
