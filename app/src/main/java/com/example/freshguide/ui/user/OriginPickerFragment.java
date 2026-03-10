package com.example.freshguide.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshguide.R;
import com.example.freshguide.database.AppDatabase;
import com.example.freshguide.model.entity.OriginEntity;
import com.example.freshguide.ui.adapter.GenericListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Checklist 5.3: Returns selected originId to RoomDetailFragment via setFragmentResult.
 */
public class OriginPickerFragment extends Fragment {

    public static final String RESULT_KEY = "origin_picker_result";
    public static final String KEY_ORIGIN_ID = "origin_id";

    private GenericListAdapter adapter;
    private List<OriginEntity> origins = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_origin_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new GenericListAdapter();
        RecyclerView recycler = view.findViewById(R.id.recycler_origins);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        Executors.newSingleThreadExecutor().execute(() -> {
            origins = db.originDao().getAllSync();
            List<GenericListAdapter.Item> items = new ArrayList<>();
            for (OriginEntity o : origins) {
                items.add(new GenericListAdapter.Item(o.id, o.name,
                        o.code != null ? o.code : ""));
            }
            requireActivity().runOnUiThread(() -> adapter.setItems(items));
        });

        adapter.setOnActionListener(new GenericListAdapter.OnActionListener() {
            @Override
            public void onEdit(int position, int id) {
                // In origin picker, "edit" button acts as "select"
                selectOrigin(id, view);
            }

            @Override
            public void onDelete(int position, int id) {
                selectOrigin(id, view);
            }
        });
    }

    private void selectOrigin(int originId, View view) {
        Bundle result = new Bundle();
        result.putInt(KEY_ORIGIN_ID, originId);
        getParentFragmentManager().setFragmentResult(RESULT_KEY, result);
        Navigation.findNavController(view).popBackStack();
    }
}
