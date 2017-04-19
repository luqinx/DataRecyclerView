package chao.app.refreshrecyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author chao.qin
 * @since 2017/4/11
 */

public class MainFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment,container,false);
        view.findViewById(R.id.load_advance).setOnClickListener(this);
        view.findViewById(R.id.load_multi_cell).setOnClickListener(this);
        view.findViewById(R.id.load_empty).setOnClickListener(this);
        view.findViewById(R.id.load_error).setOnClickListener(this);
        view.findViewById(R.id.load_forbidden).setOnClickListener(this);
        view.findViewById(R.id.load_manual).setOnClickListener(this);
        view.findViewById(R.id.load_standard).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_advance:
                AdvanceModeFragment.show(getActivity());
                break;
            case R.id.load_multi_cell:
                MultiCellModeFragment.show(getActivity());
                break;
            case R.id.load_empty:
                EmptyModeFragment.show(getActivity());
                break;
            case R.id.load_standard:
                StandardModeFragment.show(getActivity());
                break;
        }
    }
}
