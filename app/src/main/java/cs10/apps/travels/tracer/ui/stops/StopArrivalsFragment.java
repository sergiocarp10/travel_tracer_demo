package cs10.apps.travels.tracer.ui.stops;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cs10.apps.common.android.CS_Fragment;
import cs10.apps.travels.tracer.R;
import cs10.apps.travels.tracer.Utils;
import cs10.apps.travels.tracer.adapter.LocatedArrivalAdapter;
import cs10.apps.travels.tracer.databinding.FragmentArrivalsBinding;
import cs10.apps.travels.tracer.db.DynamicQuery;
import cs10.apps.travels.tracer.db.MiDB;
import cs10.apps.travels.tracer.model.Parada;
import cs10.apps.travels.tracer.model.Viaje;
import cs10.apps.travels.tracer.model.roca.ArriboTren;
import cs10.apps.travels.tracer.model.roca.HorarioTren;
import cs10.apps.travels.tracer.model.roca.RamalSchedule;
import cs10.apps.travels.tracer.ui.service.ServiceDetail;
import cs10.apps.travels.tracer.viewmodel.HomeVM;
import cs10.apps.travels.tracer.viewmodel.LocatedArrivalVM;
import cs10.apps.travels.tracer.viewmodel.LocationVM;

public class StopArrivalsFragment extends CS_Fragment {
    private FragmentArrivalsBinding binding;
    private LocatedArrivalAdapter adapter;

    // ViewModel
    private LocatedArrivalVM locatedArrivalVM;
    private LocationVM locationVM;
    private HomeVM homeVM;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentArrivalsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new LocatedArrivalAdapter(new LinkedList<>(), arriboTren -> {
            onServiceSelected(arriboTren.getServiceId(), arriboTren.getRamal());
            return null;
        });

        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.setAdapter(adapter);

        locatedArrivalVM = new ViewModelProvider(this).get(LocatedArrivalVM.class);
        locationVM = new ViewModelProvider(requireActivity()).get(LocationVM.class);
        homeVM = new ViewModelProvider(requireActivity()).get(HomeVM.class);

        locatedArrivalVM.getStop().observe(getViewLifecycleOwner(), parada -> {
            // binding.pbar.setVisibility(View.VISIBLE);
            homeVM.isLoading().postValue(true);
            binding.tvTitle.setText(getString(R.string.next_ones_in, parada.getNombre()));
            locatedArrivalVM.recalculate(locationVM, homeVM);
            fillData(parada);
        });

        locatedArrivalVM.getProximity().observe(getViewLifecycleOwner(), prox -> {
            binding.tvSubtitle.setText(getString(R.string.proximity_porcentage, prox * 100));
        });

        locatedArrivalVM.getGoingTo().observe(getViewLifecycleOwner(), goingTo -> {
            binding.walkingIcon.setVisibility(goingTo ? View.VISIBLE : View.GONE);
        });

        locatedArrivalVM.getArrivals().observe(getViewLifecycleOwner(), arrivals -> {
            int ogSize = adapter.getItemCount();
            adapter.setList(arrivals);

            if (ogSize == 0) adapter.notifyItemRangeInserted(0, arrivals.size());
            else if (ogSize == adapter.getItemCount()) adapter.notifyItemRangeChanged(0, arrivals.size());
            else adapter.notifyDataSetChanged();

            // binding.pbar.setVisibility(View.GONE);
            homeVM.isLoading().postValue(false);
        });

        locationVM.getLocation().observe(getViewLifecycleOwner(), location -> {
            Double maxD = homeVM.getMaxDistance().getValue();
            if (maxD != null) locatedArrivalVM.recalculate(location, maxD);
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // get arguments
        Bundle args = getArguments();

        if (args != null) {
            int pos = args.getInt("pos");
            Parada parada = homeVM.getStop(pos);
            locatedArrivalVM.setStop(parada);
        }
    }

    private void fillData(Parada parada) {

        doInBackground(() -> {
            MiDB miDB = MiDB.getInstance(getContext());
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int m = calendar.get(Calendar.MINUTE);
            int now = hour * 60 + m;

            String stopName = parada.getNombre();
            List<Viaje> arrivals = DynamicQuery.getNextBusArrivals(getContext(), stopName);
            List<RamalSchedule> trenes = DynamicQuery.getNextTrainArrivals(getContext(), stopName);

            for (RamalSchedule tren : trenes) {
                ArriboTren v = new ArriboTren();
                int target = tren.getHour() * 60 + tren.getMinute();
                HorarioTren end = miDB.servicioDao().getFinalStation(tren.getService());

                v.setTipo(1);
                v.setRamal(tren.getRamal());
                v.setStartHour(tren.getHour());
                v.setStartMinute(tren.getMinute());
                v.setServiceId(tren.getService());
                v.setNombrePdaFin(Utils.simplify(end.getStation()));
                v.setNombrePdaInicio(tren.getCabecera());
                v.setRecorrido(miDB.servicioDao().getRecorridoUntil(tren.getService(), now, target));
                v.setRecorridoDestino(miDB.servicioDao().getRecorridoFrom(tren.getService(), target));
                v.setEndHour(end.getHour());
                v.setEndMinute(end.getMinute());
                v.restartAux();
                arrivals.add(v);
            }

            Collections.sort(arrivals);

            doInForeground(() -> locatedArrivalVM.getArrivals().postValue(arrivals));
        });
    }

    private void onServiceSelected(long id, String ramal) {
        Parada actual = locatedArrivalVM.getStop().getValue();
        if (actual == null) return;

        Intent intent = new Intent(getActivity(), ServiceDetail.class);
        intent.putExtra("station", actual.getNombre());
        intent.putExtra("ramal", ramal);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
