package com.android.imagesearch.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.imagesearch.R;
import com.android.imagesearch.network.ImageSearchApiClient;
import com.android.imagesearch.network.model.ImageData;
import com.android.imagesearch.utils.ConnectionUtils;
import com.android.imagesearch.utils.DialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchImageFragment extends Fragment implements ImageListAdapter.OnItemClickListener {

    private RecyclerView mImageList;
    private EditText searchEt;
    private TextView mEmptyText;
    private ProgressBar mProgressBar;

    private List<ImageData> mImageDataList = new ArrayList<>();
    private ImageListAdapter mAdapter;

    public SearchImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageList = (RecyclerView) view.findViewById(R.id.frag_search_image_list);
        searchEt = (EditText) view.findViewById(R.id.frag_search_image_et);
        mEmptyText = (TextView) view.findViewById(R.id.emptyText);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        setGridLayoutManager(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE);
        mAdapter = new ImageListAdapter(getActivity(), mImageDataList);
        mImageList.setAdapter(mAdapter);

        if (ConnectionUtils.isNetworkConnected(getActivity())) {
            searchImages();
        } else {
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
		}
    }

    /**
     * Set listener for search images whenever user types a new search
     * term , an api called associated with that search term
     */
    private void searchImages() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchTerm = charSequence.toString().trim();
                // Starts search only when user type words more then 1.
                if (!TextUtils.isEmpty(searchTerm) && searchTerm.length() > 2) {
                    changeVisibility(View.GONE, View.VISIBLE);
                    getImageList(searchTerm);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * Get all images list from server as user types in search box.
     *
     * @param searchText
     */
    private void getImageList(String searchText) {

        ImageSearchApiClient.getImageSearchApi().getImageList(searchText, getWidthInPixels(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if(response != null) {
                    String jsonResponse = new String(((TypedByteArray) response.getBody()).getBytes());
                    // Reset list to show fresh data each time api called
                    mImageDataList.clear();
                    parseJsonResponse(jsonResponse);
                    setImageListAdapter();
                    changeVisibility(View.GONE, View.GONE);
                } else {
                    Toast.makeText(getActivity(), R.string.empty_text_string, Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                changeVisibility(View.VISIBLE, View.GONE);
            }
        });
    }

    /**
     * Sets GridLayoutManager based on orientation
     * For Portrait spanCount is 2 and for landscape spanCount is 3
     * @param isPortrait
     */
    private void setGridLayoutManager(boolean isPortrait) {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), isPortrait ? 2 : 3);
        mImageList.setLayoutManager(mGridLayoutManager);
    }

    /**
     * Set Adapter for recycler list of images after api hit.
     */
    private void setImageListAdapter() {
        if(!mImageDataList.isEmpty()) {
            mAdapter = new ImageListAdapter(getActivity(), mImageDataList);
            mImageList.setAdapter(mAdapter);
            mAdapter.setItemClickListener(this);
            mAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(), R.string.empty_text_string, Toast.LENGTH_SHORT).show();
		}
    }

    /**
     * Change visibility of empty text and progress bar.
     *
     * @param progressVisibility
     * @param textVisibility
     */
    private void changeVisibility(int progressVisibility, int textVisibility) {
        mEmptyText.setVisibility(progressVisibility);
        mProgressBar.setVisibility(textVisibility);
    }

    /**
     * Parse json response returned from server.
     * @param response
     */
    private void parseJsonResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.has("query")) {
                JSONObject query = obj.getJSONObject("query");
                JSONObject pagesObj = query.getJSONObject("pages");
                Iterator<String> iter = pagesObj.keys();

                while (iter.hasNext()) {
                    String key = iter.next();
                    ImageData imageData = new ImageData();
                    JSONObject value = (JSONObject) pagesObj.get(key);
                    imageData.setTitle(value.getString("title"));
                    if (value.has("thumbnail")) {
                        JSONObject thumbnail = value.getJSONObject("thumbnail");
                        imageData.setUrl(thumbnail.getString("source"));
                    }
                    mImageDataList.add(imageData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Device width in pixels
     * @return
     */
    private String getWidthInPixels() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return String.valueOf(metrics.widthPixels);
    }

    @Override
    public void onItemClick(View v, int position) {
        DialogUtils.showImage(getActivity(), (String) v.getTag());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        setGridLayoutManager(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE);
    }
}
