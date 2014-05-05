package com.aldominium.colorbudget.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class AddPaymentActivity extends Activity {
    protected int mSelectedYear;
    protected int mSelectedMonth;
    protected int mSelectedDay;
    private Intent mCallerIntent;
    protected EditText mDateInput;
    protected EditText mAmmountInput;
    protected EditText mNameInput;
    protected Button mEditarFecha;
    protected Button mAddPayment;
    protected float ammount;
    static final int DATE_DIALOG_ID = 0;

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mSelectedYear = year;
                    mSelectedMonth = monthOfYear;
                    mSelectedDay = dayOfMonth;
                    updateDisplay();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_add_payment);
        mCallerIntent = getIntent();
        mSelectedDay = mCallerIntent.getIntExtra("day",0);
        mSelectedMonth = mCallerIntent.getIntExtra("month",0);
        mSelectedYear = mCallerIntent.getIntExtra("year",0);
        mEditarFecha = (Button)findViewById(R.id.editarFecha);
        mAddPayment = (Button)findViewById(R.id.buttonAddPayment);
        mAmmountInput = (EditText)findViewById(R.id.amountField);
        mNameInput = (EditText)findViewById(R.id.nameField);


        /*Toast.makeText(getBaseContext(), "Selected Date is\n\n"
                        + mSelectedDay + " : " + (mSelectedMonth) + " : " + mSelectedYear,
                Toast.LENGTH_LONG
        ).show();
*/
        mDateInput = (EditText)findViewById(R.id.dateField);
        updateDisplay();

        //Si van a editar la fecha
        mEditarFecha.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                showDialog(DATE_DIALOG_ID);

            }
        });

        //Añade el pago
        mAddPayment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                //Salvar el pago
                //Volver a la actividad principal
                //ammount = Integer.parseInt(mAmmountInput.getText().toString());
                setProgressBarIndeterminateVisibility(true);
                try{
                    savePayment();
                }catch (Exception e){
                    e.printStackTrace();
                }

                setProgressBarIndeterminateVisibility(false);
                AddPaymentActivity.this.setResult(RESULT_OK, getIntent().putExtra("texto3", "aldo"));

                finish();

            }
        });




    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mSelectedYear, mSelectedMonth, mSelectedDay);
        }
        return null;
    }


    // updates the date in the TextView
    private void updateDisplay() {
        mDateInput.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mSelectedMonth).append("-")
                        .append(mSelectedDay).append("-")
                        .append(mSelectedYear).append(" ")
        );
    }

    private void savePayment(){
        ParseObject message = new ParseObject(ParseConstants.CLASS_PAYMENTS);
        message.put(ParseConstants.KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_AMMOUNT, Double.parseDouble(mAmmountInput.getText().toString().trim()));
        message.put(ParseConstants.KEY_DAY, mSelectedDay);
        message.put(ParseConstants.KEY_MONTH, mSelectedMonth);
        message.put(ParseConstants.KEY_YEAR, mSelectedYear);
        message.put(ParseConstants.KEY_NAME, mNameInput.getText().toString());

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // success!
                    Toast.makeText(AddPaymentActivity.this, "Exito", Toast.LENGTH_LONG).show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPaymentActivity.this);
                    builder.setMessage("Error al guardar mensaje")
                            .setTitle("Error")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_payment, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
