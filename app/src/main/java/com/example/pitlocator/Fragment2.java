package com.example.pitlocator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.preprocess.BitmapEncoder;
import com.cloudinary.android.preprocess.DimensionsValidator;
import com.cloudinary.android.preprocess.ImagePreprocessChain;
import com.example.pitlocator.databinding.ComplainLayoutBinding;
import com.example.pitlocator.modals.ComplainModal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Fragment2 extends Fragment {

    ComplainLayoutBinding binding;
   int range ;
   String condition;

    FirebaseDatabase database;

    private static final String TAG = "Upload ###";

    private Uri imagePath;
    String imageUrl;
    String phonenum;
    Map<String, String> config = new HashMap<>();
    String backendOtp;
    String currentUser;
//    boolean executed = false;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = ComplainLayoutBinding.inflate(getLayoutInflater());
        

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();

        requestPermission();

    //       #################       image select and upload code  ######################################

        binding.complainIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
                    selectImage();
                Log.d(TAG, ": "+"request permission");
                Log.d(TAG, ": "+"request permission");
            }
        });



        binding.rangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.rangeTV.setText(""+i+ "(in metres)");
                range = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.conditionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(i<32){
                    binding.conditionTV.setText("Normal");
                    condition = "Normal";
                }
                else if(i>32 && i<66){
                    binding.conditionTV.setText("Moderate");
                    condition = "Moderate";
                }
                else{
                    binding.conditionTV.setText("Extreme");
                    condition = "Extreme";
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.phoneET.getText().toString().trim().isEmpty()){
                    if((binding.phoneET.getText().toString().trim()).length() == 10){

                        binding.progressBar3.setVisibility(View.VISIBLE);
                        binding.getOtp.setVisibility(View.INVISIBLE);

                        getTheOtp();



                    }
                    else{
                        Toast.makeText(getContext(),"Please enter correct number",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(),"Enter Mobile Number",Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phonenum = binding.phoneET.getText().toString();

                if(binding.otpET.getText().toString().trim().length() == 6){



                    if(backendOtp!=null){
                        binding.progressBar2.setVisibility(View.VISIBLE);


                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(backendOtp,binding.otpET.getText().toString());
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                                        if(task.isSuccessful()){
                                            uploadImage();
                                           // Toast.makeText(getContext(),"message : " + imageUrl,Toast.LENGTH_SHORT).show();

                                        }
                                        else{
                                            Toast.makeText(getContext(),"Enter correct Otp",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else {
                        Toast.makeText(getContext(),"Error please check internet connection",Toast.LENGTH_SHORT).show();
                    }


                }
                else{
                    Toast.makeText(getContext(),"please enter all numbers",Toast.LENGTH_SHORT).show();
                }


//                Toast.makeText(getActivity(), ""+range+" "+condition+" "+phonenum, Toast.LENGTH_SHORT).show();
            }
        });

//        resendOtp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getTheOtp();
//                Toast.makeText(getContext(),"OTP sent successfully",Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void initCongif() {

        config.put("cloud_name", "dnooguikh");
        config.put("api_key","355238768254162");
        config.put("api_secret","_LonUMLkuUQLC8wg5oKLZSDQsnc");

        MediaManager.init(getContext(),config);
    }

    private void requestPermission() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED)
        {

            int IMAGE_REQ = 1;
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, IMAGE_REQ);
        }
//        else
//        {
//
//        }

    }

    private void selectImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
        binding.clickhereTV.setVisibility(View.INVISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);

    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        imagePath=data.getData();
                        Picasso.get().load(imagePath).into(binding.complainIV);
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        binding.complainIVview.setVisibility(View.INVISIBLE);

                    }
                }
            });

    private void uploadImage(){
        initCongif();
        MediaManager.get().upload(imagePath).preprocess(ImagePreprocessChain.limitDimensionsChain(1000,1000)
                .addStep(new DimensionsValidator(10,10,1000,1000))
                .saveWith(new BitmapEncoder(BitmapEncoder.Format.WEBP,80))).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId)
            {
                Log.d(TAG, "onStart: "+"started");
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                Log.d(TAG, "onStart: "+"uploading");
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {

                imageUrl = resultData.get("url").toString();
                ComplainModal complain = new ComplainModal(phonenum,imageUrl,condition,range);

                database.getReference().child("Complains")
                        .child(phonenum)
                        .push()
                        .setValue(complain)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                binding.phoneET.setText("");
                                binding.otpET.setText("");
                                binding.conditionSeekBar.setProgress(0);
                                binding.rangeSeekBar.setProgress(0);
                                binding.complainIV.setImageResource(R.drawable.complainimg);
                                binding.complainIVview.setVisibility(View.VISIBLE);
                                binding.clickhereTV.setVisibility(View.VISIBLE);
                                binding.progressBar2.setVisibility(View.INVISIBLE);
                                binding.progressBar3.setVisibility(View.INVISIBLE);
                                Toast.makeText(getContext(),"Image successfully uploaded",Toast.LENGTH_SHORT).show();
                            }
                        });




            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.d(TAG, "onStart: "+error);
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Log.d(TAG, "onStart: "+error);
            }
        }).dispatch(getContext());
    }

    private void getTheOtp(){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder()
                        .setPhoneNumber("+91" + binding.phoneET.getText().toString().trim())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(requireActivity())
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                binding.progressBar3.setVisibility(View.INVISIBLE);
//                                binding.getOtp.setVisibility(View.VISIBLE);
                            }
                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                binding.progressBar3.setVisibility(View.INVISIBLE);
                                binding.getOtp.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(),e.toString(),Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                binding.progressBar3.setVisibility(View.INVISIBLE);
                                binding.getOtp.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(),"OTP sent successfully",Toast.LENGTH_SHORT).show();
                                backendOtp = s;
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Log.d("result","number correct");

    }
}
