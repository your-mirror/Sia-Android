package vandyke.siamobile.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import vandyke.siamobile.MainActivity;
import vandyke.siamobile.R;

public class HostingFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hosting, container, false);
        MainActivity.instance.getSupportActionBar().setTitle("Hosting");
        return v;
    }
}
