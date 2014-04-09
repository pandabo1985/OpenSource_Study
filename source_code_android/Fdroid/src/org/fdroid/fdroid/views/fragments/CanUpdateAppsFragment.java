package org.fdroid.fdroid.views.fragments;

import android.net.Uri;
import org.fdroid.fdroid.R;
import org.fdroid.fdroid.data.AppProvider;
import org.fdroid.fdroid.views.AppListAdapter;
import org.fdroid.fdroid.views.CanUpdateAppListAdapter;

public class CanUpdateAppsFragment extends AppListFragment {

    @Override
    protected AppListAdapter getAppListAdapter() {
        return new CanUpdateAppListAdapter(getActivity(), null);
    }

    @Override
    protected String getFromTitle() {
        return getString(R.string.tab_updates);
    }

    @Override
    protected Uri getDataUri() {
        return AppProvider.getCanUpdateUri();
    }

}
