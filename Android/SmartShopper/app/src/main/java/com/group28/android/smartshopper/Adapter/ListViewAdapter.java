package com.group28.android.smartshopper.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.group28.android.smartshopper.Model.Memo;
import com.group28.android.smartshopper.R;

import java.util.ArrayList;

/**
 * Created by digantjagtap on 11/4/16.
 */

public class ListViewAdapter  extends BaseAdapter implements Filterable{

    private ArrayList<Memo> allMemos;
    public ArrayList<Memo> orig;
    private Context context;
    private LayoutInflater inflater;

    public ListViewAdapter(Context context, ArrayList<Memo> allMemos) {
        super();
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.allMemos = allMemos;
    }

    @Override
    public int getCount() {
        return allMemos.size();
    }

    @Override
    public Object getItem(int position) {
        return allMemos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MemoHolder holder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.memo_inflater, parent, false);
            holder = new MemoHolder();
            assert view != null;
            holder.memoListName = (TextView) view.findViewById(R.id.memoListName);
            view.setTag(holder);
        } else {
            holder = (MemoHolder) view.getTag();
        }

        holder.memoListName.setText(allMemos.get(position).getCategory());

        return view;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Memo> results = new ArrayList<Memo>();
                if (orig == null)
                    orig = allMemos;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final Memo g : orig) {
                            if (g.getCategory().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                allMemos = (ArrayList<Memo>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}

class MemoHolder {
    TextView memoListName;
}