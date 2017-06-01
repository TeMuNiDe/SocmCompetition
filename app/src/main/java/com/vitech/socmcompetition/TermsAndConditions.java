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


public class TermsAndConditions extends Fragment {

    public TermsAndConditions(){

    }

      @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View terms =  inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);
          ListView view = (ListView)terms.findViewById(R.id.t_and_c);
          view.setAdapter(ArrayAdapter.createFromResource(getActivity(),R.array.terms_conditions,android.R.layout.simple_list_item_1));
          return terms;
    }

  }
