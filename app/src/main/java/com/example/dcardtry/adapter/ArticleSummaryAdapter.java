package com.example.dcardtry.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dcardtry.UIActivity.DcardDetailActivity;
import com.example.dcardtry.R;
import com.example.dcardtry.model.Dcard;

import java.util.List;

public  class ArticleSummaryAdapter extends RecyclerView.Adapter<ArticleSummaryAdapter.ViewHolder>{
    LayoutInflater inflater;
    List<Dcard> dcards;
    private final Context mContext;
    private final int size;

    public ArticleSummaryAdapter(Context context, List<Dcard> dcards, int size) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.dcards = dcards;
        this.size=size;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.article_summary_list_item,parent,false);
        return new ViewHolder( view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        String ShortTitle,ShortContent;
//        if(dcards.get(position).getTitle().length()>9){
//            ShortTitle=dcards.get(position).getTitle().substring(0,8)+"...";
//        }else{
//            ShortTitle=dcards.get(position).getTitle();
//        }
//
//        if(dcards.get(position).getContent().length()>20){
//            ShortContent=dcards.get(position).getContent().substring(0,20)+"...";
//        }
//        else{
//            ShortContent=dcards.get(position).getContent()+"...";
//        }
//
//        holder.mTitle.setText(ShortTitle);
//        holder.mContent.setText(ShortContent);
//        holder.mDate.setText(dcards.get(position).getDate());

        holder.mTitle.setText(dcards.get(position).getTitle());
        holder.mDate.setText(dcards.get(position).getDate());
        holder.mContent.setText(dcards.get(position).getContent());
        holder.mScore.setText(dcards.get(position).getSascore());
        holder.mClass.setText(dcards.get(position).getSaclass());
        holder.mLv1.setText(dcards.get(position).getLv1());
        holder.mLv2.setText(dcards.get(position).getLv2());
        holder.mLv3.setText(dcards.get(position).getLv3());
        holder.mId.setText(dcards.get(position).getId());
        switch (holder.mClass.getText().toString()){
            case "Positive":
                holder.mScore.setTextColor(mContext.getResources().getColor(R.color.posColor));
                holder.mClass.setTextColor(mContext.getResources().getColor(R.color.posColor));
                break;
            case "Neutral":
                holder.mScore.setTextColor(mContext.getResources().getColor(R.color.neuColor));
                holder.mClass.setTextColor(mContext.getResources().getColor(R.color.neuColor));
                break;
            case "Negative":
                holder.mScore.setTextColor(mContext.getResources().getColor(R.color.negColor));
                holder.mClass.setTextColor(mContext.getResources().getColor(R.color.negColor));
                break;
            case "null":
                holder.mScore.setTextColor(mContext.getResources().getColor(R.color.gray));
                holder.mClass.setTextColor(mContext.getResources().getColor(R.color.gray));
        }
    }

    @Override
    public int getItemCount() {
     return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle, mDate, mContent, mScore, mClass, mLv1, mLv2, mLv3, mId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title_txtView);
            mDate = itemView.findViewById(R.id.date_txtView);
            mContent = itemView.findViewById(R.id.content_txtView);
            mScore = itemView.findViewById(R.id.score_txtView);
            mClass = itemView.findViewById(R.id.class_txtView);
            mLv1 = itemView.findViewById(R.id.lv1_txtView);
            mLv2 = itemView.findViewById(R.id.lv2_txtView);
            mLv3 = itemView.findViewById(R.id.lv3_txtView);
            mId = itemView.findViewById(R.id.id_txtView);

            itemView.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(mContext, DcardDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("title", mTitle.getText().toString());
                    intent.putExtra("content", mContent.getText().toString());
                    intent.putExtra("date", mDate.getText().toString());
                    intent.putExtra("saclass", mClass.getText().toString());
                    intent.putExtra("sascore", mScore.getText().toString());
                    intent.putExtra("lv1", mLv1.getText().toString());
                    intent.putExtra("lv2", mLv2.getText().toString());
                    intent.putExtra("lv3", mLv3.getText().toString());
                    intent.putExtra("id", mId.getText().toString());
                    mContext.startActivity(intent);
                }
                catch(Exception e) {
                    Toast.makeText(mContext,
                            "error" + e,Toast.LENGTH_SHORT).show();
                }

            });

        }
    }
}
