package edu.wm.werewolfclient.adapters_and_objects;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import edu.wm.werewolfclient.R;
import edu.wm.werewolfclient.UserActivity;

public class KillTargetListAdapter extends ArrayAdapter<PlayerTarget>{

	private final Context context;
	private final UserActivity activity;
	private final List<PlayerTarget> values;
 
	public KillTargetListAdapter(Context context, List<PlayerTarget> values) {
		super(context, R.layout.kill_target_layout, values);
		this.context = context;
		this.values = values;
		this.activity = (UserActivity) context;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.kill_target_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.targetName);
		textView.setText(values.get(position).getPlayerName());
		
		TextView distance = (TextView) rowView.findViewById(R.id.distanceText);
		CheckBox playerCheck = (CheckBox) rowView.findViewById(R.id.targetCheckBox);
		if(values.get(position).getDistance()==0)
		{
			distance.setTextColor(Color.RED);
			distance.setText("Can Kill!");
			playerCheck.setEnabled(true);
		}
		else if(values.get(position).getDistance()==1)
		{
			distance.setTextColor(Color.YELLOW);
			distance.setText("Nearby");
			playerCheck.setEnabled(false);
		}
		else
		{
			distance.setTextColor(Color.WHITE);
			distance.setText("Far");
			playerCheck.setEnabled(false);
		}
		
		
		playerCheck.setChecked(values.get(position).isChecked());
        playerCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                PlayerTarget pt = (PlayerTarget) cb.getTag();
                pt.setChecked(cb.isChecked());
                activity.setSelectedTarget(pt, cb);
            }

        });
		
        playerCheck.setTag(values.get(position));
 
		return rowView;
	}
}

