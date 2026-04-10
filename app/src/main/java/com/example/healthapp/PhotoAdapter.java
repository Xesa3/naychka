package com.example.healthapp;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{

    private List<String> photos;
    private OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClick(String uri);
    }

    public PhotoAdapter(List<String> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_photo, parent, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String uri = photos.get(position);

        holder.photoView.setImageURI(Uri.parse(uri));

        //holder.photoView.setOnClickListener(v -> {
         //   listener.onPhotoClick(uri);
        //});
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
        }
    }

}
