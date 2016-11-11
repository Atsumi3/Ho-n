package info.nukoneko.android.ho_n.controller.common.view;import android.support.v7.widget.LinearLayoutManager;import android.support.v7.widget.RecyclerView;/** * Created by atsumi on 2016/10/23. */public abstract class NKEndlessScrollListener extends RecyclerView.OnScrollListener {    private int previousTotal = 0;    private boolean loading = true;    private int current_page = 1;    private int visibleThreshold = 10;    private LinearLayoutManager mLinearLayoutManager;    public NKEndlessScrollListener(LinearLayoutManager linearLayoutManager) {        this.mLinearLayoutManager = linearLayoutManager;    }    @Override    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {        super.onScrolled(recyclerView, dx, dy);        int visibleItemCount = recyclerView.getChildCount();        int totalItemCount = mLinearLayoutManager.getItemCount();        int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();        if (loading) {            if (totalItemCount > previousTotal) {                loading = false;                previousTotal = totalItemCount;            }        }        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {            current_page++;            onLoadMore(current_page);            loading = true;        }    }    public abstract void onLoadMore(int current_page);}