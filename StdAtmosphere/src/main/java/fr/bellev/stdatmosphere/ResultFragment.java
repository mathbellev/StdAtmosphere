package fr.bellev.stdatmosphere;

import java.util.List;
import java.util.Vector;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class ResultFragment extends Fragment {
	private String[] Labels;
	private int [] Units;
	private String Title;
	private int page;
	private View mView;
	
	private List<ValueFragment> vfs = new Vector<ValueFragment>();
	
	private static int[] unitsIds = new int[] {R.array.TempUnits, R.array.AltUnits, R.array.MachUnits, R.array.PressureUnits, R.array.SpeedUnits, R.array.DensityUnits, R.array.ViscUnits, R.array.ReynoldsUnits};

	private static double[][] convUnits = new double[][] {
		{1,1./1.8,288.15}, /* Temperatures K, R, To */
		{1,0.3048,1000},  /* Length m, ft, km */
		{1}, /* Mach numbers */
		{1, 6895, 101325, 100000, 100}, /* Pressures Pa, Psia, atm, bar, mbar */
		{1,0.277777778, 0.446944444, 0.514444444, 0.3048, 0.00508, 340.3}, /* Speeds m/s, km/h, mph, kt, ft/s, ft/min, a0*/
		{1,16.0185,1.22500747}, /* Density kg/m3, lb/ft3, rho0 */
		{0.000001,1}, /* Viscosity Pl.10e6, Poises  */
		{1e6,1,3.280839895e-6,3.280839895}};  /* Reynolds 1e6/m, 1/m, 1e6/ft, 1/ft */



	// Returns a valid id that isn't in use
	private static int id=-1;
	private int findId(){
	    View v = getActivity().findViewById(id);  
	    while (v != null){  
	        v = getActivity().findViewById(++id);  
	    }  
	    return id++;  
	}
	
	public static ResultFragment newInstance(String Title, String[] label, int[] units, int page) {
		ResultFragment myFragment = new ResultFragment();
	    Bundle args = new Bundle();
	    args.putString("Title", Title);
	    args.putStringArray("label", label);
	    args.putIntArray("units", units);
	    args.putInt("page",page);
	    
	    myFragment.page=page;
	    myFragment.Units=units;
	    myFragment.Title=Title;
	    myFragment.Labels=label;
	    
	    myFragment.setArguments(args);
		Log.d("Result Fragment","New instance "+Title+"Created.");
		return myFragment;
	}
	public String GetTitle() {
		Log.d("ResultFragment "+this.Title,"GetTitle called="+this.Title);
		return this.Title;
	}
	public int[] GetPoss() {
		Log.d("ResultFragment "+this.Title,"GetPoss called");
		int [] mPos = {0,0,0};
		for (int i=0;i<Math.min(vfs.size(), 3);i++) {
			mPos[i]=this.vfs.get(i).GetPos();
			Log.d("ResultFragment "+this.Title,"GetPoss "+i+"="+mPos[i]);
		}

		return mPos;
	}
	public void SetValues(double[] values) {
		Log.d("ResultFragment "+this.Title,"SetValues called");
		for (int i=0;i<Math.min(vfs.size(), 3);i++) {
			this.vfs.get(i).SetValue(values[i]);
		}
	}
	public double[] GetValues() {
		Log.d("ResultFragment "+this.Title,"GetValues called");
		double [] mValues={0,0,0};
		for (int i=0;i<Math.min(vfs.size(), 3);i++) {
			mValues[i]=this.vfs.get(i).GetValue();
		}
		return mValues;
	}
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments()!=null) {
			this.Labels = getArguments().getStringArray("label");
			this.Units = getArguments().getIntArray("units");
			this.Title = getArguments().getString("Title");
			this.page=getArguments().getInt("page");
			
			if (this.vfs.size()!=3) {
				this.vfs.clear();
				ValueFragment vf;
				for(int i=0;i<3;i++) {
					vf = ValueFragment.newInstance(this.Labels[i],convUnits[this.Units[i]],-1,-1,this.page,i);
					vf.setView(this.mView);
					this.vfs.add(vf);
				}
			}
		}
		Log.d("ResultFragment "+this.Title,"onCreate called");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("ResultFragment "+this.Title,"onCreateView called");
		this.mView = inflater.inflate(R.layout.resultfragment, container, false);
		
		String [] labelsId={"Label1","Label2","Label3"};
		String [] ValId={"Val1","Val2","Val3"};
		String [] SpinId={"Unit1","Unit2","Unit3"};

		ValueFragment vf;
		for(int i=0;i<3;i++) {
			if (i<this.vfs.size()) {
				vf = this.vfs.get(i);
				vf.setView(mView);
				TextView labelview=(TextView) mView.findViewWithTag(labelsId[i]);
				if (labelview!=null) {
					labelview.setId(findId());
					labelview.setText(this.Labels[i]);
				} else {
					Log.d("ResultFragment "+this.Title,"labelview "+labelsId[i]+" not found !");
				}
				
				TextView Valueview=(TextView) mView.findViewWithTag(ValId[i]);
				Valueview.setId(findId());				
				vf.setValId(Valueview.getId());
				
				Spinner UnitSpinner=(Spinner) mView.findViewWithTag(SpinId[i]);
				UnitSpinner.setId(findId());
				vf.setSpinId(UnitSpinner.getId());
				
				ArrayAdapter<CharSequence> SAdapter = ArrayAdapter.createFromResource(getActivity(),unitsIds[this.Units[i]], android.R.layout.simple_spinner_item);
				SAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				UnitSpinner.setAdapter(SAdapter);
				UnitSpinner.setOnItemSelectedListener(vf);
				UnitSpinner.setSelection(((MainActivity)getActivity()).getPos(this.page, i),true);
				
				vf.SetValue(((MainActivity)getActivity()).getValue(this.page, i));
				//vf.setPos(mPos[i]);
			} else {
				Log.d("ResultFragment "+this.Title,"line "+i+" has not been instanciated !");
			}
		}
		return mView;
	}

	
	public void update() {
		for(int i=0;i<Math.min(3,this.vfs.size());i++) {
			if (this.vfs.get(i)!=null) {
				this.vfs.get(i).setView(this.mView);
				this.vfs.get(i).SetValue(((MainActivity)getActivity()).getValue(this.page,i));
				this.vfs.get(i).setPos(((MainActivity)getActivity()).getPos(this.page,i));
			}
		}
	}
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      // Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted.
    	for (int i=0;i<vfs.size();i++) {
    		((MainActivity)getActivity()).setPos(page,i,vfs.get(i).GetPos());
    		((MainActivity)getActivity()).setValue(page, i, vfs.get(i).GetValue());
    	}
    	super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onPause() {
      // Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted.
    	for (int i=0;i<vfs.size();i++) {
    		((MainActivity)getActivity()).setPos(this.page, i, this.vfs.get(i).GetPos());
    		((MainActivity)getActivity()).setValue(this.page, i, this.vfs.get(i).GetValue());
    	}
    	super.onPause();
    }
 
}
