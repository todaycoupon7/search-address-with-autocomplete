package com.sample.todaycoupon7.addresssearch;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.sample.todaycoupon7.addresssearch.dto.AddressDto;
import com.sample.todaycoupon7.addresssearch.dto.AutocompleteDto;
import com.sample.todaycoupon7.addresssearch.dto.DocumentDto;
import com.sample.todaycoupon7.addresssearch.dto.ResultsDto;
import com.sample.todaycoupon7.addresssearch.dto.RoadAddressDto;
import com.sample.todaycoupon7.addresssearch.network.DataManager;
import com.sample.todaycoupon7.addresssearch.network.OnRequestAutocompleteListener;
import com.sample.todaycoupon7.addresssearch.network.OnRequestSearchListener;
import com.sample.todaycoupon7.addresssearch.support.AnimRecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText mEtInput;
    private RecyclerView mRvAutocomplete;
    private AutocompleteListAdapter mAutocompleteListAdapter;
    private AnimRecyclerView mArvResults;
    private LinearLayoutManager mLayoutManager;
    private ResultListAdapter mResultListAdapter;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mSearching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEtInput = findViewById(R.id.etInput);
        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!mSearching) {
                    if(mRvAutocomplete.getVisibility() != View.VISIBLE) {
                        mRvAutocomplete.setVisibility(View.VISIBLE);
                    }
                    clearResults();
                    requestAutocomplete(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mRvAutocomplete = findViewById(R.id.rvAutocomplete);
        mRvAutocomplete.setHasFixedSize(true);
        mRvAutocomplete.setLayoutManager(new LinearLayoutManager(this));
        mAutocompleteListAdapter = new AutocompleteListAdapter();
        mRvAutocomplete.setAdapter(mAutocompleteListAdapter);

        mArvResults = findViewById(R.id.arvResults);
        mArvResults.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mArvResults.setLayoutManager(mLayoutManager);
        mArvResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 검색 결과가 더 있는 경우엔 스크롤 맨아래로 내랴가면 검색 요청함.
                if(dy > 0) { //check for scroll down
                    final int visibleItemCount = mLayoutManager.getChildCount();
                    final int totalItemCount = mLayoutManager.getItemCount();
                    final int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                    if (totalItemCount > 0 &&
                            firstVisibleItem + visibleItemCount >= totalItemCount) {
                        if (!mResultsEnded) { // 다음에 불러올게 있다면 요청한다.
                            if(!mSearching) {
                                search(mEtInput.getText().toString(), true);
                            }
                        }
                    }
                }
            }
        });
        mResultListAdapter = new ResultListAdapter();
        mArvResults.setAdapter(mResultListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAutocompleteRunnable != null) {
            mAutocompleteRunnable.cancel();
            mHandler.removeCallbacks(mAutocompleteRunnable);
            mAutocompleteRunnable = null;
        }
        if(!TextUtils.isEmpty(mReqSearch)) {
            DataManager.getInstance().cancelRequest(mReqSearch);
            mReqSearch = null;
        }
    }

    public void onClicked(View v) {
        if(v.getId() == R.id.btnSearch) {
            if(!mSearching) {
                search(mEtInput.getText().toString(), false);
            }
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEtInput.getWindowToken(), 0);
    }

    /***************************************
     *
     * 자동완성
     *
     ***************************************/
    private AutocompleteRunnable mAutocompleteRunnable;

    /**
     * 자동완성 목록 요청
     * @param keyword
     */
    private void requestAutocomplete(String keyword) {
        // 이전 요청이 완료되기 전에 새로운 요청이 오는 경우, 이전 요청을 취소함
        if(mAutocompleteRunnable != null) {
            mAutocompleteRunnable.cancel();
            mHandler.removeCallbacks(mAutocompleteRunnable);
            mAutocompleteRunnable = null;
        }
        if(TextUtils.isEmpty(keyword)) {
            mAutocompleteListAdapter.clear();
        } else {    // 자동완성 요청 키워드가 있는 경우에만 요청함.
            mAutocompleteRunnable = new AutocompleteRunnable(keyword);
            mHandler.post(mAutocompleteRunnable);
        }
    }

    /**
     * 텍스트 입력에 따른 자동완성 요청 Runnable
     */
    private class AutocompleteRunnable implements Runnable {

        private String keyword;
        private String reqAutocomplete;

        public AutocompleteRunnable(String keyword) {
            this.keyword = keyword;
        }

        public void cancel() {
            if(!TextUtils.isEmpty(reqAutocomplete)) {
                DataManager.getInstance().cancelRequest(reqAutocomplete);
                reqAutocomplete = null;
            }
        }

        @Override
        public void run() {
            reqAutocomplete = DataManager.getInstance().requestAutocomplete(keyword,
                    new OnRequestAutocompleteListener() {
                        @Override
                        public void onCompleted(AutocompleteDto dto) {
                            reqAutocomplete = null;
                            if(dto != null) {
                                if(mEtInput.getText().toString().equals(dto.q)) {
                                    mAutocompleteListAdapter.setKeyword(dto.items);
                                    mAutocompleteListAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            reqAutocomplete = null;
                        }
                    });
        }
    }

    private class AutocompleteListAdapter extends RecyclerView.Adapter<AutocompleteListAdapter.ViewHolder> {

        private ArrayList<String> mKeywords;

        public void setKeyword(ArrayList<String> keywords) {
            mKeywords = keywords;
        }

        public void clear() {
            mKeywords = null;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_autocomplete, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.onBind(mKeywords.get(position));
        }

        @Override
        public int getItemCount() {
            return mKeywords == null ? 0 : mKeywords.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView tvKeyword;
            private String keyword;

            public ViewHolder(View itemView) {
                super(itemView);
                tvKeyword = itemView.findViewById(R.id.tvKeyword);
                itemView.setOnClickListener(this);
            }

            public void onBind(String keyword) {
                this.keyword = keyword;
                tvKeyword.setText(this.keyword);
            }

            @Override
            public void onClick(View v) {
                if(!mSearching) {
                    search(keyword, false);
                }
            }
        }
    }

    /***************************************
     *
     * 주소 검색
     *
     ***************************************/
    private String mReqSearch;
    private boolean mResultsEnded;
    private int mPage = 1;

    /**
     * 검색 결과 목록을 초기화함
     */
    private void clearResults() {
        if(mResultListAdapter.getItemCount() > 0) {
            mResultListAdapter.clear();
            mResultListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 검색 요청을 함
     * @param keyword 검색 키워드
     * @param isNext 다음 페이지 요청 요부
     */
    private void search(String keyword, boolean isNext) {
        mSearching = true;
        if(!isNext) {
            // clear & hide autocomplete
            mAutocompleteListAdapter.clear();
            mRvAutocomplete.setVisibility(View.GONE);
            // set input editor
            mEtInput.setText(keyword);
            mEtInput.setSelection(mEtInput.getText().length());
            // init results list
            mArvResults.setAdapter(null);
            // hide keyboard
            hideKeyboard();
            mPage = 1;  // init
        } else {
            mPage++;
        }
        mReqSearch = DataManager.getInstance().requestSearch(keyword, mPage,
                new OnRequestSearchListener() {
                    @Override
                    public void onCompleted(ResultsDto dto) {
                        if(dto != null) {
                            if(dto.meta != null) {
                                mResultsEnded = dto.meta.is_end;
                            } else {
                                mResultsEnded = true;
                            }
                            mResultListAdapter.addResults(dto.documents);
                            if(mArvResults.getAdapter() == null) {
                                mArvResults.setAdapter(mResultListAdapter);
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mArvResults.scheduleLayoutAnimation();
                                }
                            } else {
                                mResultListAdapter.notifyDataSetChanged();
                            }
                        }
                        mReqSearch = null;
                        mSearching = false;
                    }

                    @Override
                    public void onError() {
                        mReqSearch = null;
                        mSearching = false;
                    }
                });
    }

    private class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ViewHolder> {

        private ArrayList<DocumentDto> mResults = new ArrayList<>();

        public void addResults(ArrayList<DocumentDto> results) {
            mResults.addAll(results);
        }

        public void clear() {
            mResults.clear();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_result, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.onBind(mResults.get(position));
        }

        @Override
        public int getItemCount() {
            return mResults.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvZipcode;
            private TextView tvAddress;
            private TextView tvRoadAddress;

            public ViewHolder(View itemView) {
                super(itemView);
                tvZipcode = itemView.findViewById(R.id.tvZipcode);
                tvAddress = itemView.findViewById(R.id.tvAddress);
                tvRoadAddress = itemView.findViewById(R.id.tvRoadAddress);
            }

            public void onBind(DocumentDto document) {
                final RoadAddressDto roadAddr = document.road_address;
                if(roadAddr != null) {
                    tvZipcode.setText("[우편번호] " + roadAddr.zone_no);
                    StringBuilder addrBuilder = new StringBuilder(roadAddr.address_name);
                    if(!TextUtils.isEmpty(roadAddr.building_name)) {
                        addrBuilder.append(" (").append(roadAddr.building_name).append(")");
                    }
                    tvRoadAddress.setText("[도로명] " + addrBuilder.toString());
                }
                final AddressDto addr = document.address;
                if(addr != null) {
                    if(TextUtils.isEmpty(tvZipcode.getText().toString())) {
                        tvZipcode.setText("[우편번호] " + addr.zip_code);
                    }
                    tvAddress.setText("[지번] " + addr.address_name);
                }
            }
        }
    }
}
