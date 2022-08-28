package com.mectype.countries.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mectype.countries.interfaces.CountryItemClickListener;
import com.mectype.countries.databinding.CountryRowLayoutBinding;
import com.mectype.countries.model.Country;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyViewHolder> {
    private static final String TAG = "CountryAdapter";
    final List<Country> listCountry;
    final File imagesDir;
    final CountryItemClickListener listener;


    public CountryAdapter(File imagesDir, List<Country> listCountry, CountryItemClickListener listener) {
        this.imagesDir = imagesDir;
        this.listCountry = listCountry;
        this.listener = listener;

    }

    @NonNull
    @Override
    public CountryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CountryRowLayoutBinding view = CountryRowLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  CountryAdapter.MyViewHolder holder, int position) {
        Country countryModel = listCountry.get(position);
        holder.binding.getRoot().setOnClickListener((View v) ->
                listener.onCountryItemClicked(countryModel, holder.binding.flag));
        holder.binding.countryName.setText(countryModel.getName());
        holder.binding.capital.setText(countryModel.getCapital());
        holder.binding.population.setText(String.valueOf(countryModel.getPopulation()));
        getAndSetImage(countryModel, holder.binding.flag, holder.binding.flagProgressbar);
    }

    private void getAndSetImage(Country country, ImageView flag, ProgressBar flagProgressbar) {
        File imageFile = new File(imagesDir, country.getName().replaceAll(" ", "_"));
        if (imageFile.exists()) {
            flagProgressbar.setVisibility(View.GONE);
            flag.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        } else {
            Bitmap bitmapFlag = country.getFlagBitmap();
            if (null != bitmapFlag) {
                flagProgressbar.setVisibility(View.GONE);
                flag.setImageBitmap(bitmapFlag);
            } else {
                flagProgressbar.setVisibility(View.VISIBLE);
                Executor executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executor.execute(() -> {
                    Bitmap bitmap = retrieveImage(country);
                    saveImage(bitmap, imageFile);
                    handler.post(() -> {
                        flagProgressbar.setVisibility(View.GONE);
                        flag.setImageBitmap(bitmap);
                    });
                });
            }
        }
    }

    private void saveImage(Bitmap bitmap, File imageFile) {
        FileOutputStream fos = null;
        try {
            if (!imageFile.createNewFile()) return;
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap retrieveImage(Country country) {
        Bitmap bitmap = country.getFlagBitmap();
        if (null != bitmap) {
            return bitmap;
        }
        return downloadImage(country.getFlag());
    }

    private Bitmap downloadImage(final String urlStr) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlStr);
            is = (InputStream) url.getContent();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    @Override
    public int getItemCount() {
        return listCountry.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CountryRowLayoutBinding binding;

        public MyViewHolder(@NonNull CountryRowLayoutBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}