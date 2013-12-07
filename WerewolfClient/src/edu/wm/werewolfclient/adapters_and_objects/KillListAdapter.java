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

public class KillListAdapter extends ArrayAdapter<Kill>{

		private final Context context;
		private final List<Kill> values;
	 
		public KillListAdapter(Context context, List<Kill> values) {
			super(context, R.layout.kill_target_layout, values);
			this.context = context;
			this.values = values;
		}
	 
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			final View rowView = inflater.inflate(R.layout.kill_list_kill, parent, false);
			TextView playerName = (TextView) rowView.findViewById(R.id.killVictim);
			playerName.setText(values.get(position).getVictimID());
			playerName.setTextColor(values.get(position).getColor());
			
			ImageView playerImage = (ImageView) rowView.findViewById(R.id.killImage);
			playerImage.setImageDrawable(values.get(position).getKillImage());
			
			ImageView roleImage = (ImageView) rowView.findViewById(R.id.victimRoleImage);
			roleImage.setImageDrawable(values.get(position).getVictimImage());
			
			TextView killTime = (TextView) rowView.findViewById(R.id.killTime);

			long m = values.get(position).getDate();
			String last = "";
			if(m / 1000 / 60 / 60 / 24 > 0)
				last = (m / 1000 / 60 / 60 / 24) + " days ago";
			else if(m / 1000 / 60 / 60 > 0)
			{
				last = (m / 1000 / 60 / 60) + " hours ago";
			}
			else if(m / 1000 / 60 > 0)
			{
				last = (m / 1000 / 60) + " minutes ago";
			}
			else
			{
				last = (m / 1000) + " seconds ago";
			}
			killTime.setText(last);
			killTime.setTextColor(values.get(position).getColor());
			
			
	 
			return rowView;
		}
	}