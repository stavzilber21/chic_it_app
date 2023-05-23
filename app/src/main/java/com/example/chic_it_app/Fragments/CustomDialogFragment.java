package com.example.chic_it_app.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.chic_it_app.R;


public class CustomDialogFragment extends DialogFragment {

    public interface DialogListener {
        void onSave(String fieldValue1, String fieldValue2, String fieldValue3, String fieldValue4);
    }

    private DialogListener dialogListener;

    private EditText editText1, editText2, editText3, editText4;

    public void setDialogListener(DialogListener listener) {
        this.dialogListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(view);

        editText1 = view.findViewById(R.id.editText1);
        editText2 = view.findViewById(R.id.editText2);
        editText3 = view.findViewById(R.id.editText3);
        editText4 = view.findViewById(R.id.editText4);
        Button saveButton = view.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText1.getText().toString();
                String store = editText2.getText().toString();
                String price = editText3.getText().toString();
                String more = editText4.getText().toString();

                // Save the details entered by the user
                if (dialogListener != null) {
                    dialogListener.onSave(name, store, price, more);
                }
                dismiss(); // Close the dialog
            }
        });

        return builder.create();
    }
}