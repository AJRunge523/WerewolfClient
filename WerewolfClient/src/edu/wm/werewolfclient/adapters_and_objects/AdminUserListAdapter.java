package edu.wm.werewolfclient.adapters_and_objects;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import edu.wm.werewolfclient.R;

public class AdminUserListAdapter extends ArrayAdapter<ListUser>{

	private final Context context;
	private final List<ListUser> values;
 
	public AdminUserListAdapter(Context context, List<ListUser> values) {
		super(context, R.layout.kill_target_layout, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		final View rowView = inflater.inflate(R.layout.admin_user_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.admin_user_username);
		textView.setText(values.get(position).getUserName());
		TextView textView2 = (TextView) rowView.findViewById(R.id.admin_user_playing);
		textView2.setText((values.get(position).isPlaying() ? "In Game" : "Not In Game"));
		
		CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.isPlaying);
		
		checkBox.setChecked(values.get(position).isChecked());
        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                ListUser user = (ListUser) cb.getTag();
                user.setChecked(cb.isChecked());
                Toast.makeText(
                        context,
                        "Clicked on Checkbox: " + cb.getText() + " is "
                                + cb.isChecked(), Toast.LENGTH_LONG)
                        .show();
            }

        });
		

        checkBox.setTag(values.get(position));
		
 
		return rowView;
	}
}

