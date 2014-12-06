package com.elmeripoikolainen.habifier;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HactivityAdapter extends ArrayAdapter<Hactivity> {

    private final List<Hactivity> list;
    private final Activity context;
    boolean checkAll_flag = false;
    boolean checkItem_flag = false;

    public HactivityAdapter(Activity context, List<Hactivity> list) {
        super(context, R.layout.hactivity_row, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            Log.d("Viewholder created", "test");
            LayoutInflater inflator = context.getLayoutInflater();
            convertView = inflator.inflate(R.layout.hactivity_row, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.hactivity_label);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.hactivity_check);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                //Here,  change the value
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int check_count = 0;
                    for (Hactivity item : list){
                        if(item.isSelected())
                            check_count++;
                    }
                    if(check_count > 3 && buttonView.isChecked()){
                        buttonView.setChecked(false);
                        //Context toastContext = context;
                        CharSequence text = "Only 4 activities are allowed at the same time.";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    } else {
                        int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                        list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.hactivity_label, viewHolder.text);
            convertView.setTag(R.id.hactivity_check, viewHolder.checkbox);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkbox.setTag(position); // This line is important.

        Log.d("List size before error "  , Integer.toString( list.size()));
        Log.d("Position before error "  , Integer.toString(position ));
        Log.d("getposition resuult ",  Boolean.toString(list.get(position).isSelected()));
        Log.d("gethacktivity resuult ",  list.get(position).getHactivity());
        Log.d("viewholder is atm : ", viewHolder.text.toString());
        Log.d("viewholder text",  viewHolder.text.getText().toString());
        viewHolder.text.setText(list.get(position).getHactivity());
        viewHolder.checkbox.setChecked(list.get(position).isSelected());

        return convertView;
    }
}