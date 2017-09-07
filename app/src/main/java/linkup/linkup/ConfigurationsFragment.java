package linkup.linkup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import linkup.linkup.helper.CropCircleTransformation;


public class ConfigurationsFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configurations, container, false);
        ImageView profileImageView = (ImageView) view.findViewById(R.id.profileImage);
        Picasso.with(getContext()).load(R.drawable.p2).fit().centerCrop().transform(new CropCircleTransformation()).into(profileImageView);
        return view;
    }
}
