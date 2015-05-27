package aad.finalproject.jhoregatta;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link aad.finalproject.jhoregatta.ProofOfIntentDialog.ProofOfIntentCommunicator} interface
 * to handle interaction events.
 * Use the {@link ProofOfIntentDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProofOfIntentDialog extends DialogFragment {

    private static final String LOGTAG = "DialogFragment "; // for logging

    private Button confirm;

    private EditText exitText;

    //instance of the communicator
    private ProofOfIntentCommunicator mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProofOfIntentDialog.
     */
    public static ProofOfIntentDialog newInstance(String param1, String param2) {
        ProofOfIntentDialog fragment = new ProofOfIntentDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ProofOfIntentDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_proof_of_intent_dialog, container, false);
        //wire widgets

        Button cancel = (Button) rootView.findViewById(R.id.btn_pid_cancel);
        confirm = (Button) rootView.findViewById(R.id.btn_pid_confirm);
        exitText = (EditText) rootView.findViewById(R.id.txtTypeExit);

        //disable confirmation initially.
        confirm.setEnabled(false);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // close the dialog
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentInteraction("exit"); // send the message to exit
            }
        });

        exitText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if the user typed in the required string then enable confirm button
                if (exitText.getText().toString().toUpperCase().equals("EXIT")) {
                    //enable confirmation button
                    confirm.setEnabled(true);
                } else {
                    //make sure confirmation button is disabled
                    confirm.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ProofOfIntentCommunicator) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ProofOfIntentCommunicator {
        public void onFragmentInteraction(String message); // return the message
    }

}
