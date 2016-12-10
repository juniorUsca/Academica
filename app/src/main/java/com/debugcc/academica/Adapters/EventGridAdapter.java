package com.debugcc.academica.Adapters;



import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.debugcc.academica.Models.Event;
import com.debugcc.academica.R;
import com.debugcc.academica.Utils.Utils;

import java.util.List;

public class EventGridAdapter extends RecyclerView.Adapter<EventGridAdapter.ViewHolder>{

    private List<Event> mDataSet;
    private OnItemClickListener mListener;

    public EventGridAdapter(List<Event> mDataSet) {
        this.mDataSet = mDataSet;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_grid_event, parent, false);

        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private ImageView mImage;
        private TextView mName;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mImage = (ImageView) itemView.findViewById(R.id.row_grid_event_image);
            mName = (TextView) itemView.findViewById(R.id.row_grid_event_name);
        }

        public void bind(final Event event) {
            mName.setText(event.getNombre());

            mImage.getLayoutParams().height = Utils.getDeviceWidth(mView.getContext())/3;

            Glide.with(itemView.getContext())
                    .load(event.getImagen())
                    .placeholder(R.drawable.img_placeholder_dark)
                    .centerCrop()
                    .crossFade()
                    .error(R.drawable.img_placeholder_dark)
                    .into(mImage);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(getAdapterPosition(), event);
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, Event event);
    }
}
