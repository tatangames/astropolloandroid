package com.tatanstudios.astropollocliente.adaptadores.principal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.fragmentos.principal.FragmentZonaServicios;
import com.tatanstudios.astropollocliente.modelos.principal.SliderItem;

import java.util.ArrayList;
import java.util.List;

public class SliderBannerPrincipalAdapter extends SliderViewAdapter<SliderBannerPrincipalAdapter.SliderAdapterVH> {

    private Context context;
    private List<SliderItem> mSliderItems = new ArrayList<>();

    FragmentZonaServicios fragmentZonaServicios;

    public SliderBannerPrincipalAdapter(Context context, FragmentZonaServicios fragmentZonaServicios) {
        this.context = context;
        this.fragmentZonaServicios = fragmentZonaServicios;
    }


    public void addItem(SliderItem sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderBannerPrincipalAdapter.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_banner_slider, null);
        return new SliderBannerPrincipalAdapter.SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderBannerPrincipalAdapter.SliderAdapterVH viewHolder, final int position) {

        SliderItem sliderItem = mSliderItems.get(position);

        Glide.with(viewHolder.itemView)
                .load(sliderItem.getImageUrl())
                .fitCenter()
                .into(viewHolder.imageViewBackground);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mSliderItems.get(position).getIdproducto() != 0){
                    int id = mSliderItems.get(position).getIdproducto();

                    fragmentZonaServicios.redireccionarProducto(id);
                }
            }
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        ImageView imageGifContainer;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            this.itemView = itemView;
        }
    }

}