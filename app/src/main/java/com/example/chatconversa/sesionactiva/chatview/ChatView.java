package com.example.chatconversa.sesionactiva.chatview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.chatconversa.FullImageDialog;
import com.example.chatconversa.R;
import com.example.chatconversa.ServicioWeb;
import com.example.chatconversa.sesionactiva.Bienvenida_activity;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeViewModel;
import com.example.chatconversa.sesionactiva.enviarchat.ubicacion.UbicacionViewModel;
import com.example.chatconversa.sesionactiva.enviarchat.ubicacion.ViewLocationSentMapDialog;
import com.google.android.gms.maps.model.LatLng;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private EnviarMensajeViewModel chatViewModel;

    private MsgViewModel msgViewModel;

    /**TextView que muestra los mensajes que envía el usuario al chat.*/
    private TextView displayMyText;

    /**ImageVIiew que muestra las imágenes enviadas al chat.*/
    private ImageView displayMyPhoto;

    /**ImageView que muestra la imagen de un mapa falso (envío de ubicaciones)*/
    private ImageView displayFakeMap;

    /**Obtener datos del usuario para subir la imagen falsa del mapa.*/
    private String accessToken;
    private int userID;
    private String usernameWSR;

    /**Ubicaciones.*/
    private UbicacionViewModel ubicacionViewModel;
    private ViewLocationSentMapDialog locationReceived  = new ViewLocationSentMapDialog();

    private FullImageDialog fullImageDialog;

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

        chatViewModel = ViewModelProviders.of(getActivity()).get(EnviarMensajeViewModel.class);
        ubicacionViewModel = ViewModelProviders.of(getActivity()).get(UbicacionViewModel.class);

        msgViewModel = ViewModelProviders.of(getActivity()).get(MsgViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View[] root = {inflater.inflate(R.layout.fragment_chat_view, container, false)};

        /**Se obtiene el username para comprobar si es que, de los mensajes recuperados algunos
         * fueron enviados por el usuario o no.*/
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("accessToken", null);
        userID = sharedPreferences.getInt("userID", -1);
        usernameWSR = sharedPreferences.getString("username", null);

        contenedor = root[0].findViewById(R.id.chat_messages);

        /**
        msgViewModel.obtenerMsg().observe(getActivity(), new Observer<MensajesRespWS>() {
            @Override
            public void onChanged(MensajesRespWS mensajesRespWS) {

            }
        });
         */

        params.setMargins(10,10,10,10);
        myMessagesParams.setMargins(10,10,10,10);
        myMessagesParams.gravity = Gravity.END;

        Log.d("retrofit", "SHARED PREFERENCES USERNAME: " + sharedPreferences.getString("username", null));

        for (int i=data.getData().length-1; i>=0; i--) {
            if (!data.getData()[i].getUser().getUsername().equalsIgnoreCase(usernameWSR)) {
                /**Carga nombre usuario*/
                if (data.getData()[i].getUser().getUsername() != null) {
                    TextView display = new TextView(getActivity());
                    display.setText(data.getData()[i].getUser().getUsername());
                    display.setTextSize(12);
                    display.setPadding(12, 12, 12, 12);

                    if (!data.getData()[i].getUser().getUsername().equalsIgnoreCase(sharedPreferences.getString("username", null))) {
                        //display.setBackgroundColor(Color.parseColor("#f1faee"));
                        display.setTextColor(Color.parseColor("#000000"));
                        display.setLayoutParams(params);
                    } else {
                        //display.setBackgroundColor(Color.parseColor("#457b9d"));
                        display.setTextColor(Color.parseColor("#FFFFFF"));
                        display.setLayoutParams(myMessagesParams);
                    }

                    contenedor.addView(display);
                }

                /**Cargar thumbnail del usuario (img de perfil)*/
                if (!data.getData()[i].getUser().getUser_thumbnail().isEmpty()) {
                    Log.d("retrofit", "THUMBNAIL URL: " + data.getData()[i].getUser().getUser_thumbnail());

                    String imgUserUrl = data.getData()[i].getUser().getUser_thumbnail();
                    ImageView displayUserImg = new ImageView(getActivity());
                    Picasso.get().load(imgUserUrl).resize(80,80).into(displayUserImg);

                    /**Transformacion a imagen circular, pero se ve enanna :c
                     Transformation transformation = new RoundedTransformationBuilder()
                     .borderColor(Color.TRANSPARENT)
                     .borderWidthDp(3)
                     .cornerRadius(30)
                     .oval(false)
                     .build();

                     Picasso.get()
                     .load(imageUrl)
                     .fit()
                     .transform(transformation)
                     .into(displayUserImg);
                     */

                    displayUserImg.setPadding(12, 12, 12, 12);

                    if (!data.getData()[i].getUser().getUsername().equalsIgnoreCase(sharedPreferences.getString("username", null))) {
                        displayUserImg.setLayoutParams(params);
                    }else {
                        displayUserImg.setLayoutParams(myMessagesParams);
                    }

                    contenedor.addView(displayUserImg);

                }else {
                    ImageView displayUserImg = new ImageView(getActivity());
                    Picasso.get().load(R.drawable.user_icon).resize(80,80).into(displayUserImg);
                    displayUserImg.setPadding(12, 12, 12, 12);
                    displayUserImg.setLayoutParams(params);
                    contenedor.addView(displayUserImg);
                }
            }

            /**Carga mensajes.*/
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

            /**Carga imágenes*/
            if (!data.getData()[i].getImage().isEmpty()) {
                Log.d("retrofit", "IMAGE URL: " + data.getData()[i].getImage());

                String thumbnailURL = data.getData()[i].getThumbnail();
                ImageView displayThumbnail = new ImageView(getActivity());
                Picasso.get().load(thumbnailURL).into(displayThumbnail);

                displayThumbnail.setPadding(12, 12, 12, 12);

                if (!data.getData()[i].getUser().getUsername().equalsIgnoreCase(sharedPreferences.getString("username", null))) {
                    displayThumbnail.setLayoutParams(params);
                } else {
                    displayThumbnail.setLayoutParams(myMessagesParams);
                }


                int finalI1 = i;
                displayThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String imageUrl = data.getData()[finalI1].getImage();
                        fullImageDialog = new FullImageDialog(imageUrl);
                        fullImageDialog.show(getActivity().getSupportFragmentManager(), "FullImageDialog");
                    }
                });

                contenedor.addView(displayThumbnail);
            }

            /**Carga ubicaciones.*/
            if (data.getData()[i].getLatitude() != null && data.getData()[i].getLongitude() != null) {
                ImageView displayReceivedLocationsFakeMap = new ImageView(getActivity());
                displayReceivedLocationsFakeMap.setImageResource(R.drawable.googlemap);
                displayReceivedLocationsFakeMap.setPadding(15, 15, 15, 15);

                if (!data.getData()[i].getUser().getUsername().equalsIgnoreCase(sharedPreferences.getString("username", null))) {
                    displayReceivedLocationsFakeMap.setLayoutParams(params);
                } else {
                    displayReceivedLocationsFakeMap.setLayoutParams(myMessagesParams);
                }

                contenedor.addView(displayReceivedLocationsFakeMap);

                /**Al hacer click en el mapa enviado, se abre un diálogo que despliega un marcador con la ubicación recibida
                 * desde el WS.*/
                int finalI = i;
                displayReceivedLocationsFakeMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double lat = Double.parseDouble(data.getData()[finalI].getLatitude());
                        double lng = Double.parseDouble(data.getData()[finalI].getLongitude());

                        LatLng latLng = new LatLng(lat, lng);
                        ubicacionViewModel.setUbicacion(latLng);

                        locationReceived.show(getActivity().getSupportFragmentManager(), "ViewLocationSentMapDialog");
                    }
                });
            }

        }

        /**Para gestionar los mensajes que hayan sido enviados por el usuario al chat.*/
        chatViewModel.getWebServiceResponse().observe(getActivity(), new Observer<EnviarMensajeRespWS>() {
            @Override
            public void onChanged(EnviarMensajeRespWS enviarMensajeRespWS) {
                if (enviarMensajeRespWS.getStatus_code() != 0) {
                    /**Mensaje de texto.*/
                    if (enviarMensajeRespWS.getData().getMessage() != null) {
                        showSentText(enviarMensajeRespWS);
                    }

                    /**Imagen.*/
                    if (!enviarMensajeRespWS.getData().getImage().isEmpty()) {
                        showSentImage(enviarMensajeRespWS);
                    }

                    /**Ubicación (actual o seleccionada).*/
                    if (enviarMensajeRespWS.getData().getLatitude() != null &&
                            enviarMensajeRespWS.getData().getLongitude() != null) {
                        showSentLocation(enviarMensajeRespWS);
                    }
                }
            }
        });

        return root[0];
    }

    /**Método que gestiona la adición del mensaje de texto enviado por el usuario.*/
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

    /**Método que gestiona la adición de la imagen que el usuario haya enviado al chat.*/
    public void showSentImage(EnviarMensajeRespWS enviarMensajeRespWS) {
        displayMyPhoto = new ImageView(getActivity());
        Picasso.get().load(enviarMensajeRespWS.getData().getThumbnail()).into(displayMyPhoto);
        displayMyPhoto.setPadding(12, 12, 12, 12);
        displayMyPhoto.setLayoutParams(myMessagesParams);

        fullImageDialog = new FullImageDialog(enviarMensajeRespWS.getData().getImage());
        displayMyPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullImageDialog.show(getActivity().getSupportFragmentManager(), "FullImageDialog");
            }
        });

        contenedor.addView(displayMyPhoto);
    }

    /**Método que gestiona la ubicación que el usuario haya enviado al chat.*/
    public void showSentLocation(EnviarMensajeRespWS enviarMensajeRespWS) {
        displayFakeMap = new ImageView(getActivity());
        displayFakeMap.setImageResource(R.drawable.googlemap);
        displayFakeMap.setPadding(15, 15, 15, 15);
        displayFakeMap.setLayoutParams(myMessagesParams);
        contenedor.addView(displayFakeMap);

        displayFakeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationReceived.show(getActivity().getSupportFragmentManager(), "ViewLocationSentMapDialog");
            }
        });
    }

}