package com.example.dcardtry;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public  class ArticleSummaryAdapter extends RecyclerView.Adapter<ArticleSummaryAdapter.ViewHolder>{
    LayoutInflater inflater;
    List<GetDcardInfo> dcards;
    private Context mContext;
    private  int size;

    public ArticleSummaryAdapter(Context context, List<GetDcardInfo> dcards,int size) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.dcards = dcards;
        this.size=size;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.article_list_item,parent,false);
        return new ViewHolder( view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ShortTitle,ShortContent;
        if(dcards.get(position).getTitle().length()>9){
            ShortTitle=dcards.get(position).getTitle().substring(0,8)+"...";
        }else{
            ShortTitle=dcards.get(position).getTitle();
        }

        if(dcards.get(position).getContent().length()>20){
            ShortContent=dcards.get(position).getContent().substring(0,20)+"...";
        }
        else{
            ShortContent=dcards.get(position).getContent()+"...";
        }

        holder.mTitle.setText(ShortTitle);
        holder.mContent.setText(ShortContent);
        holder.mDate.setText(dcards.get(position).getDate());
    }

    @Override
    public int getItemCount() {
     return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTitle, mDate, mContent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title_txtView);
            mDate = (TextView) itemView.findViewById(R.id.date_txtView);
            mContent = (TextView) itemView.findViewById(R.id.content_txtView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(mContext, DcardDetailActivity.class);
                        intent.putExtra("title", mTitle.getText().toString());
                        intent.putExtra("content", mContent.getText().toString());
                        intent.putExtra("date", mDate.getText().toString());
                        mContext.startActivity(intent);
                        Toast.makeText(mContext,
                                "clicked",Toast.LENGTH_SHORT).show();
                    }
                    catch(Exception e) {
                        Toast.makeText(mContext,
                                "error",Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }
}
