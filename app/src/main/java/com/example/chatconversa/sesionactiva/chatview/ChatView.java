package com.example.chatconversa.sesionactiva.chatview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chatconversa.R;
import com.example.chatconversa.sesionactiva.enviarchat.TextRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.TextViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatView extends Fragment {
    LinearLayout contenedor;
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    LinearLayout.LayoutParams myTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    private static MensajesRespWS data;
    private TextViewModel textViewModel;

    private TextView displayMyText;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatView() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ChatView newInstance(MensajesRespWS respuestaWS) {
        ChatView fragment = new ChatView();
        data = respuestaWS;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        textViewModel = ViewModelProviders.of(getActivity()).get(TextViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat_view, container, false);

        /**Se obtiene el username para comprobar si es que, de los mensajes recuperados algunos
         * fueron enviados por el usuario o no.*/
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        contenedor = root.findViewById(R.id.chat_messages);
        params.setMargins(10,10,10,10);

        myTextParams.setMargins(10,10,10,10);

        Log.d("retrofit", "SHARED PREFERENCES USERNAME: " + sharedPreferences.getString("username", null));

        for (int i=data.getData().length-1; i>=0; i--) {

            Log.d("retrofit", "USERNAME: " + data.getData()[i].getUser().getUsername());

            TextView display = new TextView(getActivity());
            display.setText(data.getData()[i].getMessage());
            display.setTextSize(24);
            display.setTextColor(Color.parseColor("#FFFFFF"));
            display.setPadding(12, 12, 12, 12);

            if (!data.getData()[i].getUser().getUsername().equalsIgnoreCase(sharedPreferences.getString("username", null))) {
                display.setBackgroundColor(Color.parseColor("#457b9d"));
                display.setLayoutParams(params);
            } else {
                display.setBackgroundColor(Color.parseColor("#023e7d"));
                myTextParams.gravity = Gravity.END;
                display.setLayoutParams(myTextParams);
            }

            contenedor.addView(display);
        }
        return root;
    }

    public void showSentText() {
        if (displayMyText != null) {
            contenedor.removeView(displayMyText);
        }

        myTextParams.gravity = Gravity.END;

        textViewModel.getWebServiceResponse().observe(getActivity(), new Observer<TextRespWS>() {
            @Override
            public void onChanged(TextRespWS textRespWS) {
                displayMyText = new TextView(getActivity());
                displayMyText.setText(textRespWS.getData().getMessage());
                displayMyText.setTextSize(24);
                displayMyText.setBackgroundColor(Color.parseColor("#023e7d"));
                displayMyText.setTextColor(Color.parseColor("#FFFFFF"));
                displayMyText.setLayoutParams(myTextParams);
                displayMyText.setPadding(12, 12, 12, 12);
            }
        });

        contenedor.addView(displayMyText);
    }
}