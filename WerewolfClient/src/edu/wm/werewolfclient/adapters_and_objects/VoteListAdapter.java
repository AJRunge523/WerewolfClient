package edu.wm.werewolfclient.adapters_and_objects;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import edu.wm.werewolfclient.R;
import edu.wm.werewolfclient.UserActivity;

public class VoteListAdapter extends ArrayAdapter<VoteListPlayer>{

	private final Context context;
	private final List<VoteListPlayer> values;
	private final UserActivity activity;
 
	public VoteListAdapter(Context context, List<VoteListPlayer> values) {
		super(context, R.layout.kill_target_layout, values);
		this.context = context;
		this.values = values;
		this.activity = (UserActivity) context;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		final View rowView = inflater.inflate(R.layout.vote_list_player, parent, false);
		TextView playerName = (TextView) rowView.findViewById(R.id.voteListTextView);
		playerName.setText(values.get(position).getUserID());
		
		ImageView playerImage = (ImageView) rowView.findViewById(R.id.voteListImage);
		playerImage.setImageDrawable(values.get(position).getPlayerImage());
				
		CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.voteListCheckBox);
		
		checkBox.setChecked(values.get(position).isChecked());
        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                VoteListPlayer user = (VoteListPlayer) cb.getTag();
                user.setChecked(cb.isChecked());
                activity.setSelectedVote(user, cb);
            }

        });
		

        checkBox.setTag(values.get(position));
		
 
		return rowView;
	}
}
