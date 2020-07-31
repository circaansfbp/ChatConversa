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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.chatconversa.R;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeViewModel;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatView extends Fragment {
    LinearLayout contenedor;
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams myMessagesParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    private static MensajesRespWS data;
    private EnviarMensajeViewModel textViewModel;

    /**TextView que muestra los mensajes que envía el usuario al chat.*/
    private TextView displayMyText;

    /**ImageVIiew que muestra las imágenes enviadas al chat.*/
    private ImageView displayMyPhoto;

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

        textViewModel = ViewModelProviders.of(getActivity()).get(EnviarMensajeViewModel.class);
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
        myMessagesParams.setMargins(10,10,10,10);
        myMessagesParams.gravity = Gravity.END;

        Log.d("retrofit", "SHARED PREFERENCES USERNAME: " + sharedPreferences.getString("username", null));

        for (int i=data.getData().length-1; i>=0; i--) {

            if (data.getData()[i].getMessage() != null) {
                TextView display = new TextView(getActivity());
                display.setText(data.getData()[i].getMessage());
                display.setTextSize(24);
                display.setPadding(12, 12, 12, 12);

                if (!data.getData()[i].getUser().getUsername().equalsIgnoreCase(sharedPreferences.getString("username", null))) {
                    display.setBackgroundColor(Color.parseColor("#f1faee"));
                    display.setTextColor(Color.parseColor("#000000"));
                    display.setLayoutParams(params);
                } else {
                    display.setBackgroundColor(Color.parseColor("#457b9d"));
                    display.setTextColor(Color.parseColor("#FFFFFF"));
                    display.setLayoutParams(myMessagesParams);
                }

                contenedor.addView(display);
            }

            if (!data.getData()[i].getImage().isEmpty()) {
                Log.d("retrofit", "IMAGE URL: " + data.getData()[i].getImage());

                String imageURL = data.getData()[i].getImage();
                ImageView displayImage = new ImageView(getActivity());
                Picasso.get().load(imageURL).into(displayImage);

                displayImage.setPadding(12, 12, 12, 12);

                if (!data.getData()[i].getUser().getUsername().equalsIgnoreCase(sharedPreferences.getString("username", null))) {
                    displayImage.setLayoutParams(params);
                } else {
                    displayImage.setLayoutParams(myMessagesParams);
                }

                contenedor.addView(displayImage);
            }

        }

        textViewModel.getWebServiceResponse().observe(getActivity(), new Observer<EnviarMensajeRespWS>() {
            @Override
            public void onChanged(EnviarMensajeRespWS enviarMensajeRespWS) {
                if (enviarMensajeRespWS.getStatus_code() != 0) {
                    if (enviarMensajeRespWS.getData().getMessage() != null) {
                        showSentText(enviarMensajeRespWS);
                    }

                    if (!enviarMensajeRespWS.getData().getImage().isEmpty()) {
                        showSentImage(enviarMensajeRespWS);
                    }
                }
            }
        });

        return root;
    }

    public void showSentText(EnviarMensajeRespWS enviarMensajeRespWS) {
        displayMyText = new TextView(getActivity());
        displayMyText.setText(enviarMensajeRespWS.getData().getMessage());
        displayMyText.setTextSize(24);
        displayMyText.setBackgroundColor(Color.parseColor("#457b9d"));
        displayMyText.setTextColor(Color.parseColor("#FFFFFF"));
        displayMyText.setLayoutParams(myMessagesParams);
        displayMyText.setPadding(12, 12, 12, 12);

        contenedor.addView(displayMyText);
    }

    public void showSentImage(EnviarMensajeRespWS enviarMensajeRespWS) {
        displayMyPhoto = new ImageView(getActivity());
        Picasso.get().load(enviarMensajeRespWS.getData().getImage()).into(displayMyPhoto);
        displayMyPhoto.setPadding(12, 12, 12, 12);
        displayMyPhoto.setLayoutParams(myMessagesParams);

        contenedor.addView(displayMyPhoto);
    }
}