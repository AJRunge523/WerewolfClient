package edu.wm.werewolfclient.adapters_and_objects;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.wm.werewolfclient.R;

public class AdminPlayerListAdapter extends ArrayAdapter<ListPlayer>{

	private final Context context;
	private final List<ListPlayer> values;
 
	public AdminPlayerListAdapter(Context context, List<ListPlayer> values) {
		super(context, R.layout.kill_target_layout, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		final View rowView = inflater.inflate(R.layout.admin_player_layout, parent, false);
		TextView playerName = (TextView) rowView.findViewById(R.id.admin_playerName);
		playerName.setText(values.get(position).getUserName());
		TextView status = (TextView) rowView.findViewById(R.id.admin_playerStatus);
		status.setText((values.get(position).isAlive() ? "Dead" : "Alive"));
		ImageView playerImage = (ImageView) rowView.findViewById(R.id.admin_playerImage);
		playerImage.setImageDrawable(values.get(position).getPlayerImage());
		TextView lastUpdate = (TextView) rowView.findViewById(R.id.admin_playerLastUpdate);
		long m = values.get(position).getLastUpdate();
		String last = "";
		if(m < 0)
		{
			last = "Never";
			lastUpdate.setTextColor(Color.RED);
		}
		else if(m / 1000 / 60 / 60 > 0)
		{
			last = (m / 1000 / 60 / 60) + " hours ago";
			lastUpdate.setTextColor(Color.RED);
		}
		else if(m / 1000 / 60 > 0)
		{
			last = (m / 1000 / 60) + " minutes ago";
			if(m / 1000 / 60 > 30)
				lastUpdate.setTextColor(Color.YELLOW);
			else
				lastUpdate.setTextColor(Color.GREEN);
		}
		else
		{
			last = (m / 1000) + " seconds ago";
			lastUpdate.setTextColor(Color.GREEN);
		}
		lastUpdate.setText(last);
		
		CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.admin_playerCheckBox);
		
		checkBox.setChecked(values.get(position).isChecked());
        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                ListPlayer user = (ListPlayer) cb.getTag();
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
