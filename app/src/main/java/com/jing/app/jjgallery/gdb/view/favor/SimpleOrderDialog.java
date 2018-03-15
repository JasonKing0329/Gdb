package com.jing.app.jjgallery.gdb.view.favor;

import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jing.app.jjgallery.gdb.FavorPopupMvpActivity;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DraggableDialogFragmentV4;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.FavorRecordOrder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/15 10:56
 */
public class SimpleOrderDialog extends DraggableDialogFragmentV4 {

    private OrderFragment ftOrder;

    private long mRecordId;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        setTitle("Orders");
        requestCloseAction();
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        ftOrder = new OrderFragment();
        ftOrder.setRecordId(mRecordId);
        return ftOrder;
    }

    public void setRecordId(long mRecordId) {
        this.mRecordId = mRecordId;
    }

    public static class OrderFragment extends ContentFragmentV4 {

        @BindView(R.id.rv_orders)
        RecyclerView rvOrders;

        Unbinder unbinder;

        private SimpleRecordOrderAdapter orderAdapter;

        private long mRecordId;

        @Override
        protected int getLayoutRes() {
            return R.layout.dialog_record_orders;
        }

        @Override
        protected void initView(View view) {
            unbinder = ButterKnife.bind(this, view);
            GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
            rvOrders.setLayoutManager(manager);
        }

        @Override
        public void onResume() {
            super.onResume();
            loadOrders();
        }

        private void loadOrders() {
            FavorRecordDao dao = GdbApplication.getInstance().getDaoSession().getFavorRecordDao();
            List<FavorRecord> list = dao.queryBuilder()
                    .where(FavorRecordDao.Properties.RecordId.eq(mRecordId))
                    .build().list();

            List<FavorRecordOrder> orders = new ArrayList<>();
            for (FavorRecord relation:list) {
                orders.add(relation.getOrder());
            }

            orderAdapter = new SimpleRecordOrderAdapter();
            orderAdapter.setOnAddListener(new SimpleRecordOrderAdapter.OnAddListener() {
                @Override
                public void onAddOrder() {
                    if (getActivity() instanceof FavorPopupMvpActivity) {
                        ((FavorPopupMvpActivity) getActivity()).requestSelectOrder();
                    }
                    else {
                        showToastLong("Function is not supported in parent activity");
                    }
                }
            });
            orderAdapter.setList(orders);
            rvOrders.setAdapter(orderAdapter);
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        public void setRecordId(long mRecordId) {
            this.mRecordId = mRecordId;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }
    }

}
