package com.jing.app.jjgallery.gdb.view.record.pad;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.view.adapter.BaseRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/12 10:51
 */
public class RecordPageScoreDetailAdapter extends BaseRecyclerAdapter<RecordPageScoreDetailAdapter.ScoreHolder, TitleValueBean> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_pad_score_detail;
    }

    @Override
    protected ScoreHolder newViewHolder(View view) {
        return new ScoreHolder(view);
    }

    @Override
    public void onBindViewHolder(ScoreHolder holder, int position) {
        if (list.get(position).isOnlyValue()) {
            holder.tvTitle.setVisibility(View.GONE);
        }
        else {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(list.get(position).getTitle());
        }
        holder.tvValue.setText(list.get(position).getValue());
    }

    public static class ScoreHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_value)
        TextView tvValue;

        public ScoreHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
