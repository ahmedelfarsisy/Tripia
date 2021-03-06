package com.iti.mad41.tripia.ui.fragment.form;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.iti.mad41.tripia.R;
import com.iti.mad41.tripia.database.dto.Trip;
import com.iti.mad41.tripia.databinding.FormFragmentBinding;
import com.iti.mad41.tripia.helper.Constants;
import com.iti.mad41.tripia.helper.Validations;
import com.iti.mad41.tripia.repository.firebase.FirebaseRepo;
import com.iti.mad41.tripia.repository.localrepo.TripsDataRepository;
import com.iti.mad41.tripia.ui.activity.main.MainActivity;
import com.iti.mad41.tripia.ui.fragment.notes.NotesFragment;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FormFragment extends Fragment {
    private static final String TAG = "FormFragment";
    private static final int START_ADDRESS_REQUEST_CODE = 1;
    private static final int DESTINATION_ADDRESS_REQUEST_CODE = 2;
    private String API_KEY;
    private FormViewModel mViewModel;
    private FormFragmentBinding binding;
    private PlacesClient placesClient;
    private List<Place.Field> fields;
    private Place place;
    private boolean isFormComplete = false;
    private String startDate;
    private String startTime;
    private Trip trip;
    private String title;
    private Double startLatitude;
    private Double startLongitude;
    private String startAddress;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private String destinationAddress;
    private String imgB64;
    private FirebaseRepo firebaseRepo;
    private TripsDataRepository tripsDataRepository;
    private Trip updateTripObject;
    private int tripId;
    private boolean isNavigateToUpdate;
    long timeStampValue;

    public static FormFragment newInstance(int tripId, boolean isNavigateToUpdate) {
        FormFragment formFragment = new FormFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.TRIP_ID_KEY, tripId);
        bundle.putBoolean(Constants.IS_NAVIGATE_TO_UPDATE, isNavigateToUpdate);
        formFragment.setArguments(bundle);
        return formFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.form_fragment, container, false);
        ApplicationInfo app = null;
        try {
            app = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            API_KEY = bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return binding.getRoot();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseRepo = new FirebaseRepo(getActivity());
        tripsDataRepository = TripsDataRepository.getINSTANCE(getActivity());
        mViewModel = new ViewModelProvider(this, new FormViewModelFactory(firebaseRepo, tripsDataRepository)).get(FormViewModel.class);
        isNavigateToUpdate = getArguments().getBoolean(Constants.IS_NAVIGATE_TO_UPDATE);
        binding.setFormViewModel(mViewModel);
        mViewModel.setContext(getActivity());
        binding.setLifecycleOwner(this);
        initGooglePlaces();

        if (isNavigateToUpdate) {
            tripId = getArguments().getInt(Constants.TRIP_ID_KEY);
            Log.i(TAG, "onActivityCreated: " + tripId);
            mViewModel.getTrip(tripId);
        }

        mViewModel.isNavigateFromStartAddress.observe(getViewLifecycleOwner(), isNavigate -> {
            if (isNavigate) {
                navigateToGooglePlacesAutoComplete(START_ADDRESS_REQUEST_CODE);
            }
        });

        mViewModel.isNavigateFromDestinationAddress.observe(getViewLifecycleOwner(), isNavigate -> {
            if (isNavigate) {
                navigateToGooglePlacesAutoComplete(DESTINATION_ADDRESS_REQUEST_CODE);
            }
        });

        mViewModel.startDate.observe(getViewLifecycleOwner(), s -> {
            if (!Validations.isEmpty(s))
                startDate = s;
        });

        mViewModel.startTime.observe(getViewLifecycleOwner(), s -> {
            if (!Validations.isEmpty(s))
                startTime = s;
        });

        mViewModel.timeStamp.observe(getViewLifecycleOwner(), aLong -> {

            timeStampValue = aLong;
        });

        mViewModel.startAddress.observe(getViewLifecycleOwner(), address -> {
            if (!Validations.isEmpty(address))
                startAddress = address;
        });

        mViewModel.destinationAddress.observe(getViewLifecycleOwner(), address -> {
            if (!Validations.isEmpty(address))
                destinationAddress = address;
        });

        mViewModel.startAddress.observe(getViewLifecycleOwner(), address -> {
            if (!Validations.isEmpty(address))
                startAddress = address;
        });

        mViewModel.startLatitude.observe(getViewLifecycleOwner(), latitude -> {
            startLatitude = latitude;
        });

        mViewModel.startLongitude.observe(getViewLifecycleOwner(), longitude -> {
            startLongitude = longitude;
        });

        mViewModel.destinationAddress.observe(getViewLifecycleOwner(), address -> {
            if (!Validations.isEmpty(address))
                destinationAddress = address;
        });

        mViewModel.destinationLatitude.observe(getViewLifecycleOwner(), latitude -> {
            destinationLatitude = latitude;
        });

        mViewModel.destinationLongitude.observe(getViewLifecycleOwner(), longitude -> {
            destinationLongitude = longitude;
        });

        mViewModel.addressImageB64.observe(getViewLifecycleOwner(), img -> {
            imgB64 = img;
        });

        mViewModel.isRoundTrip.observe(getViewLifecycleOwner(), isRoundTrip -> {

        });

        mViewModel.liveTrip.observe(getViewLifecycleOwner(), trip -> {
            setDataToFormOnUpdateMode(trip);
            updateTripObject = trip;
        });

        binding.toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MainActivity.class));
        });

        binding.textViewEndPoint.setOnClickListener(v -> binding.textViewEndPoint.setText("sss"));

        mViewModel.isNavigateToNotes.observe(getViewLifecycleOwner(), isNavigate -> {
            if (isNavigate) {

                title = binding.editTextTripTitle.getText().toString();
                isFormComplete = Validations.isNull(startTime) &&
                        Validations.isNull(startDate) &&
                        Validations.isNull(title) &&
                        Validations.isNull(startAddress) &&
                        Validations.isNull(destinationAddress);
                if (!Validations.isEmpty(title) && isFormComplete) {
                    if (updateTripObject != null) {
                        updateTripObject.setId(tripId);
                        updateTripObject.setTripTitle(title);
                        updateTripObject.setStartAddress(startAddress);
                        updateTripObject.setStartLatitude(startLatitude);
                        updateTripObject.setStartLongitude(startLongitude);
                        updateTripObject.setDestinationAddress(destinationAddress);
                        updateTripObject.setDestinationLatitude(destinationLatitude);
                        updateTripObject.setDestinationLongitude(destinationLongitude);
                        updateTripObject.setDateTime(timeStampValue - 45);
                        updateTripObject.setImageUrl(imgB64);
                        navigateToNotes(updateTripObject);
                    } else {
                        trip = new Trip(
                            UUID.randomUUID().toString(),
                            title,
                            startAddress,
                            startLongitude,
                            startLatitude,
                            destinationAddress,
                            destinationLongitude,
                            destinationLatitude,
                            (timeStampValue - 45),
                            imgB64);
                        navigateToNotes(trip);
                    }
                } else {
                    if (Validations.isEmpty(binding.editTextTripTitle.getText().toString()))
                        binding.editTextTripTitle.setError("Title field is empty");
                    else
                        Toast.makeText(getActivity(), "you should fill all form", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == START_ADDRESS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                scheduleUpdateAfterOnActivityResult(data, bundle -> {
                    mViewModel.setStartAddressData(place.getName(), place.getLatLng().latitude, place.getLatLng().longitude);
                });
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                scheduleUpdateAfterOnActivityResult(data, bundle -> {
                    mViewModel.setDestinationAddressData(place.getName(), place.getLatLng().latitude, place.getLatLng().longitude);
                });
                mViewModel.fetchPhoto(place.getPhotoMetadatas());
                //Log.i("imgimgimgimg", img);
                //fetchPhoto(place.getPhotoMetadatas());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public <T> void scheduleUpdateAfterOnActivityResult(T data, Observer<T> observer) {
        final MutableLiveData<T> liveData = new MutableLiveData<>();
        liveData.observe(this, new Observer<T>() {
            @Override
            public void onChanged(@Nullable final T v) {
                observer.onChanged(v);
                liveData.removeObserver(this);
            }

        });
        liveData.postValue(data);
    }

    private void initGooglePlaces() {
        Log.i(TAG, API_KEY);
        Places.initialize(getActivity(), API_KEY);

        placesClient = Places.createClient(getActivity());

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS);
    }

    private void navigateToGooglePlacesAutoComplete(int requestCode) {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(getActivity());
        startActivityForResult(intent, requestCode);
    }

    private void setDataToFormOnUpdateMode(Trip trip) {
        binding.editTextTripTitle.setText(trip.getTripTitle());
        mViewModel.startAddress.setValue(trip.getStartAddress());
        mViewModel.addressImageB64.setValue(trip.getImageUrl());
        mViewModel.startLatitude.setValue(trip.getStartLatitude());
        mViewModel.startLongitude.setValue(trip.getStartLongitude());
        mViewModel.destinationAddress.setValue(trip.getDestinationAddress());
        mViewModel.destinationLatitude.setValue(trip.getDestinationLatitude());
        mViewModel.destinationLongitude.setValue(trip.getDestinationLongitude());
        String date = parseTimeStamp(trip.getDateTime());
        String[] arrayToGetDate = date.split(",");
        mViewModel.startDate.setValue("" + arrayToGetDate[0]);
        mViewModel.startTime.setValue("" + arrayToGetDate[1]);
    }

    private String parseTimeStamp(long postDate) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(postDate);
        String date = DateFormat.format("dd-MM-yyy ,hh:mm", calendar).toString();
        return date;
    }

    private void navigateToNotes(Trip trip){
        Log.i("myTrip", trip.toString());
        NotesFragment notesFragment = NotesFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.UPDATE_TRIP, trip);
        bundle.putBoolean(Constants.IS_NAVIGATE_TO_UPDATE, isNavigateToUpdate);
        notesFragment.setArguments(bundle);

        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, notesFragment)
                .commit();
    }

}