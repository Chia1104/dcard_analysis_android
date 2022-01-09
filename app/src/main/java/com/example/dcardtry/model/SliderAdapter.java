package com.example.dcardtry.model;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcardtry.R;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.MyViewHolder> {

    public SliderAdapter(int[] list, String[] bannertxt, int[] bannerpic) {
        this.list = list;this.bannertxt=bannertxt;this.bannerpic=bannerpic;
    }

    int list[];
    String bannertxt[];
    int bannerpic[];

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bannertxt.setText(bannertxt[position%3]);
        holder.bannerpic.setBackgroundResource(bannerpic[position%3]);
    }

    @Override
    public int getItemCount() { return Integer.MAX_VALUE;}

    public class MyViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView bannertxt;
        View bannerpic;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);;
            view=itemView.findViewById(R.id.view);
            bannertxt=itemView.findViewById(R.id.banner_title);
            bannerpic=itemView.findViewById(R.id.banner_pic);
        }
    }
}
