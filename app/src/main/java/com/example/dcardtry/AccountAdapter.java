package com.example.dcardtry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>{
    LayoutInflater inflater;
    List<GetAccountInfo> accountList;
    private Context mContext;

    public AccountAdapter(Context context, List<GetAccountInfo> accountList) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.account_manager_accountlist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(accountList.get(position).getName());
        holder.jobTitle.setText(accountList.get(position).getjobTitle());
        holder.mail.setText(accountList.get(position).getMail());
    }


    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name, jobTitle, mail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.AccountList_Name);
            jobTitle = (TextView) itemView.findViewById(R.id.AccountList_JobTitle);
            mail = (TextView) itemView.findViewById(R.id.AccountList_Mail);
        }
    }
}