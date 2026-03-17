package com.example.freshguide.ui.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.freshguide.R;
import com.example.freshguide.model.entity.FacilityEntity;
import com.example.freshguide.viewmodel.RoomDetailViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoomDetailFragment extends BottomSheetDialogFragment {

    public static final String ARG_ROOM_ID = "roomId";

    private RoomDetailViewModel viewModel;
    private int roomId;
    private boolean isCampusArea;
    private final ExecutorService imageExecutor = Executors.newSingleThreadExecutor();
    private String latestImageUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            View sheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (sheet != null) {
                sheet.setBackgroundResource(R.drawable.bg_room_detail_sheet);
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(sheet);
                behavior.setSkipCollapsed(true);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Checklist 5.2: receive data from args
        roomId = requireArguments().getInt(ARG_ROOM_ID, -1);
        isCampusArea = requireArguments().getBoolean("isCampusArea", false);
        viewModel = new ViewModelProvider(this).get(RoomDetailViewModel.class);

        TextView tvName = view.findViewById(R.id.tv_room_name);
        TextView tvSubtitle = view.findViewById(R.id.tv_room_subtitle);
        TextView tvType = view.findViewById(R.id.tv_room_type);
        TextView tvDescription = view.findViewById(R.id.tv_room_description);
        TextView tvFacilities = view.findViewById(R.id.tv_facilities);
        ImageView ivRoomImage = view.findViewById(R.id.iv_room_image);
        TextView tvImagePlaceholder = view.findViewById(R.id.tv_image_placeholder);
        View btnDirections = view.findViewById(R.id.btn_get_directions);
        ImageButton btnBookmark = view.findViewById(R.id.btn_room_bookmark);
        NavController nav = NavHostFragment.findNavController(this);

        btnBookmark.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Favorites coming soon", Toast.LENGTH_SHORT).show());

        viewModel.getRoom().observe(getViewLifecycleOwner(), room -> {
            if (room == null) return;
            tvName.setText(room.name);
            tvSubtitle.setText(buildSubtitle(room.code, room.location));

            if (room.type != null && !room.type.isBlank()) {
                tvType.setVisibility(View.VISIBLE);
                tvType.setText(room.type.toUpperCase(Locale.getDefault()));
            } else {
                tvType.setVisibility(View.GONE);
            }

            String description = room.description != null ? room.description : "";
            if (description.isBlank()) {
                description = "No description available.";
            }
            tvDescription.setText(description);

            loadRoomImage(room.cachedImagePath, room.imageUrl, ivRoomImage, tvImagePlaceholder);
        });

        viewModel.getFacilities().observe(getViewLifecycleOwner(), facilities -> {
            if (facilities == null || facilities.isEmpty()) {
                tvFacilities.setText("No facilities listed");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (FacilityEntity f : facilities) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(f.name);
            }
            tvFacilities.setText(sb.toString());
        });

        viewModel.getError().observe(getViewLifecycleOwner(), err -> {
            if (err == null) return;

            if (isCampusArea) {
                Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
                nav.navigate(R.id.homeFragment);
                return;
            }

            Snackbar.make(view, err, Snackbar.LENGTH_LONG).show();
        });

        // Checklist 5.3: navigate to origin picker, receive result back
        btnDirections.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("roomId", roomId);
            nav.navigate(R.id.action_roomDetail_to_originPicker, args);
        });

        // Listen for origin selection result (set by OriginPickerFragment)
        getParentFragmentManager().setFragmentResultListener(
                OriginPickerFragment.RESULT_KEY, getViewLifecycleOwner(),
                (requestKey, result) -> {
                    int originId = result.getInt(OriginPickerFragment.KEY_ORIGIN_ID, -1);
                    if (originId != -1) {
                        Bundle dirArgs = new Bundle();
                        dirArgs.putInt("roomId", roomId);
                        dirArgs.putInt("originId", originId);
                        nav.navigate(R.id.action_roomDetail_to_directions, dirArgs);
                    }
                });

        if (roomId != -1) {
            viewModel.loadRoom(roomId);
        }
    }

    private String buildSubtitle(String code, String location) {
        String c = code != null ? code.trim() : "";
        String l = location != null ? location.trim() : "";
        if (!l.isEmpty() && !c.isEmpty()) {
            return l + " • " + c;
        }
        if (!l.isEmpty()) {
            return l;
        }
        if (!c.isEmpty()) {
            return c;
        }
        return "Location details unavailable";
    }

    private void loadRoomImage(String cachedImagePath, String imageUrl, ImageView imageView, TextView placeholderView) {
        if (cachedImagePath != null && !cachedImagePath.trim().isEmpty()) {
            File cachedFile = new File(cachedImagePath);
            if (cachedFile.exists()) {
                Bitmap cachedBitmap = BitmapFactory.decodeFile(cachedFile.getAbsolutePath());
                if (cachedBitmap != null) {
                    imageView.setImageBitmap(cachedBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    placeholderView.setVisibility(View.GONE);
                    return;
                }
            }
        }

        latestImageUrl = imageUrl;

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageView.setImageBitmap(null);
            imageView.setVisibility(View.VISIBLE);
            placeholderView.setVisibility(View.VISIBLE);
            return;
        }

        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(null);
        placeholderView.setVisibility(View.VISIBLE);

        imageExecutor.execute(() -> {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            Bitmap bitmap = null;

            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.connect();

                inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception ignored) {
                bitmap = null;
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception ignored) {
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }

            Bitmap finalBitmap = bitmap;
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) return;
                if (latestImageUrl == null || !latestImageUrl.equals(imageUrl)) return;

                if (finalBitmap != null) {
                    imageView.setImageBitmap(finalBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    placeholderView.setVisibility(View.GONE);
                } else {
                    imageView.setImageBitmap(null);
                    imageView.setVisibility(View.VISIBLE);
                    placeholderView.setVisibility(View.VISIBLE);
                }
            });
        });
    }

}
