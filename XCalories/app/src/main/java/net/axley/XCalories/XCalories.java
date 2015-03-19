package net.axley.XCalories;

import java.text.DecimalFormat;

import android.R.string;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class XCalories extends Activity {
	private EditText InpAge;
	private EditText InpWeight;
	private Spinner WeightUnits;
	private EditText InpAverageHeartRate;
	private EditText InpDuration;
	private RadioGroup GenderSelection;
	private Button BtnCalculate;
	private Button BtnClosePU;
	private TextView LblCaloriesBurned;
	private Gender genderChecked;
    private Dialog results;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // find the page layout elements
        GenderSelection = (RadioGroup)findViewById(R.id.RadioGroup01);
        InpAge = (EditText)findViewById(R.id.InpAge);
        InpWeight = (EditText)findViewById(R.id.InpWeight);
        WeightUnits = (Spinner)findViewById(R.id.WeightUnits);
        InpAverageHeartRate = (EditText)findViewById(R.id.InpAverageHeartRate);
        InpDuration = (EditText)findViewById(R.id.InpDuration);
        BtnCalculate = (Button)findViewById(R.id.BtnCalculate);
        
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item,
                new String[] { "lbs", "kg" });
        WeightUnits.setAdapter(spinnerArrayAdapter);

        GenderSelection.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
            // When the radio button is checked, set the genderChecked enum
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                 RadioButton checked = (RadioButton)arg0.findViewById(checkedId);
                 genderChecked = Gender.valueOf(checked.getText().toString());
            } 
		}); 
        
        BtnCalculate.setOnClickListener(new CalculateListener(this));
    }
    
    /* Calculates calories burned per minute using these formulas:
     *    Men: C/min = (-59.3954 + (-36.3781 + 0.271 x age + 0.394 x weight + 0.404 x VO2max + 0.634 x HR))/4.184
     *    Women: C/min = (-59.3954 + (0.274 x age + 0.103 x weight + 0.380 x VO2max + 0.450 x HR)) / 4.184
     * Returns calories burned by multiplying by the exercise duration in minutes.
	 */
    private double DetermineCaloriesBurned(Gender sex, int averageHeartRate, int age, int weight, int minutes) {
    	// constants for the equation
    	double c1, c2, c3, c4, c5, c6;	// coefficients
    	final double divisor = 4.184;
    	final int VO2Max = 35;	// reasonable default;  TODO:  make this configurable or calculate based on age range.
    	double kgWeight;
    	if (WeightUnits.getSelectedItem().equals("lbs")) {
    		kgWeight = PoundsToKilograms(weight);
    	} else {
    		kgWeight = weight;
    	}
    	
    	if (sex.equals(Gender.Male)) {
    		c1 = -59.3954;
    		c2 = -36.3781;
    		c3 = 0.271;
    		c4 = 0.394;
    		c5 = 0.404;
    		c6 = 0.634;
    		return ((c1 + (c2 + (c3 * age) + (c4 * kgWeight) + (c5 * VO2Max) + (c6 * averageHeartRate))) / divisor) * minutes;
    	} else {
    		c1 = -59.3954;
    		c2 = 0.274;
    		c3 = 0.103;
    		c4 = 0.380;
    		c5 = 0.450;
    		return ((c1 + (c2 * age) + (c3 * kgWeight) + (c4 * VO2Max) + (c5 * averageHeartRate)) / divisor) * minutes;
    	}
    }    
    
    public double PoundsToKilograms(int pounds) {
    	return pounds * 0.45359237;
    }
    
    public double KilogramsToPounds(int kg) {
    	return kg / 0.45359237;
    }
    
    int roundTwoDecimals(double d) {
    	DecimalFormat intFmt = new DecimalFormat("#");
    	return Integer.valueOf(intFmt.format(d));
    }
    
    public static enum Gender {
    	Male,
    	Female
    }

protected class OKListener implements OnClickListener {

    private Dialog dialog;

    public OKListener(Dialog dialog) {
         this.dialog = dialog;
    }

    public void onClick(View v) {
         dialog.dismiss();
    }
}

protected class CalculateListener implements OnClickListener {
	private Activity activity;
	
	public CalculateListener (Activity activity) {
		this.activity = activity;
	}
	
	public void onClick(View v) {
		int ahr = 0, age = 0, weight = 0, duration = 0;
		boolean inputError = false;
		String errmsgs = "";
		
		if (genderChecked == null) {
			errmsgs += "You must select a gender\n";
			inputError = true;
		}
		
	    try
	    {
	      // the String to int conversion happens here
	       ahr = Integer.parseInt(InpAverageHeartRate.getText().toString().trim());
	    }
	    catch (NumberFormatException nfe)
	    {
	    	errmsgs += "Heart rate must be a number\n";
	    	inputError = true;
	    }

	    try
	    {
	      // the String to int conversion happens here
	       age = Integer.parseInt(InpAge.getText().toString().trim());
	    }
	    catch (NumberFormatException nfe)
	    {
	    	errmsgs += "Age must be a number\n";
	    	inputError = true;
	    }
	    
	    try
	    {
	      // the String to int conversion happens here
	       weight = Integer.parseInt(InpWeight.getText().toString().trim());
	    }
	    catch (NumberFormatException nfe)
	    {
	    	errmsgs += "Weight must be a number\n";
	    	inputError = true;
	    }
	    
	    try
	    {
	      // the String to int conversion happens here
	       duration = Integer.parseInt(InpDuration.getText().toString().trim());
	    }
	    catch (NumberFormatException nfe)
	    {
	    	errmsgs += "Duration must be a number\n";
	    	inputError = true;
	    }

	    if (inputError) {
	    	Toast.makeText(getBaseContext(), errmsgs, Toast.LENGTH_LONG).show();
	    } else {
	    	//results = new Dialog(getBaseContext());
	    	results = new Dialog(activity);
	    	
	    	results.setContentView(R.layout.results);
	    	results.setTitle("Calories Burned (Kcal):");
	    	results.show();
	    	
	        LblCaloriesBurned = (TextView)results.findViewById(R.id.LblCaloriesBurned);
	        BtnClosePU = (Button)results.findViewById(R.id.BtnClosePU);
	    	
	    	LblCaloriesBurned.setText(Double.toString(
	    			Math.round(
	    					DetermineCaloriesBurned(genderChecked, ahr, age, weight, duration)
	    			)
	    		)
    		);
	        
	        // Trap click and close the popup window.
	        BtnClosePU.setOnClickListener(new OKListener(results));
	    }
	}
}

}