package fr.bellev.stdatmosphere;

import java.util.List;
import java.util.Vector;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends FragmentActivity {

	private int[][] mPos = new int[4][3];
	private int[] iPos={0,0,0};
	private int Page = 0;
	private List<EditFragment> edits = new Vector<EditFragment>();
	private List<ResultFragment> fragments = new Vector<ResultFragment>();
	// tables to link the UI to the content of the variables

	// Ids of available array to attach to spinners
	private static int[] unitsIds = new int[] {R.array.TempUnits, R.array.AltUnits, R.array.MachUnits, R.array.PressureUnits, R.array.SpeedUnits, R.array.DensityUnits, R.array.ViscUnits, R.array.ReynoldsUnits};
	private static int[][] unitfields=new int[][] {
		{R.string.TstdLabel,R.string.AbsTempLabel,R.string.TtlLabel},
		{R.string.vsonLabel,R.string.TASLabel,R.string.CASLabel},
		{R.string.PambLabel,R.string.PtlLabel,R.string.PdynLabel},
		{R.string.rhoLabel,R.string.ViscLabel,R.string.ReyLabel}};
	
	// Correspondence between values and units
	private static int[][] valUnits = new int[][] {{0,0,0},{4,4,4},{3,3,3},{5,6,7},{0,1,2}};
	
	private static double[][] convUnits = new double[][] {
			{1,1/1.8,288.15}, /* Temperatures K, R, To */
			{1,0.3048,1000},  /* Length m, ft, km */
			{1}, /* Mach numbers */
			{1, 6895, 101325, 100000, 100}, /* Pressures Pa, Psia, atm, bar, mbar */
			{1,0.277777778,0.446944444,0.514444444,0.00508, 340.3}, /* Speeds m/s, km/h, mph, kt, ft/min, a0*/
			{1,16.0185,1.22500747}, /* Density kg/m3, lb/ft3, rho0 */
			{0.000001,1}, /* Viscosity Pl.10e6, Poises  */
			{1e6,1,3.280839895e6,3.280839895}};  /* Reynolds 1e6/m, 1/m, 1e6/ft, 1/ft */
	
	private MyPagerAdapter mPagerAdapter;


	private double [] ival={0,0,0};
	private double[][] vals = {{288.15,288.15,288.15},{340.2975,0.,0.},{101325,101325,0.},{1.2250,17.8447,0.}};

	public double getValue(int i, int j){
    	return vals[i][j];
    }
	public int getPos(int i, int j){
    	return mPos[i][j];
    }   
	public void setPos(int i, int j, int pos){
    	mPos[i][j]=pos;
    }   

    public double getIValue(int i) {
    	return ival[i];
    }
	public int getIPos(int i){
    	return iPos[i];
    }   
	public void setIPos(int i,int pos){
    	iPos[i]=pos;
    }   
    public void setValue(int i, int j, double v){
    	vals[i][j]=v;
    }
    
    public void setValues(int i, double[] v) {
    	vals[i]=v;
    }
    public void setIValue(int i, double v) {
    	ival[i]=v;
    }
    public void setIValues(double [] v) {
    	ival=v;
    }
   
	public void Compute() {
		
        double DISA=ival[0];
        double ALTP=ival[1];
        double MACH=ival[2];

   		double TSTD;
		double PAMB;
	    if (ALTP <= 11000.) { 
	    	TSTD=288.15-0.0065*ALTP;
	    	PAMB = 101325 * Math.pow((1 - (ALTP / 44330.78)), 5.25587611);
	    } else if (ALTP <= 20000.) { 
	    	TSTD=216.65; 
	    	PAMB = 22632 * Math.pow(2.718281 , (1.7345725 - 0.0001576883 * ALTP));
	    } else if (ALTP <= 32000.) {
	    	TSTD=216.65+0.001*(ALTP-20000.); 
	    	PAMB = Math.pow((0.7055184555 + ALTP * 0.000003587686018), (-34.16322));
	    } else if (ALTP<=47000.) { 
	    	TSTD=228.65+0.0028*(ALTP-32000.);
	    	PAMB = Math.pow((0.3492686141 + ALTP * 0.000007033096869),(-12.20115));
	    } else { 
	    	TSTD=270.65;
	    	PAMB = 41828.42421 * Math.exp(ALTP * (-0.00012622656));
	    }
	    double TABS=TSTD+DISA;
	    double TTTL = TABS * (1+0.2*MACH*MACH);
	    double PTTL = PAMB * Math.pow((1+0.2*MACH*MACH),3.5);
	    double PDYN = 0.7 * PAMB * MACH*MACH;
	    double v_son= 20.047*Math.sqrt(TABS);
	    double VTAS=MACH*v_son;
	    double REY = 47898.89*PAMB*MACH*(TABS+110.4)/(TABS*TABS);
	    double RHO = PAMB/TABS*0.0034837;
	    double VISC = 0.000001454 * (Math.pow(TABS, 1.5) / (110.4 + TABS));
	    double VCAS = 661.485 * 0.5144 * Math.sqrt(5 * (Math.pow((PAMB / 101325 * (Math.pow((1 + 0.2 * MACH * MACH) , 3.5) - 1) + 1) ,(1 / 3.5)) - 1));
	    vals[0][0] = TSTD;
	    vals[0][1] = TABS;
	    vals[0][2] = TTTL;
	    vals[1][0] = v_son;
	    vals[1][1] = VTAS;
	    vals[1][2] = VCAS;
	    vals[2][0] = PAMB;
	    vals[2][1] = PTTL;
	    vals[2][2] = PDYN;
	    vals[3][0] = RHO;
	    vals[3][1] = VISC;
	    vals[3][2] = REY;
        
    }
	
    private static String  makeFragmentName(int viewId, int index) {
    	        return "android:switcher:" + viewId + ":" + index;
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("MainActivity","Started");
        super.onCreate(savedInstanceState);
    	if (savedInstanceState != null) {
    		Page = savedInstanceState.getInt("Page");
    		setIValues(savedInstanceState.getDoubleArray("ival"));
    		iPos = savedInstanceState.getIntArray("iPos");
    		setValues(0, savedInstanceState.getDoubleArray("tab1"));
    		setValues(1, savedInstanceState.getDoubleArray("tab2"));
    		setValues(2, savedInstanceState.getDoubleArray("tab3"));
    		setValues(3, savedInstanceState.getDoubleArray("tab4"));
    		mPos[0]=savedInstanceState.getIntArray("pos1");
    		mPos[1]=savedInstanceState.getIntArray("pos2");
    		mPos[2]=savedInstanceState.getIntArray("pos3");
    		mPos[3]=savedInstanceState.getIntArray("pos4");
    	} else {
        	SharedPreferences settings = getSharedPreferences("StdAtmosphere",MODE_PRIVATE);
        	if (settings!=null) {
        		Page=settings.getInt("Page",Page);
        		for (int i=0;i<3;i++) {
        			ival[i]=(double) settings.getFloat("ival"+i,(float)ival[i]);
        			iPos[i]=settings.getInt("iPos"+i, iPos[i]);
        		}
        		for (int i=0;i<4;i++){
        			for (int j=0;j<3;j++){
        				vals[i][j]=(double) settings.getFloat("val"+i+j, (float)vals[i][j]);
        				mPos[i][j]=settings.getInt("pos"+i+j, mPos[i][j]);
        			}
        		}
        	}
    	}
        int[] Ids = {R.id.Line01,R.id.Line02,R.id.Line03};
        
        setContentView(R.layout.activity_main);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Resources res = getResources();
		String[] elabel = {res.getString(R.string.TempLabel),res.getString(R.string.AltLabel),res.getString(R.string.MachLabel)};
	    edits.clear();	    
	    if (savedInstanceState != null) {
	    	for (int i=0;i<3;i++) {
	    		EditFragment vf = (EditFragment) fm.findFragmentByTag("edit"+i);
	    		edits.add(vf);
	    	}
	    } else {
			for(int i=0;i<3;i++) {
				EditFragment vf = EditFragment.newInstance(elabel[i],convUnits[i],ival[i],unitsIds[i],iPos[i],i);
				View mframe= findViewById(Ids[i]);
				ft.add(mframe.getId(), vf,"edit"+i);
				edits.add(vf);
				Log.d("MainActivity","fragment created and added");
			}
			ft.commit();
	    }
        
		String[] Tabs = res.getStringArray(R.array.Tabs);
		fragments.clear();
	    if (savedInstanceState != null) {
	    	for(int i=0;i<4;i++){
	    		ResultFragment RF=(ResultFragment) fm.findFragmentByTag(makeFragmentName(R.id.viewpager,i));
	    		fragments.add(RF);
	    		RF.update();
	    	}
	    } else {
	    	for(int i=0;i<4;i++){
	    		String[] label = {res.getString(unitfields[i][0]),res.getString(unitfields[i][1]),res.getString(unitfields[i][2])};
	    		ResultFragment RF=ResultFragment.newInstance(Tabs[i], label, valUnits[i], i);
	    		if (RF==null) {
	    			Log.d("intialiseViewPager","Null ResultFragment");
	    		}
	    		fragments.add(RF);
	    		RF.update();
	    	}
		}


		// Création de l'adapter qui s'occupera de l'affichage de la liste de
		// Fragments
		mPagerAdapter = new MyPagerAdapter(fm, fragments);
		Log.d("MainActivity","mPagerAdapter created");
		ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
		// Affectation de l'adapter au ViewPager
		pager.setAdapter(mPagerAdapter);
		pager.setOffscreenPageLimit (4);
		pager.setCurrentItem(Page);
		Log.d("MainActivity","mPagerAdapter set.");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showHelp() {
    	help_dialog newFragment = new help_dialog();
    	newFragment.show(getSupportFragmentManager(), "help");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
      // Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted.
      Log.d("MainActivity","onSaveInstanceState called.");
      for (int i=0;i<edits.size();i++) {
    	  iPos[i]=edits.get(i).GetPos();
      }
      for (int i=0;i<fragments.size();i++) {
    	  mPos[i]=fragments.get(i).GetPoss();
      }
      ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
      Page=pager.getCurrentItem();
      savedInstanceState.putInt("Page", Page);
      savedInstanceState.putDoubleArray("ival", ival);
      savedInstanceState.putIntArray("iPos", iPos);
      savedInstanceState.putDoubleArray("tab1", vals[0]);
      savedInstanceState.putDoubleArray("tab2", vals[1]);
      savedInstanceState.putDoubleArray("tab3", vals[2]);
      savedInstanceState.putDoubleArray("tab4", vals[3]);
      savedInstanceState.putIntArray("pos1", mPos[0]);
      savedInstanceState.putIntArray("pos2", mPos[1]);
      savedInstanceState.putIntArray("pos3", mPos[2]);
      savedInstanceState.putIntArray("pos4", mPos[3]);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	Log.d("MainActivity","onRestoreInstanceState called.");
    	if (savedInstanceState != null) {
    		Page = savedInstanceState.getInt("Page");
    		setIValues(savedInstanceState.getDoubleArray("ival"));
    		iPos = savedInstanceState.getIntArray("iPos");
    		setValues(0, savedInstanceState.getDoubleArray("tab1"));
    		setValues(1, savedInstanceState.getDoubleArray("tab2"));
    		setValues(2, savedInstanceState.getDoubleArray("tab3"));
    		setValues(3, savedInstanceState.getDoubleArray("tab4"));
    		mPos[0]=savedInstanceState.getIntArray("pos1");
    		mPos[1]=savedInstanceState.getIntArray("pos2");
    		mPos[2]=savedInstanceState.getIntArray("pos3");
    		mPos[3]=savedInstanceState.getIntArray("pos4");
    	} else {
    		Log.d("MainActivity","onRestoreInstanceState: no state available");
    	}
    	if (mPagerAdapter!=null) {
    		mPagerAdapter.update();
    	}
    }

    public void click(android.view.View view) {
    	// Compute the values
    	this.Compute();
    	// broadcast update to all fragments
    	this.update();
    }
    public void update() {
    	if (mPagerAdapter!=null) {
    		mPagerAdapter.update();
    	}
    }
    @Override
    public void onPause(){
    	super.onPause();
    }
    @Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getSharedPreferences("StdAtmosphere",MODE_PRIVATE);
      SharedPreferences.Editor editor = settings.edit();
      editor.putInt("Page", Page);
      for (int i=0;i<3;i++) {
    	  editor.putFloat("ival"+i, (float)ival[i]);
    	  editor.putInt("iPos"+i, iPos[i]);
      }
      for (int i=0;i<4;i++){
    	  for (int j=0;j<3;j++){
    		  editor.putFloat("val"+i+j, (float)vals[i][j]);
    		  editor.putInt("pos"+i+j, mPos[i][j]);
    	  }
      }
      // Commit the edits!
      editor.commit();
    }
    @Override
    protected void onStart(){
    	super.onStart();

    	SharedPreferences settings = getSharedPreferences("StdAtmosphere",MODE_PRIVATE);
    	if (settings!=null) {
    	  
    		Page=settings.getInt("Page",Page);
    		for (int i=0;i<3;i++) {
    			ival[i]=(double) settings.getFloat("ival"+i,(float)ival[i]);
    			iPos[i]=settings.getInt("iPos"+i, iPos[i]);
    		}
    		for (int i=0;i<4;i++){
    			for (int j=0;j<3;j++){
    				vals[i][j]=(double) settings.getFloat("val"+i+j, (float)vals[i][j]);
    				mPos[i][j]=settings.getInt("pos"+i+j, mPos[i][j]);
    			}
    		}
    	}
    }
}
