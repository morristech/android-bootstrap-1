package com.tunjid.androidbootstrap.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.tunjid.androidbootstrap.R;
import com.tunjid.androidbootstrap.model.Doggo;
import com.tunjid.androidbootstrap.view.recyclerview.InteractiveAdapter;
import com.tunjid.androidbootstrap.view.recyclerview.InteractiveViewHolder;
import com.tunjid.androidbootstrap.view.util.ViewUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

public class ImageListAdapter extends InteractiveAdapter<ImageListAdapter.ImageViewHolder, ImageListAdapter.ImageListAdapterListener> {

    private List<Doggo> doggos;

    public ImageListAdapter(List<Doggo> doggos, ImageListAdapterListener listener) {
        super(listener);
        setHasStableIds(true);
        this.doggos = doggos;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(getItemView(R.layout.viewholder_image_list, parent), adapterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int recyclerViewPosition) {
        final Doggo item = doggos.get(recyclerViewPosition);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return doggos.size();
    }

    @Override
    public long getItemId(int position) {
        return doggos.get(position).hashCode();
    }


    public interface ImageListAdapterListener extends InteractiveAdapter.AdapterListener {
        default void onDoggoClicked(Doggo doggo) { }

        default void onDoggoImageLoaded(Doggo doggo) { }
    }

    public static class ImageViewHolder extends InteractiveViewHolder<ImageListAdapterListener>
            implements View.OnClickListener {

        private static final int FULL_SIZE_DELAY = 100;
        private static int THUMBNAIL_SIZE = 250;

        private Doggo doggo;
        private final TextView textView;
        private final ImageView fullSize;
        public final ImageView thumbnail;

        public ImageViewHolder(View itemView, ImageListAdapterListener adapterListener) {
            super(itemView, adapterListener);

            textView = itemView.findViewById(R.id.doggo_name);
            fullSize = itemView.findViewById(R.id.full_size);
            thumbnail = itemView.findViewById(R.id.doggo_image);
            itemView.setOnClickListener(this);
        }

        public void bind(Doggo doggo) {
            this.doggo = doggo;

            ViewCompat.setTransitionName(thumbnail, ViewUtil.transitionName(doggo, thumbnail));
            getCreator(doggo)
                    .resize(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                    .into(thumbnail, onSuccess(this::onThumbnailLoaded));

            textView.setText(doggo.getName());
        }

        private RequestCreator getCreator(Doggo doggo) {
            return Picasso.with(thumbnail.getContext())
                    .load(doggo.getImageRes()).centerCrop();
        }

        private void onThumbnailLoaded() {
            adapterListener.onDoggoImageLoaded(doggo);
            if (fullSize != null) fullSize.postDelayed(() -> getCreator(doggo).fit()
                    .into(fullSize, onSuccess(() -> fullSize.setVisibility(View.VISIBLE))), FULL_SIZE_DELAY);
        }

        private Callback onSuccess(Runnable runnable) {
            return new Callback() {
                public void onSuccess() { runnable.run(); }

                public void onError() { }
            };
        }

        @Override
        public void onClick(View v) { adapterListener.onDoggoClicked(doggo); }
    }
}
