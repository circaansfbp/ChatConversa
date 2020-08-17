package com.example.chatconversa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.fragment.app.DialogFragment;

import com.squareup.picasso.Picasso;

/**Clase para desplegar las imágenes enviadas en su tamaño completo.*/
public class FullImageDialog extends DialogFragment {
    private String imageURL;
    private View root;

    public FullImageDialog(String url) {
        this.imageURL = url;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder displayImage = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        root = inflater.inflate(R.layout.dialog_display_full_image, null);
        final ImageView imageView = root.findViewById(R.id.full_length_img);

        Picasso.get().load(imageURL).into(imageView);

        displayImage.setView(root).setNeutralButton("CERRAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return displayImage.create();
    }
}
