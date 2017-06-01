package com.vitech.socmcompetition;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class RulesAndRegulations extends Fragment {
        public RulesAndRegulations() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View terms =  inflater.inflate(R.layout.fragment_rules_and_regulations, container, false);
        ListView view = (ListView)terms.findViewById(R.id.r_and_r);
        view.setAdapter(ArrayAdapter.createFromResource(getActivity(),R.array.rules_regulations,android.R.layout.simple_list_item_1));
        return terms;
    }

}
